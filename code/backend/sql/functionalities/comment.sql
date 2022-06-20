/*
 * Comment functionalities
 */

/*
 * Auxiliary function to return the comment item representation
 */
CREATE OR REPLACE FUNCTION comment_item_representation(c_id BIGINT, comment TEXT, c_timestamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', c_id, 'comment', comment, 'timestamp', c_timestamp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new comment
 * Returns the comment representation
 * Throws exception when the ticket is archived or when no rows were affected.
 */
CREATE OR REPLACE PROCEDURE create_comment(comment_rep OUT JSON, person_id UUID, ticket_id BIGINT, ticket_comment TEXT)
AS
$$
DECLARE
    comment_id BIGINT; comment_timestamp TIMESTAMP;
BEGIN
    IF EXISTS (
        SELECT id FROM TICKET WHERE employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')
        AND id = ticket_id
    ) THEN
        RAISE 'archived-ticket';
    ELSE
        comment_id = (SELECT MAX(id) FROM COMMENT WHERE ticket = ticket_id) + 1;
        IF (comment_id IS NULL) THEN
            comment_id = 1;
        END IF;
        INSERT INTO COMMENT (id, comment, person, ticket) VALUES (comment_id, ticket_comment, person_id, ticket_id)
        RETURNING timestamp INTO comment_timestamp;

        IF (comment_id IS NULL) THEN
            RAISE 'unknown-error-writing-resource' USING DETAIL = 'writing';
        END IF;
        comment_rep = comment_item_representation(comment_id, ticket_comment, comment_timestamp);
    END IF;
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Gets a specific comment
 * Returns the comment representation
 * Throws exception when the comment id does not exist
 */
CREATE OR REPLACE FUNCTION get_comment(comment_id BIGINT, ticket_id BIGINT)
RETURNS JSON
AS
$$
DECLARE
    person_id UUID; person_name TEXT; person_phone TEXT; person_email TEXT; comment TEXT; comment_timestamp TIMESTAMP;
BEGIN
    SELECT person.id, person.name, person.phone, person.email, comment.comment, comment.timestamp
    FROM COMMENT comment INNER JOIN PERSON person ON comment.person = person.id
    WHERE comment.id = comment_id AND comment.ticket = ticket_id
    INTO person_id, person_name, person_phone, person_email, comment, comment_timestamp;
    IF (NOT FOUND) THEN
        RAISE 'comment_not_found';
    END IF;
    RETURN (
        json_build_object('id', comment_id, 'comment', comment, 'timestamp', comment_timestamp,
            'person', person_item_representation(person_id, person_email, person_phone, person_email))
        );
END$$ LANGUAGE plpgsql;

/*
 * Get the comments of a ticket
 * Function to return the representation of all comments from ticket
 */
CREATE OR REPLACE FUNCTION get_comments(
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
    collection_size INT = 0;
    rec RECORD;
BEGIN
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
            json_build_object('id', rec.comment_id, 'comment', rec.comment, 'timestamp',  rec.comment_timestamp,
            'person', person_item_representation(rec.person_id, rec.name, rec.phone, rec.email)
        ));
        collection_size = collection_size + 1;
    END LOOP;
    RETURN json_build_object('comments', comments, 'collectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Update a specific comment
 * Returns the comment representation
 * Throws exception when cant change a comment that belongs to a archived ticket
 */
CREATE OR REPLACE PROCEDURE update_comment(
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
    IF NOT EXISTS (SELECT id FROM COMMENT WHERE id = comment_id AND ticket = ticket_id) THEN
        RAISE 'comment_not_found';
    END IF;
   IF ((SELECT employee_state FROM TICKET WHERE id = ticket_id)
            = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')) THEN
        RAISE 'cant_comment_archived_ticket';
    ELSE
        UPDATE COMMENT SET comment = new_comment WHERE id = comment_id AND ticket = ticket_id
        RETURNING timestamp INTO comment_timestamp;
        IF (comment_timestamp IS NULL) THEN
            RAISE 'unknown_error_updating_resource';
        END IF;
        comment_rep = json_build_object('id', comment_id, 'comment', new_comment, 'timestamp', comment_timestamp);
    END IF;
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Delete a specific comment
 * Returns the comment representation
 * Throws exception when try delete a comment  belongs to an archived ticket
 */
CREATE OR REPLACE PROCEDURE delete_comment(
    comment_id BIGINT,
    ticket_id BIGINT,
    comment_rep OUT JSON
)
AS
$$
DECLARE
    comment_timestamp TIMESTAMP; ticket_comment TEXT;
BEGIN
    IF NOT EXISTS (SELECT id FROM COMMENT WHERE id = comment_id AND ticket = ticket_id) THEN
        RAISE 'comment_not_found';
    END IF;
    IF ((SELECT employee_state FROM TICKET WHERE id = ticket_id)
            = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')) THEN
        RAISE 'cant_delete_comment_from_archived_ticket';
    ELSE
        DELETE FROM COMMENT WHERE id = comment_id AND ticket = ticket_id
        RETURNING CURRENT_TIMESTAMP, comment INTO comment_timestamp, ticket_comment;

        IF (comment_timestamp IS NULL) THEN
            RAISE 'unknown_error_deleting_resource';
        END IF;
        comment_rep = comment_item_representation(comment_id, ticket_comment, comment_timestamp);
    END IF;
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;