/*
 * Comment functionalities
 */

/*
 * Auxiliary function to return the comment item representation
 */
CREATE OR REPLACE FUNCTION comment_item_representation (c_id BIGINT, comment TEXT, person JSON, c_timestamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', c_id, 'comment', comment, 'timestamp', c_timestamp, 'person', person);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new comment
 * Returns the comment representation
 * Throws exception when the ticket is archived or when no rows affected.
 */
CREATE OR REPLACE PROCEDURE create_comment (
    person_id UUID,
    ticket_id BIGINT,
    ticket_comment TEXT,
    comment_rep OUT JSON
)
AS
$$
DECLARE
    comment_id BIGINT; comment_timestamp TIMESTAMP;
BEGIN
    IF (
        (SELECT employee_state FROM TICKET
        WHERE id = ticket_id FOR SHARE) = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')
    ) THEN
        RAISE 'cant_comment_archived_ticket';
    ELSE
        comment_id = (SELECT MAX(id) FROM COMMENT WHERE ticket = ticket_id) + 1;
        IF (comment_id IS NULL) THEN
            comment_id = 1;
        END IF;
        INSERT INTO COMMENT (id, comment, person, ticket) VALUES (comment_id, ticket_comment, person_id, ticket_id)
         RETURNING timestamp INTO comment_timestamp;
        IF (comment_id IS NULL) THEN
            RAISE 'unknown_error_creating_comment_resource';
        END IF;
        comment_rep = json_build_object('id', comment_id, 'comment', ticket_comment, 'timestamp', comment_timestamp);
    END IF;
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific comment
 * Returns the comment representation
 * Throws exception when the comment id does not exist
 */
CREATE OR REPLACE FUNCTION get_comment (comment_id BIGINT, ticket_id BIGINT)
RETURNS JSON
AS
$$
DECLARE
    person_id UUID; person_name TEXT; person_phone TEXT; person_email TEXT; comment TEXT; comment_timestamp TIMESTAMP;
BEGIN
    SELECT p.id, p.name, p.phone, p.email, c.comment, c.timestamp
    FROM COMMENT c INNER JOIN PERSON p ON c.person = p.id
    WHERE c.id = comment_id AND c.ticket = ticket_id FOR SHARE
    INTO person_id, person_name, person_phone, person_email, comment, comment_timestamp;
    IF (NOT FOUND) THEN
        RAISE 'comment_not_found';
    END IF;

    RETURN comment_item_representation(
        comment_id, comment,
        person_item_representation(person_id, person_email, person_phone, person_email), comment_timestamp);
END$$ LANGUAGE plpgsql;

/*
 * Function to return the representation of all comments from ticket
 */
CREATE OR REPLACE FUNCTION get_comments (
    t_id BIGINT,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL,
    direction TEXT DEFAULT 'DESC'
)
RETURNS JSON
AS
$$
DECLARE
    comments JSON[];
    collection_size INT;
    rec RECORD;
BEGIN
    SELECT COUNT(*) FROM COMMENT WHERE ticket = t_id INTO collection_size;
    FOR rec IN
        SELECT c.id as comment_id, comment, c.timestamp as comment_timestamp, p.id as person_id, name, phone, email
        FROM COMMENT c INNER JOIN PERSON p ON c.person = p.id
        WHERE ticket = t_id
        ORDER BY
            CASE WHEN direction='DESC' THEN c.timestamp END DESC,
            CASE WHEN direction='ASC' THEN c.timestamp END ASC
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        comments = array_append(comments,
            comment_item_representation(rec.comment_id, rec.comment,
                person_item_representation(rec.person_id, rec.name, rec.phone, rec.email), rec.comment_timestamp));
    END LOOP;
    RETURN json_build_object('comments', comments, 'collectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Update comment
 */

CREATE OR REPLACE PROCEDURE update_comment (
    comment_id BIGINT,
    ticket_id BIGINT,
    new_comment TEXT,
    comment_rep OUT JSON
)
AS
$$
DECLARE
    comment_timestamp TIMESTAMP;
BEGIN
    IF (
        (SELECT employee_state FROM TICKET
        WHERE id = ticket_id FOR SHARE) != (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')
    ) THEN
        UPDATE COMMENT SET comment = new_comment WHERE id = comment_id AND ticket = ticket_id
        RETURNING timestamp INTO comment_timestamp;
        IF (comment_timestamp IS NULL) THEN
            RAISE 'unknown_error_updating_resource';
        END IF;
        comment_rep = json_build_object('id', comment_id, 'comment', new_comment, 'timestamp', comment_timestamp);
    ELSE
        RAISE 'cant_comment_archived_ticket';
    END IF;
END$$ LANGUAGE plpgsql;

/*
 * Delete ticket
 */
CREATE OR REPLACE PROCEDURE delete_comment (
    comment_id BIGINT,
    ticket_id BIGINT,
    comment_rep OUT JSON
)
AS
$$
DECLARE
    comment_timestamp TIMESTAMP; ticket_comment TEXT;
BEGIN
    IF NOT EXISTS (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived' AND id = (SELECT employee_state FROM TICKET
        WHERE id = ticket_id)
    ) THEN
        DELETE FROM COMMENT WHERE id = comment_id AND ticket = ticket_id
        RETURNING CURRENT_TIMESTAMP, comment INTO comment_timestamp, ticket_comment;

        IF (comment_timestamp IS NULL) THEN
            RAISE 'unknown_error_deleting_resource';
        END IF;
        comment_rep = json_build_object('id', comment_id, 'comment', ticket_comment, 'timestamp', comment_timestamp);
    ELSE
        RAISE 'cant_delete_comment_from_archived_ticket';
    END IF;
END$$ LANGUAGE plpgsql;