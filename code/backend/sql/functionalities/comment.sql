/*
 * Comment functionalities
 */

/*
 * Auxiliary function to return the comment item representation
 */
CREATE OR REPLACE FUNCTION comment_item_representation (c_id BIGINT, comment TEXT, c_timestamp TIMESTAMP, c_person JSON)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', c_id, 'comment', comment, 'timestamp', c_timestamp, 'person', c_person);
END$$ LANGUAGE plpgsql;


/*
 * Auxiliary function to return the representation of all comments from ticket
 */
CREATE OR REPLACE FUNCTION list_of_comments (t_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON[]
AS
$$
DECLARE
    t_comments JSON[];
    rec RECORD;
BEGIN
    FOR rec IN
        SELECT c.id as comment_id, comment, c.timestamp as comment_timestamp, p.id as person_id, name, phone, email
        FROM COMMENT c INNER JOIN PERSON p ON c.person = p.id WHERE ticket = t_id ORDER BY c.id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        t_comments = array_append(t_comments,
            comment_item_representation(rec.comment_id, rec.comment, rec.comment_timestamp,
                person_item_representation(rec.person_id, rec.name, rec.phone, rec.email)));
    END LOOP;
    RETURN t_comments;
END$$ LANGUAGE plpgsql;