/*
 * Script to test all the implemented ticket functionalities
 */

 /*
 * Tests the ticket representation function
 */
DO
$$
DECLARE
    ticket_id INT = 1;
    subject TEXT = 'Ticket subject test';
    description TEXT = 'Ticket description test';
    employee_state_id INT = 1;
    user_state_name_expected TEXT = 'On execution';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Ticket item representation test |---';

    ticket_rep = ticket_item_representation(ticket_id, subject, description, employee_state_id);
    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', subject) AND
        assert_json_value(ticket_rep, 'description', description) AND
        assert_json_value(ticket_rep, 'userState', user_state_name_expected)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new ticket
 */
DO
$$
DECLARE
    id BIGINT;
    subject TEXT = 'Ticket subject test';
    description TEXT = 'Ticket description test';
    person_name TEXT = 'Daniela Gomes';
    person_email TEXT = 'dani@isel.com';
    qr_hash TEXT = '5abd4089b7921fd6af09d1cc1cbe5220';
    expected_user_state TEXT = 'Waiting analysis';
    expected_employee_state TEXT = 'To assign';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Ticket creation test |---';

    CALL create_ticket(ticket_rep, qr_hash, subject, description, person_name, person_email);
    id = ticket_rep->>'id';
    IF (
        assert_json_is_not_null(ticket_rep, 'id') AND
        assert_json_value(ticket_rep, 'subject', subject) AND
        assert_json_value(ticket_rep, 'description', description) AND
        assert_json_value(ticket_rep, 'userState', expected_user_state) AND
        assert_json_value(ticket_rep, 'employeeState', expected_employee_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
    -- Remove sequence inc
    IF (id = 1) THEN
        ALTER SEQUENCE ticket_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('ticket_id_seq', (SELECT last_value FROM ticket_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new ticket with an unknown hash, throws unknown_room_device
 */
DO
$$
DECLARE
    subject TEXT = 'Ticket subject test';
    description TEXT = 'Ticket description test';
    user_id UUID = '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3';
    qr_hash TEXT = 'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Creation ticket, throws unknown_room_device |---';

    CALL create_ticket(subject, description, user_id, qr_hash, ticket_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unknown_room_device') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Test updating subject and description
 */
DO
$$
DECLARE
    ticket_id BIGINT = 6;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    new_subject TEXT = 'Ticket new subject test';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Update subject and description test |---';

    CALL update_ticket(ticket_rep, ticket_id, person_id, new_subject, new_description);

    IF (
        assert_json_is_not_null(ticket_rep, 'id') AND
        assert_json_value(ticket_rep, 'subject', new_subject) AND
        assert_json_value(ticket_rep, 'description', new_description)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Test updating just subject
 */
DO
$$
DECLARE
    ticket_id BIGINT = 3;
    new_subject TEXT = 'Ticket new subject test';
    current_description TEXT = 'Descrição de mesa partida';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Update subject test |---';

    CALL update_ticket(ticket_id, ticket_rep, new_subject, NULL);
    ROLLBACK;
    IF (
        assert_json_is_not_null(ticket_rep, 'id') AND
        assert_json_value(ticket_rep, 'subject', new_subject) AND
        assert_json_value(ticket_rep, 'description', current_description)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Test updating just description
 */
DO
$$
DECLARE
    ticket_id BIGINT = 3;
    current_subject TEXT = 'Mesa partida';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Update description test |---';

    CALL update_ticket(ticket_id, ticket_rep, NULL, new_description);
    IF (
        assert_json_is_not_null(ticket_rep, 'id') AND
        assert_json_value(ticket_rep, 'subject', current_subject) AND
        assert_json_value(ticket_rep, 'description', new_description)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests updating an non existent ticket, throw ticket_not_found
 */
DO
$$
DECLARE
    ticket_id BIGINT = -1;
    new_subject TEXT = 'Ticket new subject test';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update ticket, throws ticket_not_found test |---';

    CALL update_ticket(ticket_id, ticket_rep, new_subject, new_description);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests updating an concluded ticket, throw ticket_being_fixed_or_concluded
 */
DO
$$
DECLARE
    ticket_id BIGINT = 5;
    new_subject TEXT = 'Ticket new subject test';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update ticket, throws ticket_being_fixed_or_concluded test |---';

    CALL update_ticket(ticket_id, ticket_rep, new_subject, new_description);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_being_fixed_or_concluded') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of an archived ticket, throw cant_update_archived_ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    ticket_new_state INT = 6;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws cant_update_archived_ticket test |---';

    CALL change_ticket_state(ticket_id, ticket_new_state, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'cant_update_archived_ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of an ticket to the same state, throw impossible_state_transition
 */
DO
$$
DECLARE
    ticket_id BIGINT = 2;
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws impossible_state_transition test |---';

    CALL change_ticket_state(ticket_id, ticket_new_state, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'impossible_state_transition') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing ticket state
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manel | Employee
    ticket_new_state INT = 6;
    ticket_expected_subject TEXT = 'Fuga de água';
    ticket_expected_desc TEXT = 'A sanita está a deixar sair água por baixo';
    ticket_expected_state TEXT = 'Completed';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Changing ticket state to Concluded |---';
    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);
    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', ticket_expected_subject) AND
        assert_json_value(ticket_rep, 'description', ticket_expected_desc) AND
        assert_json_value(ticket_rep, 'employeeState', ticket_expected_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests changing state of an archived ticket, throw cant_update_archived_ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 6;
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws cant_update_archived_ticket test |---';

    CALL change_ticket_state(ticket_id, ticket_new_state, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'cant_update_archived_ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of an ticket to the same state, throw impossible_state_transition
 */
DO
$$
DECLARE
    ticket_id BIGINT = 2;
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws impossible_state_transition test |---';

    CALL change_ticket_state(ticket_id, ticket_new_state, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'impossible_state_transition') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of a not found ticket, throw ticket_not_found
 */
DO
$$
DECLARE
    ticket_id BIGINT = -1;
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state throws ticket_not_found test |---';

    CALL change_ticket_state(ticket_id, ticket_new_state, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests get ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 5;
    subject TEXT = 'Corrimão danificado';
    description TEXT = 'Descrição de corrimão danificado';
    employeeState TEXT = 'Refused';
    userState TEXT = 'Refused';
    possibleTransitions JSON[];
    commentsCollectionSize INT = 0;
    returned_value JSON;
    ticket_rep JSON;
    comments_rep JSON;
BEGIN
    possibleTransitions = array_append(possibleTransitions, json_build_object('id', 8, 'name', 'Concluded'));

    RAISE INFO '---| Get ticket test |---';

    returned_value = get_ticket(ticket_id, 10, 0);
    ticket_rep = returned_value->>'ticket';
    comments_rep = returned_value->>'ticketComments';
    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', subject) AND
        assert_json_value(ticket_rep, 'description', description) AND
        assert_json_is_not_null(ticket_rep, 'creationTimestamp') AND
        assert_json_value(ticket_rep, 'employeeState', employeeState) AND
        assert_json_value(ticket_rep, 'userState', userState) AND
        assert_json_value(comments_rep, 'collectionSize', commentsCollectionSize::TEXT) AND
        assert_json_is_not_null(returned_value, 'person')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get ticket, throw ticket_not_found
 */
DO
$$
DECLARE
    ticket_id BIGINT = -1;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Test get ticket, throws ticket_not_found test |---';

    ticket_rep = get_ticket(ticket_id, 10, 0);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests get user tickets
 */
DO
$$
DECLARE
    user_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    tickets_rep JSON;
    tickets_expected INT = 4;
BEGIN
    RAISE INFO '---| Get user tickets test |---';

    tickets_rep = get_tickets(user_id, 1000, 0);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get manager tickets
 */
DO
$$
DECLARE
    manager_id UUID = 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057';
    tickets_rep JSON;
    tickets_expected INT = 5;
BEGIN
    RAISE INFO '---| Get manager tickets test |---';

    tickets_rep = get_tickets(manager_id, 1000, 0);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get admin tickets
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 7;
BEGIN
    RAISE INFO '---| Get admin tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get tickets from a specific company
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 5;
    company_name TEXT = 'ISEL';
BEGIN
    RAISE INFO '---| Get specific company tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0, company_name := company_name);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get tickets from a specific building
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 4;
    building_name TEXT = 'A';
BEGIN
    RAISE INFO '---| Get specific tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0, building_name := building_name);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get tickets from a specific room
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 3;
    room_name TEXT = 'Biblioteca';
BEGIN
    RAISE INFO '---| Get specific room tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0, room_name := room_name);
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get tickets from a specific category
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 3;
    category_name TEXT = 'canalization';
BEGIN
    RAISE INFO '---| Get specific category tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0, category_name := category_name);

    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get tickets with search option
 */
DO
$$
DECLARE
    admin_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    tickets_rep JSON;
    tickets_expected INT = 5;
BEGIN
    RAISE INFO '---| Get specific category tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0, search := 'o');
    IF (
        assert_json_value(tickets_rep, 'collectionSize', tickets_expected::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests set ticket employee
 */
DO
$$
DECLARE
    employee_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb';
    ticket_id BIGINT = 4;
    expected_user_state TEXT = 'On execution';
    expected_employee_state TEXT = 'On execution';
    ticket_rep JSON;
    returned_value JSON;

BEGIN
    RAISE INFO '---| Set ticket employee test |---';

    CALL set_ticket_employee(employee_id, ticket_id, returned_value);
    ticket_rep = returned_value->>'ticket';
    IF (
        assert_json_value(ticket_rep, 'userState', expected_user_state) AND
        assert_json_value(ticket_rep, 'employeeState', expected_employee_state) AND
        assert_json_is_not_null(returned_value, 'person')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests set ticket employee, throws ticket_not_found
 */
DO
$$
DECLARE
    employee_id UUID = 'e85c73aa-7869-4861-a1cc-ca30d7c84123';
    ticket_id BIGINT = '-1';
    returned_value JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Test set ticket employee, throws ticket_not_found test |---';

    CALL set_ticket_employee(employee_id, ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests set ticket employee, throws missing_necessary_skill
 */
DO
$$
DECLARE
    employee_id UUID = 'e85c73aa-7869-4861-a1cc-ca30d7c84123';
    ticket_id BIGINT = '4';
    returned_value JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Test set ticket employee, throws missing_necessary_skill test |---';

    CALL set_ticket_employee(employee_id, ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'missing_necessary_skill') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests set ticket employee, throws missing_necessary_skill
 */
DO
$$
DECLARE
    employee_id UUID = 'e85c73aa-7869-4861-a1cc-ca30d7c84123';
    ticket_id BIGINT = 2;
    returned_value JSON;
    ex_constraint TEXT;

BEGIN
    RAISE INFO '---| Test set ticket employee, throws already_have_an_employee test |---';

    CALL set_ticket_employee(employee_id, ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'already_have_an_employee') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Remove ticket employee
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    expected_employee_state TEXT = 'Waiting for new employee';
    expected_user_state TEXT = 'Not Started';
    returned_value JSON;
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Remove employee ticket test |---';

    CALL remove_ticket_employee(ticket_id, returned_value);
    ticket_rep = returned_value->>'ticket';
    IF (
        assert_json_value(ticket_rep, 'employeeState', expected_employee_state) AND
        assert_json_value(ticket_rep, 'userState', expected_user_state) AND
        assert_json_is_not_null(returned_value, 'person')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests set ticket employee, throws ticket_not_found
 */
DO
$$
DECLARE
    ticket_id BIGINT = '-1';
    returned_value JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Remove ticket employee, throws ticket_not_found test |---';

    CALL remove_ticket_employee(ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests set ticket employee, throws must_have_employee
 */
DO
$$
DECLARE
    ticket_id BIGINT = '4';
    returned_value JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Remove employee ticket test, throws must_have_employee test |---';

    CALL remove_ticket_employee(ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'must_have_employee') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests set ticket employee, throws must_be_a_running_ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = '6';
    returned_value JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Remove employee ticket test, throws must_be_a_running_ticket test |---';

    CALL remove_ticket_employee(ticket_id, returned_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'must_be_a_running_ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests add ticket rate
 */
DO
$$
DECLARE
    employee_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    ticket_id BIGINT = 7;
    rate_value INT = 5;
    expected_subject TEXT = 'Porta partida';
    expected_description TEXT = 'Descrição de porta partida';
    expected_employeeState TEXT = 'Concluded';
    expected_userState TEXT = 'Completed';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Add ticket rate test |---';

    CALL add_ticket_rate(employee_id, ticket_id, rate_value, ticket_rep);
    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', expected_subject) AND
        assert_json_value(ticket_rep, 'description', expected_description) AND
        assert_json_value(ticket_rep, 'employeeState', expected_employeeState) AND
        assert_json_value(ticket_rep, 'userState', expected_userState) AND
        assert_json_value(ticket_rep, 'rate', rate_value::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests add ticket rate, throws ticket_not_found
 */
DO
$$
DECLARE
    employee_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    ticket_id BIGINT = '-1';
    rate_value INT = 5;
    ex_constraint TEXT;
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Tests add ticket rate, throws ticket_not_found test |---';

    CALL add_ticket_rate(employee_id, ticket_id, rate_value, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'ticket_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests add ticket rate, throws invalid_access_exception
 */
/*DO
$$
DECLARE
    employee_id UUID = 'e85c73aa-7869-4861-a1cc-ca30d7c8499b';
    ticket_id BIGINT = 7;
    rate_value INT = 5;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Add ticket rate test, throws invalid_access_exception test |---';

    CALL add_ticket_rate(employee_id, ticket_id, rate_value, ticket_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'invalid_access_exception') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;*/