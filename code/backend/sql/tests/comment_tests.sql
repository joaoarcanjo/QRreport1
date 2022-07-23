/*
 * Script to test all the implemented comment functionalities
 */

/*
 * Tests the comment representation function
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    comment TEXT = 'Comment test';
    comment_rep JSON;
    comment_timestamp TIMESTAMP;
BEGIN
    RAISE INFO '---| Comment item representation test |---';
    comment_timestamp = CURRENT_TIMESTAMP;
    comment_rep = comment_item_representation(comment_id, comment, comment_timestamp);
    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', comment) AND
        assert_json_is_not_null(comment_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;


/*
 * Tests get comments
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    collection_size_expected INT = 2;
    comments_rep JSON;
BEGIN
    RAISE INFO '---| Get comments test |---';

    comments_rep = get_comments(ticket_id);

    IF (
        assert_json_is_not_null(comments_rep, 'comments') AND
        assert_json_value(comments_rep, 'collectionSize', collection_size_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get a comment
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    ticket_id BIGINT = 1;
    return_rep JSON;
    comment_rep JSON;
    comment_expected TEXT = 'Esta sanita não tem arranjo, vou precisar de uma nova.';
BEGIN
    RAISE INFO '---| Get comment test |---';

    return_rep = get_comment(comment_id := comment_id, ticket_id := ticket_id);
    comment_rep = return_rep ->> 'comment';

    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', comment_expected) AND
        assert_json_is_not_null(comment_rep, 'timestamp') AND
        assert_json_is_not_null(return_rep, 'person')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new comment
 */
DO
$$
DECLARE
    id BIGINT;
    ticket_id BIGINT = 1;
    comment TEXT = 'Comment comment test';
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment creation test |---';

    CALL create_comment(comment_rep, person_id, ticket_id, comment);
    id = comment_rep->>'id';
    IF (
        assert_json_is_not_null(comment_rep, 'id') AND
        assert_json_value(comment_rep, 'comment', comment) AND
        assert_json_is_not_null(comment_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;

    -- Remove sequence inc
    IF (id = 1) THEN
        ALTER SEQUENCE comment_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('comment_id_seq', (SELECT last_value FROM comment_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new comment, throws archived-ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 3;
    comment TEXT = 'Comment comment test';
    person_id UUID = '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3';
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Creation comment, throws archived-ticket |---';

    CALL create_comment(comment_rep, person_id, ticket_id, comment);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'archived-ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Test updating comment
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb';
    ticket_id BIGINT = 1;
    new_comment TEXT = 'Comment test';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment item representation test |---';
    CALL update_comment(comment_rep, comment_id, person_id, ticket_id, new_comment);
    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', new_comment) AND
        assert_json_is_not_null(comment_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests updating an comment from an archived ticket, throw archived-ticket
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb';
    ticket_id BIGINT = 3;
    new_comment TEXT = 'Comment test';
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update comment, throws archived-ticket |---';
    CALL update_comment(comment_rep, comment_id, person_id, ticket_id, new_comment);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'archived-ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Test comment deletion
 */
DO
$$
DECLARE
    comment_id BIGINT = 2;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_id BIGINT = 1;
    expected_comment TEXT = 'Tente fazer o possível para estancar a fuga.';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment deletion test |---';
    CALL delete_comment(comment_rep, comment_id, person_id, ticket_id);
    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', expected_comment) AND
        assert_json_is_not_null(comment_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests deleting an comment from an archived ticket, throw cant_comment_archived_ticket
 */
DO
$$
DECLARE
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    comment_id BIGINT = 3;
    ticket_id BIGINT = 3;
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update comment, throws cant_delete_comment_from_archived_ticket |---';
    CALL delete_comment(comment_rep, comment_id, person_id, ticket_id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        RAISE INFO '%', ex_constraint;
        IF (ex_constraint = 'archived-ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;
