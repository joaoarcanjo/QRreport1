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
    user_state_expected TEXT = 'Waiting analysis';
    employee_state_expected TEXT = 'To assign';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Ticket item representation test |---';

    ticket_rep = ticket_item_representation(ticket_id, subject, description, employee_state_id);

    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', subject) AND
        assert_json_value(ticket_rep, 'description', description) AND
        assert_json_value(ticket_rep, 'userState', user_state_expected) AND
        assert_json_value(ticket_rep, 'employeeState', employee_state_expected)
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
 * Test updating subject and description
 */
DO
$$
DECLARE
    ticket_id BIGINT = 2;
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
    ticket_id BIGINT = 2;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    new_subject TEXT = 'Ticket new subject test';
    current_description TEXT = 'Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Update subject test |---';

    CALL update_ticket(ticket_rep, ticket_id, person_id, new_subject, null);

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
    ticket_id BIGINT = 2;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    current_subject TEXT = 'Infiltração na parede';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Update description test |---';

    CALL update_ticket(ticket_rep, ticket_id, person_id, null, new_description);
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
 * Tests updating an non existent ticket, throw resource-not-found
 */
DO
$$
DECLARE
    ticket_id BIGINT = 9999;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    new_subject TEXT = 'Ticket new subject test';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update ticket, throws resource-not-found test |---';

    CALL update_ticket(ticket_rep, ticket_id, person_id, new_subject, new_description);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;

        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests updating an concluded ticket, throw fixing-ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    new_subject TEXT = 'Ticket new subject test';
    new_description TEXT = 'Ticket new description test';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update ticket, throws fixing-ticket test |---';

    CALL update_ticket(ticket_rep, ticket_id, person_id, new_subject, new_description);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        raise info '%', ex_constraint;
        IF (ex_constraint = 'fixing-ticket') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of an archived ticket, throw archived-ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 3;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    ticket_new_state INT = 1;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws archived-ticket test |---';

    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);
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
 * Tests changing state of an ticket to the same state, throw resource-not-found
 */
DO
$$
DECLARE
    ticket_id BIGINT = 1;
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | Admin
    ticket_new_state INT = 4;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws resource-not-found test |---';

    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;

        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '%', ex_constraint;
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
 * Tests changing state of an archived ticket, throw archived-ticket
 */
DO
$$
DECLARE
    ticket_id BIGINT = 3;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manel | Employee
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws archived-ticket test |---';

    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);
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
 * Tests changing state of an ticket to the same state, throw resource-permission-denied
 */
DO
$$
DECLARE
    ticket_id BIGINT = 2;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manel | Employee
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state, throws resource-permission-denied test |---';

    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;

        IF (ex_constraint = 'resource-permission-denied') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing state of a not found ticket, throw resource-not-found
 */
DO
$$
DECLARE
    ticket_id BIGINT = -1;
    person_id UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manel | Employee
    ticket_new_state INT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Changing state throws resource-not-found test |---';

    CALL change_ticket_state(ticket_rep, ticket_id, person_id, ticket_new_state);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
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
    user_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    ticket_id BIGINT = 1;
    subject TEXT = 'Fuga de água';
    description TEXT = 'A sanita está a deixar sair água por baixo';
    employeeState TEXT = 'Fixing';
    userState TEXT = 'Fixing';
    returned_value JSON;
    ticket_rep JSON;
    comments_rep JSON;
