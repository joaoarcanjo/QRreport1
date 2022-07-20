/*
 * Script to test all the implemented person functionalities
 */

/*
 * Tests the creation of a new person
 */
DO
$$
DECLARE
    id UUID;
    role TEXT = 'user';
    name TEXT = 'Jervásio';
    email TEXT = 'jer@gmail.com';
    password TEXT = 'password';
    phone TEXT = NULL;
    person_rep JSON;
BEGIN
    RAISE INFO '---| Person creation test |---';

    CALL create_person(person_rep, role, name, email, password, phone);

    IF (assert_json_is_not_null(person_rep, 'id') AND
        assert_json_value(person_rep, 'name', name) AND
        assert_json_value(person_rep, 'email', email) AND
        NOT assert_json_is_not_null(person_rep, 'phone') AND
        assert_json_value(person_rep, 'roles', array_to_json(ARRAY['user'])::TEXT) AND
        assert_json_value(person_rep, 'state', 'active')) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the creation of a person with a non unique email
 */
DO
$$
DECLARE
    id UUID;
    role TEXT = 'user'; -- user
    name TEXT = 'Diogo';
    email TEXT = 'diogo@qrreport.com';
    password TEXT = 'password';
    pcompany INT = 1;
    skill INT = 1;
    phone TEXT = NULL;
    person_rep JSON;
    type TEXT;
BEGIN
    RAISE INFO '---| Person creation test with non unique email |---';

    CALL create_person(person_rep, role, name, email, password, phone, pcompany, skill);
    id = person_rep->>'id';
    RAISE INFO '-> Test failed!';
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS type = MESSAGE_TEXT;
        IF (type = 'unique-constraint') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the get person function
 */
DO
$$
DECLARE
    req_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo | manager(ISEL) and admin
    res_id UUID = '1f6c1014-b029-4a75-b78c-ba09c8ea474d'; -- João Arcanjo | guest
    person_rep JSON;
BEGIN
    RAISE INFO '---| Get person test |---';

    person_rep = get_person(req_id, res_id);

     IF (assert_json_is_not_null(person_rep, 'person') AND
        assert_json_is_not_null(person_rep, 'personTickets')) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the valid update of a person name, phone and email
 */
DO
$$
DECLARE
    id UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    name TEXT = 'Nobinhu';
    phone TEXT = '897895651';
    email TEXT = 'nobibi@gmail.com';
    person_rep JSON;
