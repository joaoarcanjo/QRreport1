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
    person_rep JSON = json_build_object('name', 'Representation test');
    comment_rep JSON;
    comment_timestamp TIMESTAMP;
BEGIN
    RAISE INFO '---| Comment item representation test |---';
    comment_timestamp = CURRENT_TIMESTAMP;
    comment_rep = comment_item_representation(comment_id, comment, person_rep, comment_timestamp);
    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', comment) AND
        assert_json_is_not_null(comment_rep, 'timestamp') AND
        assert_json_is_not_null(comment_rep, 'person')
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
    ticket_id BIGINT = 2;
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
    ticket_id BIGINT = 2;
    comment_rep JSON;
    comment_expected TEXT = 'Comentário ao trabalho realizado em torneira avariada';
BEGIN
    RAISE INFO '---| Get comment test |---';

    comment_rep = get_comment(comment_id := comment_id, ticket_id := ticket_id);
    IF (
        assert_json_value(comment_rep, 'id', comment_id::TEXT) AND
        assert_json_value(comment_rep, 'comment', comment_expected) AND
        assert_json_is_not_null(comment_rep, 'timestamp') AND
        assert_json_is_not_null(comment_rep, 'person')
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
    ticket_id BIGINT = 2;
    comment TEXT = 'Comment comment test';
    person_id UUID = '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment creation test |---';

    CALL create_comment(person_id, ticket_id, comment, comment_rep);
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
END$$;

/*
 * Tests the creation of a new comment, throws cant_comment_archived_ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 6;
    comment TEXT = 'Comment comment test';
    person_id UUID = '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3';
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Creation comment, throws cant_comment_archived_ticket |---';

    CALL create_comment(person_id, ticket_id, comment, comment_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'cant_comment_archived_ticket') THEN
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
    ticket_id BIGINT = 1;
    new_comment TEXT = 'Comment test';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment item representation test |---';
    CALL update_comment(comment_id, ticket_id, new_comment, comment_rep);
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
 * Tests updating an comment from an archived ticket, throw cant_comment_archived_ticket
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    ticket_id BIGINT = 6;
    new_comment TEXT = 'Comment test';
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update comment, throws cant_comment_archived_ticket |---';
    CALL update_comment(comment_id, ticket_id, new_comment, comment_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'cant_comment_archived_ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Test deleting comment
 */
DO
$$
DECLARE
    comment_id BIGINT = 1;
    ticket_id BIGINT = 1;
    expected_comment TEXT = 'Comentário ao trabalho realizado em fuga de água';
    comment_rep JSON;
BEGIN
    RAISE INFO '---| Comment item representation test |---';
    CALL delete_comment(comment_id, ticket_id, comment_rep);
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
    comment_id BIGINT = 1;
    ticket_id BIGINT = 6;
    comment_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update comment, throws cant_delete_comment_from_archived_ticket |---';
    CALL delete_comment(comment_id, ticket_id, comment_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'cant_delete_comment_from_archived_ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;