BEGIN

    RAISE INFO '---| Get ticket test |---';

    returned_value = get_ticket(ticket_id, user_id, 10, 0, 'DESC');
    ticket_rep = returned_value->>'ticket';
    IF (
        assert_json_value(ticket_rep, 'id', ticket_id::TEXT) AND
        assert_json_value(ticket_rep, 'subject', subject) AND
        assert_json_value(ticket_rep, 'description', description) AND
        assert_json_is_not_null(ticket_rep, 'creationTimestamp') AND
        assert_json_value(ticket_rep, 'employeeState', employeeState) AND
        assert_json_value(ticket_rep, 'userState', userState)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get ticket, throw resource-not-found
 */

DO
$$
DECLARE
    ticket_id BIGINT = -1;
    user_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Test get ticket, throws resource-not-found test |---';

    ticket_rep = get_ticket(ticket_id, user_id, 10, 0, 'DESC');
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;

        IF (ex_constraint = 'resource-not-found') THEN
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
    user_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    tickets_rep JSON;
    tickets_expected_size INT = 1;
BEGIN
    RAISE INFO '---| Get user tickets test |---';

    tickets_rep = get_tickets(user_id, 1000, 0);
    tickets_expected_size = tickets_rep -> 'ticketsCollectionSize';
    IF (
        assert_json_value(tickets_rep, 'ticketsCollectionSize', tickets_expected_size::TEXT)
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
    admin_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    tickets_rep JSON;
    tickets_expected_size INT = 3;
BEGIN
    RAISE INFO '---| Get admin tickets test |---';

    tickets_rep = get_tickets(admin_id, 1000, 0);
    tickets_expected_size = tickets_rep -> 'ticketsCollectionSize';
    IF (
        assert_json_value(tickets_rep, 'ticketsCollectionSize', tickets_expected_size::TEXT)
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
    admin_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_id BIGINT = 2;
    expected_user_state TEXT = 'Not started';
    expected_employee_state TEXT = 'Not started';
    ticket_rep JSON;
    returned_value JSON;

BEGIN
    RAISE INFO '---| Set ticket employee test |---';

    CALL set_ticket_employee(returned_value, admin_id, employee_id, ticket_id);
    raise info '%', returned_value;
    ticket_rep = returned_value->>'ticket';
    IF (
        assert_json_value(ticket_rep, 'userState', expected_user_state) AND
        assert_json_value(ticket_rep, 'employeeState', expected_employee_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests set ticket employee, throws resource-not-found
 */
DO
$$
DECLARE
    employee_id UUID = 'c2b393be-1111-4111-1111-43765f111111';
    admin_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_id BIGINT = '-1';
    ticket_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Test set ticket employee, throws ticket_not_found test |---';

    CALL set_ticket_employee(ticket_rep, admin_id, employee_id, ticket_id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;

        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests set ticket employee, throws ticket-employee-skill-mismatch
 */
DO
$$
DECLARE
    employee_id UUID = 'c2b393be-1111-4111-1111-43765f111111';
    admin_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_id BIGINT = 2;
    ticket_rep JSON;
    ex_constraint TEXT;

BEGIN
    RAISE INFO '---| Test set ticket employee, throws already_have_an_employee test |---';

    CALL set_ticket_employee(ticket_rep, admin_id, employee_id, ticket_id);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        raise info '%', ex_constraint;
        IF (ex_constraint = 'ticket-employee-skill-mismatch') THEN
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
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_id BIGINT = 1;
    expected_employee_state TEXT = 'To assign';
    expected_user_state TEXT = 'Waiting analysis';
    returned_value JSON;
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Remove employee ticket test |---';

    CALL remove_ticket_employee(returned_value, ticket_id, person_id);
    ticket_rep = returned_value->'ticket';
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
 * Tests set ticket employee, throws resource-not-found
 */
DO
$$
DECLARE
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    ticket_rep JSON;
    ticket_id BIGINT = '-1';
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Remove ticket employee, throws resource-not-found test |---';

    CALL remove_ticket_employee(ticket_rep, ticket_id, person_id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        raise info '%', ex_constraint;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests remove ticket employee, throws resource-permission-denied
 */
DO
$$
DECLARE
    person_id UUID = 'd1ad1c02-9e4f-476e-4234-c56ae8aa8765';
    ticket_rep JSON;
    ticket_id BIGINT = '1';
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Remove employee ticket test, throws must_have_employee test |---';

    CALL remove_ticket_employee(ticket_rep, ticket_id, person_id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        raise info '%', ex_constraint;
        IF (ex_constraint = 'resource-permission-denied') THEN
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
    person_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    ticket_id BIGINT = 3;
    rate_value INT = 5;
    expected_subject TEXT = 'Sanita entupida';
    expected_description TEXT = 'A sanita não permite que realizemos descargas de água.';
    expected_employeeState TEXT = 'Archived';
    expected_userState TEXT = 'Archived';
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Add ticket rate test |---';

    CALL add_ticket_rate(ticket_rep, ticket_id, person_id, rate_value);

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
 * Tests add ticket rate, throws resource-not-found
 */

DO
$$
DECLARE
    person_id UUID = '0a8b83ec-7675-4467-91e5-33e933441eee';
    ticket_id BIGINT = '-1';
    rate_value INT = 5;
    ex_constraint TEXT;
    ticket_rep JSON;
BEGIN
    RAISE INFO '---| Tests add ticket rate, throws ticket_not_found test |---';

    CALL add_ticket_rate(ticket_rep, ticket_id, person_id, rate_value);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;