BEGIN
    RAISE INFO '---| Update person name, phone and email test |---';

    CALL update_person(person_rep, id, name, phone, email, NULL);

    IF (assert_json_value(person_rep, 'id', id::TEXT) AND
        assert_json_value(person_rep, 'name', name) AND
        assert_json_value(person_rep, 'phone', phone) AND
        assert_json_value(person_rep, 'email', email)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the deletion of a user account
 */
DO
$$
DECLARE
    id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    name TEXT = 'b555b6fc-b904-4bd9-8c2b-4895738a437c';
    email TEXT = 'b555b6fc-b904-4bd9-8c2b-4895738a437c@deleted.com';
    person_rep JSON;
BEGIN
    RAISE INFO '---| User deletion test |---';

    CALL delete_user(person_rep, id);

    IF (assert_json_value(person_rep, 'id', id::TEXT) AND
        assert_json_value(person_rep, 'name', name) AND
        NOT assert_json_is_not_null(person_rep, 'phone') AND
        assert_json_value(person_rep, 'email', email) AND
        assert_json_value(person_rep, 'state', 'inactive')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the dismissal of an employee
 */
DO
$$
DECLARE
    employee UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manuel
    reason TEXT = 'Left the company for bad conduct.';
    company BIGINT = 1; -- ISEL
    person_rep JSON;
BEGIN
    RAISE INFO '---| Employee dismissal test |---';

    CALL fire_person(person_rep, employee, company, reason);

    IF (assert_json_value(person_rep, 'id', employee::TEXT) AND
        assert_json_value(person_rep, 'state', 'inactive') AND
        assert_json_value(person_rep, 'reason', reason)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the ban of a person
 */
DO
$$
DECLARE
    manager UUID = 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'; -- Pedro Miguens
    puser UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c'; -- Francisco Ludovico
    reason TEXT = 'Insulting through reports.';
    person_rep JSON;
BEGIN
    RAISE INFO '---| Person ban test |---';

    CALL ban_person(person_rep, manager, puser, reason);

    IF (assert_json_value(person_rep, 'id', puser::TEXT) AND
        assert_json_value(person_rep, 'state', 'banned') AND
        assert_json_value(person_rep, 'reason', reason)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the addition of a role to a person
 */
DO
$$
DECLARE
    person_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c'; -- Francisco Ludovico - user
    role TEXT = 'manager';
    company BIGINT = 1; -- ISEL
    person_rep JSON;
BEGIN
    RAISE INFO '---| Person role addition test |---';

    CALL add_role_to_person(person_rep, person_id, role, company);

    IF (assert_json_value(person_rep, 'id', person_id::TEXT) AND
        assert_json_value(person_rep, 'roles', array_to_json(ARRAY['user', 'manager'])::TEXT) AND
        assert_json_value(person_rep, 'companies', array_to_json(ARRAY['ISEL'])::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the removal of a role from a person
 */
DO
$$
DECLARE
    person_id UUID = '4b341de0-65c0-4526-8898-24de463fc315'; -- Diogo Novo - manager(ISEL)/admin
    role TEXT = 'admin';
    person_rep JSON;
BEGIN
    RAISE INFO '---| Person role removal test |---';

    CALL remove_role_from_person(person_rep, person_id, role);

    IF (assert_json_value(person_rep, 'id', person_id::TEXT) AND
        assert_json_value(person_rep, 'roles', array_to_json(ARRAY['manager'])::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the removal of a role from a person, when there is only one role
 */
DO
$$
DECLARE
    person_id UUID = 'b555b6fc-b904-4bd9-8c2b-4895738a437c'; -- Francisco Ludovico - user
    role TEXT = 'user';
    person_rep JSON;
    type TEXT;
BEGIN
    RAISE INFO '---| Person with only one role, removal test |---';

    CALL remove_role_from_person(person_rep, person_id, role);

    RAISE '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS type = MESSAGE_TEXT;
        IF (type = 'minimum-roles') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the addition of a skill to an employee
 */
DO
$$
DECLARE
    employee UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manuel
    skill INT = 2; -- electricity
    person_rep JSON;
BEGIN
    RAISE INFO '---| Person skill addition test |---';

    CALL add_skill_to_employee(person_rep, employee, skill);

    IF (assert_json_value(person_rep, 'id', employee::TEXT) AND
        assert_json_value(person_rep, 'skills', array_to_json(ARRAY['water', 'electricity'])::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the removal of a skill from an employee, when there is only one skill
 */
DO
$$
DECLARE
    employee UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manuel
    skill INT = 1; -- water
    person_rep JSON;
    type TEXT;
BEGIN
    RAISE INFO '---| Employee with only one skill, removal test |---';

    CALL remove_skill_from_employee(person_rep, employee, skill);
    RAISE '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS type = MESSAGE_TEXT;
        IF (type = 'minimum-skills') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the addition of a company to an employee/manager
 */
DO
$$
DECLARE
    employee UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manuel
    company BIGINT = 2; -- IST
    person_rep JSON;
BEGIN
    RAISE INFO '---| Employee company addition test |---';

    CALL assign_person_to_company(person_rep, employee, company);

    IF (assert_json_value(person_rep, 'id', employee::TEXT) AND
        assert_json_value(person_rep, 'companies', array_to_json(ARRAY['ISEL', 'IST'])::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

DO
$$
DECLARE
    employee UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- Zé Manuel
    company BIGINT = 2; -- IST
    person_rep JSON;
BEGIN
    RAISE INFO '---| Employee company addition test |---';

    CALL assign_person_to_company(person_rep, employee, company);

    IF (assert_json_value(person_rep, 'id', employee::TEXT) AND
        assert_json_value(person_rep, 'companies', array_to_json(ARRAY['ISEL', 'IST'])::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;