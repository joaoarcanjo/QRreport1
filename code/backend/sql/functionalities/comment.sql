/*
 * Comment functionalities
 */


/*
 * Auxiliary function to verify if a comment exists
 */
CREATE OR REPLACE FUNCTION comment_exists(comment_id BIGINT, person_id UUID, ticket_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT id FROM COMMENT WHERE id = comment_id AND person = person_id AND ticket = ticket_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'comment', HINT = comment_id;
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the comment item representation by its id
 */
CREATE OR REPLACE FUNCTION comment_item_representation(comment_id BIGINT)
RETURNS JSON
AS
$$
DECLARE ticket_comment TEXT; ticket_timestamp TIMESTAMP;
BEGIN
    SELECT comment, timestamp INTO ticket_comment, ticket_timestamp FROM COMMENT WHERE id = comment_id;
    RETURN json_build_object('id', comment_id, 'comment', ticket_comment, 'timestamp', ticket_timestamp);
END$$ LANGUAGE plpgsql;

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
    comment_id BIGINT;
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);

    -- Check if person is employee, and if so check if the ticket is his responsibility
    IF (EXISTS(SELECT person FROM PERSON_ROLE WHERE person = person_id
        AND role = (SELECT id FROM ROLE WHERE name = 'employee'))
    ) THEN
        PERFORM ticket_belongs_to_employee(ticket_id, person_id);
    END IF;

    INSERT INTO COMMENT (comment, person, ticket) VALUES (ticket_comment, person_id, ticket_id)
    RETURNING id INTO comment_id;

    comment_rep = comment_item_representation(comment_id);
END$$
-- SET default_transaction_isolation = 'serializable'
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
        RAISE 'resource-not-found';
    END IF;
    RETURN (
        json_build_object(
            'comment', comment_item_representation(comment_id, comment, comment_timestamp),
            'person', person_item_representation(person_id))
        );
END$$ LANGUAGE plpgsql;

/*
 * Get the comments of a ticket
 * Function to return the representation of all comments from ticket
 */
CREATE OR REPLACE FUNCTION get_comments(
    t_id BIGINT,
    direction TEXT DEFAULT 'DESC',
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL
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
        WHERE ticket = (SELECT parent_ticket FROM TICKET WHERE id = t_id) OR ticket = t_id
        ORDER BY
            CASE WHEN direction = 'DESC' THEN c.timestamp END DESC,
            CASE WHEN direction = 'ASC' THEN c.timestamp END ASC
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        comments = array_append(comments,
            json_build_object(
                'comment', comment_item_representation(rec.comment_id, rec.comment, rec.comment_timestamp),
                'person', person_item_representation(rec.person_id)
        ));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM COMMENT WHERE ticket = t_id;
    RETURN json_build_object('comments', comments, 'collectionSize', collection_size,
        'ticketState', get_ticket_state_name(t_id),
        'isTicketChild', is_child_ticket(t_id)
    );
END$$ LANGUAGE plpgsql;

/*
 * Update a specific comment
 * Returns the comment representation
 * Throws exception when cant change a comment that belongs to a archived ticket
 */
CREATE OR REPLACE PROCEDURE update_comment(
    comment_rep OUT JSON,
    comment_id BIGINT,
    person_id UUID,
    ticket_id BIGINT,
    new_comment TEXT
)
AS
$$
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    PERFORM comment_exists(comment_id, person_id, ticket_id);

    UPDATE COMMENT SET comment = new_comment
    WHERE id = comment_id AND ticket = ticket_id AND person = person_id AND comment != new_comment;

    comment_rep = comment_item_representation(comment_id);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Delete a specific comment
 * Returns the comment representation
 * Throws exception when the comment belongs to an archived ticket
 */
CREATE OR REPLACE PROCEDURE delete_comment(
    comment_rep OUT JSON,
    comment_id BIGINT,
    person_id UUID,
    ticket_id BIGINT
)
AS
$$
DECLARE cc TEXT; ctm TIMESTAMP;
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    PERFORM comment_exists(comment_id, person_id, ticket_id);

    DELETE FROM COMMENT WHERE id = comment_id AND ticket = ticket_id AND person = person_id
    RETURNING comment, timestamp INTO cc, ctm;

    comment_rep = comment_item_representation(comment_id, cc, ctm);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;