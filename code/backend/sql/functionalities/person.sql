/*
 * Person functionalities
 */

/*
 * Auxiliary function to return the person item representation
 */
CREATE OR REPLACE FUNCTION person_item_representation(p_id UUID, name TEXT, phone TEXT, email TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', p_id, 'name', name, 'phone', phone, 'email', email);
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the person item representation
 */
CREATE OR REPLACE FUNCTION person_item_representation(person_id UUID)
RETURNS JSON
AS
$$
DECLARE
    pname TEXT; pphone TEXT; pemail TEXT; pstate TEXT;
BEGIN
    SELECT name, phone, email, state INTO pname, pphone, pemail, pstate FROM PERSON WHERE id = person_id;
    RETURN json_build_object('id', person_id, 'name', pname, 'phone', pphone, 'email', pemail, 'roles',
        get_person_roles(person_id), 'state', pstate, 'skills', get_employee_skills(person_id));
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the person details representation
 */
CREATE OR REPLACE FUNCTION person_details_representation(person_id UUID, pcompany BIGINT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    pname TEXT; pphone TEXT; pemail TEXT; pstate TEXT; ptimestamp TIMESTAMP; preason TEXT; pbanned_by UUID;
    person_rep JSON;
BEGIN
    SELECT name, phone, email, state, timestamp, reason, banned_by
    INTO pname, pphone, pemail, pstate, ptimestamp, preason, pbanned_by FROM PERSON WHERE id = person_id;
    IF (pbanned_by IS NOT NULL) THEN
        person_rep = person_item_representation(pbanned_by);
    ELSEIF (pcompany IS NOT NULL) THEN
        SELECT state, reason, timestamp INTO pstate, preason, ptimestamp
        FROM PERSON_COMPANY WHERE person = person_id AND company = pcompany;
    END IF;

    RETURN json_build_object('id', person_id, 'name', pname, 'phone', pphone, 'email', pemail, 'roles', get_person_roles(person_id),
        'state', pstate, 'timestamp', ptimestamp, 'reason', preason, 'bannedBy', person_rep,
        'skills', get_employee_skills(person_id), 'companies', get_person_companies(person_id));
END$$ LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a specific person already exists.
  * Returns the person id (UUID) if the person exists and null otherwise.
  */
CREATE OR REPLACE FUNCTION person_exists(person_email TEXT)
RETURNS UUID
AS
$$
DECLARE
    person_id UUID;
BEGIN
    SELECT id INTO person_id FROM PERSON WHERE email = person_email;
    IF (person_id IS NULL) THEN
        RETURN NULL;
    END IF;
    RETURN person_id;
END$$ LANGUAGE plpgsql;

/**
  * Creates a new person and defines her role
  */
CREATE OR REPLACE PROCEDURE create_person(
    person_rep OUT JSON,
    prole TEXT,
    person_name TEXT,
    person_email TEXT,
    person_password TEXT,
    person_phone TEXT DEFAULT NULL,
    pcompany BIGINT DEFAULT NULL,
    skill BIGINT DEFAULT NULL
)AS
$$
DECLARE
    person_id UUID; ex_constraint TEXT; role_id INT; ignore JSON;
BEGIN
    role_id = get_role_id(prole);
    IF (NOT EXISTS(SELECT id FROM ROLE WHERE id = role_id)) THEN
         RAISE 'resource-not-found' USING DETAIL = 'role', HINT = prole;
    END IF;

    INSERT INTO PERSON(name, email, password, phone, active_role)
    VALUES(person_name, person_email, person_password, person_phone, role_id) RETURNING id INTO person_id;

    IF (prole = 'employee' OR prole = 'manager') THEN
        IF (pcompany IS NULL) THEN RAISE 'employee-manager-company'; END IF;
        CALL assign_person_to_company(ignore, person_id, pcompany);
        IF (prole = 'employee') THEN
            IF (skill IS NULL) THEN RAISE 'employee-skill'; END IF;
            CALL add_skill_to_employee(ignore, person_id, skill);
        END IF;
    END IF;

    INSERT INTO PERSON_ROLE(person, role) VALUES(person_id, role_id);

    person_rep = person_details_representation(person_id);
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'unique_person_email') THEN
            RAISE 'unique-constraint' USING DETAIL = 'email', HINT = person_email;
        END IF;
END$$LANGUAGE plpgsql;

/**
* Returns employees with the skill necessary to resolve the ticket
*/
CREATE OR REPLACE FUNCTION get_specific_employees(ticket_id BIGINT, limit_rows INT, skip_rows INT)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    persons JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id FROM PERSON WHERE id IN (SELECT person FROM person_company
            WHERE company IN (SELECT company FROM building b
            WHERE b.id IN (SELECT building FROM room r
            WHERE r.id IN (SELECT room FROM ticket WHERE id = ticket_id)))) AND id IN (SELECT PERSON FROM PERSON_SKILL
            WHERE category = (SELECT category FROM DEVICE WHERE id = (SELECT device FROM ticket WHERE id = ticket_id)))
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        persons = array_append(persons, person_item_representation(rec.id));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM PERSON WHERE id IN (SELECT person FROM person_company
            WHERE company IN (SELECT company FROM building b
            WHERE b.id IN (SELECT building FROM room r
            WHERE r.id IN (SELECT room FROM ticket WHERE id = ticket_id)))) AND id IN (SELECT PERSON FROM PERSON_SKILL
            WHERE category = (SELECT category FROM DEVICE WHERE id = (SELECT device FROM ticket WHERE id = ticket_id)));

RETURN json_build_object('persons', persons, 'personsCollectionSize', collection_size);
END$$LANGUAGE plpgsql;

/**
 *  Returns users with a specific role from a specific company
 */
CREATE OR REPLACE FUNCTION get_company_persons(person_id UUID, company_id BIGINT, role_name TEXT, limit_rows INT, skip_rows INT)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    persons JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id FROM PERSON WHERE id != person_id
            AND id IN (SELECT person FROM person_company WHERE company = company_id)
            AND id IN (SELECT person FROM person_role WHERE role = (SELECT id FROM role WHERE name = role_name))
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        persons = array_append(persons, person_item_representation(rec.id));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM PERSON WHERE id != person_id
            AND id IN (SELECT person FROM person_company WHERE company = company_id)
            AND id IN (SELECT person FROM person_role WHERE role = (SELECT id FROM role WHERE name = role_name));

RETURN json_build_object('persons', persons, 'personsCollectionSize', collection_size);
END$$LANGUAGE plpgsql;

/**
  * Returns a representation with all the persons
  */
CREATE OR REPLACE FUNCTION get_persons(person_id UUID, is_manager BOOL, limit_rows INT, skip_rows INT)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    persons JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id FROM PERSON WHERE id != person_id AND
            CASE WHEN (is_manager) THEN
                -- Get the employees that share the same company with the manager
                id IN (SELECT person FROM PERSON_COMPANY WHERE company IN (
                        SELECT company FROM PERSON_COMPANY WHERE person = person_id)
                    EXCEPT (SELECT person FROM PERSON_ROLE WHERE role = get_role_id('manager')))
            ELSE TRUE END
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        persons = array_append(persons, person_item_representation(rec.id));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM PERSON WHERE id != person_id AND
        CASE WHEN (is_manager) THEN
            id IN (SELECT person FROM PERSON_COMPANY WHERE company IN (
                        SELECT company FROM PERSON_COMPANY WHERE person = person_id)
                    EXCEPT (SELECT person FROM PERSON_ROLE WHERE role = get_role_id('manager')))
        ELSE TRUE END;
    RETURN json_build_object('persons', persons, 'personsCollectionSize', collection_size);
END$$LANGUAGE plpgsql;
-- Not tested, and not completed (filters)

/**
  * Gets a specific person details, req_person_id is the id of the person that requested the information
  * and res_person_id is the id of the person to retrieve the information, if are both the same, means that
  * the person is accessing his own profile.
  * Returns the person details, personal info, tickets reported(guest/user) or assigned(employee)
  * Throws exception when the person id doesn't exist or when doesn't have sufficient permissions
  */
CREATE OR REPLACE FUNCTION get_person(
    req_person_id UUID,
    res_person_id UUID
)
RETURNS JSON
AS
$$
DECLARE
    pstate TEXT; req_role TEXT; res_roles TEXT[]; is_profile BOOL = FALSE;
BEGIN
    req_role = get_person_active_role(req_person_id);
    res_roles = get_person_roles(res_person_id);

    CASE
        -- Equal ids means access to own profile OR admin accessing profile of another admin or manager
        WHEN (req_person_id = res_person_id OR (req_role = 'admin' AND ('admin' = ANY(res_roles) OR 'manager' = ANY(res_roles)))) THEN
            pstate = get_person_state(req_person_id);
            IF (req_person_id = res_person_id AND (pstate = 'banned' OR pstate = 'inactive')) THEN
                RAISE 'change-inactive-or-banned-person';
            END IF;
            is_profile = TRUE;
        WHEN (req_role = 'manager'AND ('admin' = ANY(res_roles) AND 'manager' != ANY(res_roles))) THEN
            -- Managers cannot access to data of other managers (different company) or admins
            RAISE 'resource-permission-denied';
        WHEN (req_role = 'manager' AND 'manager' = ANY(res_roles)
                  AND NOT EXISTS(SELECT company FROM PERSON_COMPANY WHERE person = req_person_id AND company IN
                      (SELECT company FROM PERSON_COMPANY WHERE person = res_person_id))) THEN
            -- A manager is trying to access a profile of a manager in a different company
            RAISE 'resource-permission-denied';
        ELSE
        -- A manager or admin is accessing the profile of an employee or guest/user
            -- Get person infos like in the profile
            -- Get person tickets, submitted in the case of a user, and assigned in case of an employee
            -- User - get tickets to be analysed first, then the fixing ones and then the completed
            -- Employee - get tickets to be started, then tickets fixing and then the completed
    END CASE;
    RETURN json_build_object(
        'person', person_details_representation(res_person_id),
        'personTickets', CASE WHEN (NOT is_profile) THEN get_tickets(res_person_id) END
    );
END$$LANGUAGE plpgsql;

/*
 * Updates a person
 * Returns the updated person item representation
 * Throws exception when the person id does not exist, when person has the state set to inactive/banned,
 * when all updatable parameters are null, when the unique constraint is violated(email) or when there is no row updated
 */

CREATE OR REPLACE PROCEDURE update_person(
    person_rep OUT JSON,
    person_id UUID,
    new_name TEXT,
    new_phone TEXT,
    new_email TEXT,
    new_password TEXT
)
AS
$$
DECLARE
    pname TEXT; pphone TEXT; pemail TEXT; pstate TEXT; ppassword TEXT; ex_constraint TEXT;
BEGIN
    SELECT name, phone, email, password, state
    INTO pname, pphone, pemail, ppassword, pstate
    FROM PERSON WHERE id = person_id;

    IF (pname IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (pstate = 'inactive' OR pstate = 'banned') THEN
        RAISE 'change-inactive-or-banned-person' USING DETAIL = 'update';
    END IF;

    IF (new_name IS NOT NULL AND new_name != pname) THEN
        UPDATE PERSON SET name = new_name WHERE id = person_id;
    END IF;

    IF (new_phone IS NOT NULL AND (pphone IS NULL OR new_phone != pphone)) THEN
        UPDATE PERSON SET phone = new_phone WHERE id = person_id;
    END IF;

    IF (new_email IS NOT NULL AND new_email != pemail) THEN
        UPDATE PERSON SET email = new_email WHERE id = person_id;
    END IF;

    IF (new_password IS NOT NULL AND new_password != ppassword) THEN
        UPDATE PERSON SET password = new_password WHERE id = person_id;
    END IF;

    person_rep = person_item_representation(person_id);
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'unique_person_email') THEN
            RAISE 'unique-constraint' USING DETAIL = 'email', HINT = new_email;
        END IF;
END$$ LANGUAGE plpgsql;
-- Repeatable read

/**
  * Deletes a specific person with the user role and replaces its information to a specific format
  * Returns the representation of the changes made
  * Throws exception if the person doesn't exist, if doesn't have the user role, if is inactive or banned and
  * if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE delete_user(person_rep OUT JSON, person_id UUID)
AS
$$
DECLARE
    pname TEXT; pphone TEXT; pemail TEXT; prole INT; pstate TEXT;
BEGIN
    SELECT name, phone, email, active_role, state INTO pname, pphone, pemail, prole, pstate FROM PERSON WHERE id = person_id;
    IF (pname IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (get_role_name(prole) != 'user') THEN
        RAISE 'user-deletion';
    ELSEIF (pstate = 'inactive' OR pstate = 'banned') THEN
        RAISE 'change-inactive-or-banned-person' USING DETAIL = 'delete';
    END IF;

    UPDATE PERSON SET name = person_id, email = person_id || '@deleted.com', phone = NULL,
         state = 'inactive', reason = 'User deleted account' WHERE id = person_id;
    IF (NOT FOUND) THEN
        RAISE 'unknown-error-writing-resource' USING DETAIL = 'deleting';
    END IF;
    person_rep = person_details_representation(person_id);
END$$LANGUAGE plpgsql;
-- Repeatable read

/**
  * Fires an employee
  * Returns the representation of the changes made
  * Throws exception if the person doesn't exist, if doesn't have the employee or manager/admin role, if is inactive< and
  * if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE fire_person(
    person_rep OUT JSON, employee_id UUID, pcompany BIGINT, fire_reason TEXT
)
AS
$$
DECLARE
    employee_role TEXT;
BEGIN
    employee_role = get_person_active_role(employee_id);
    IF (employee_role IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'employee', HINT = employee_id;
    ELSEIF (employee_role != 'employee' AND employee_role != 'manager') THEN
        RAISE 'person-dismissal';
    ELSEIF (EXISTS(SELECT state FROM PERSON_COMPANY WHERE company = pcompany AND person = employee_id AND state = 'inactive')) THEN
        RAISE 'change-inactive-or-banned-person' USING DETAIL = 'fire';
    END IF;

    UPDATE PERSON_COMPANY SET state = 'inactive', reason = fire_reason, timestamp = CURRENT_TIMESTAMP
    WHERE person = employee_id AND company = pcompany;

    person_rep = person_details_representation(employee_id, pcompany);
END$$LANGUAGE plpgsql;
-- Repeatable read

/**
  * Rehires an employee
  * Returns the representation of the changes made
  * Throws exception if the person doesn't exist, if doesn't have the employee and manager/admin role and
  * if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE rehire_person(person_rep OUT JSON, employee_id UUID, pcompany BIGINT)
AS
$$
DECLARE
    employee_role TEXT;
BEGIN
    employee_role = get_person_active_role(employee_id);
    IF (employee_role IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'employee', HINT = employee_id;
    ELSEIF (employee_role != 'employee' AND employee_role != 'manager') THEN
        RAISE 'person-dismissal';
    ELSEIF (EXISTS(SELECT state FROM PERSON_COMPANY WHERE company = pcompany AND person = employee_id AND state = 'active')) THEN
        person_rep = person_details_representation(employee_id, pcompany);
        RETURN;
    END IF;

    UPDATE PERSON_COMPANY SET state = 'active', timestamp = CURRENT_TIMESTAMP, reason = NULL
    WHERE person = employee_id AND company = pcompany;

    person_rep = person_details_representation(employee_id, pcompany);
END$$LANGUAGE plpgsql;
-- Repeatable read
-- Not tested

/**
  * Bans a person
  * Returns the representation of the changes made
  * Throws exception if the person to ban doesn't exist, if has an unauthorized role and if is inactive.
  */
CREATE OR REPLACE PROCEDURE ban_person(
    person_rep OUT JSON, req_person_id UUID, ban_person_id UUID, ban_reason TEXT
)
AS
$$
DECLARE
    ban_person_role TEXT; req_role TEXT;
BEGIN
    req_role = get_person_active_role(req_person_id);
    ban_person_role = get_person_active_role(ban_person_id);
    IF (ban_person_role IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person to ban', HINT = ban_person_id;
    ELSEIF (req_role = 'manager' AND (ban_person_role = 'employee' OR ban_person_role = 'manager' OR ban_person_role = 'admin')) THEN
        RAISE 'manager-ban-permission';
    ELSEIF (EXISTS(SELECT state FROM PERSON WHERE id = ban_person_id AND (state = 'banned' OR state = 'inactive'))) THEN
        RAISE 'change-inactive-or-banned-person' USING DETAIL = 'ban';
    END IF;

    UPDATE PERSON SET state = 'banned', reason = ban_reason, timestamp = CURRENT_TIMESTAMP, banned_by = req_person_id
    WHERE id = ban_person_id;

    person_rep = person_details_representation(ban_person_id);
END$$LANGUAGE plpgsql;
-- Repeatable read

/**
  * Unbans a person
  * Returns the representation of the changes made
  * Throws exception if the person to unban doesn't exist, if has a non authorized role and if is inactive.
  */

CREATE OR REPLACE PROCEDURE unban_person(
    person_rep OUT JSON, req_person_id UUID, unban_person_id UUID
)
AS
$$
DECLARE
    unban_person_role TEXT; req_role TEXT;
BEGIN
    req_role = get_person_active_role(req_person_id);
    unban_person_role = get_person_active_role(unban_person_id);
    IF (unban_person_role IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person to ban', HINT = unban_person_id;
    ELSEIF (req_role = 'manager' AND (unban_person_role = 'employee' OR unban_person_role = 'manager' OR unban_person_role = 'admin')) THEN
        RAISE 'manager-ban-permission';
    ELSEIF (EXISTS(SELECT state FROM PERSON WHERE id = unban_person_id AND state = 'inactive')) THEN
        RAISE 'change-inactive-or-banned-person' USING DETAIL = 'inactive';
    END IF;

    UPDATE PERSON SET state = 'active', timestamp = CURRENT_TIMESTAMP, reason = NULL, banned_by = NULL WHERE id = unban_person_id;
    IF (NOT FOUND) THEN
        RAISE 'unknown-error-writing-resource' USING DETAIL = 'unbanning';
    END IF;
    person_rep = person_details_representation(unban_person_id);
END$$LANGUAGE plpgsql;
-- Repeatable read
-- Not tested

/**
  * Adds a role to a person (admin only)
  * Returns the representation of the changes made
  * Throws exception if the person or role doesn't exist and if there was an error while updating to the new values.
  */

CREATE OR REPLACE PROCEDURE add_role_to_person(
    person_rep OUT JSON, person_id UUID, new_role TEXT, pcompany BIGINT DEFAULT NULL, skill BIGINT DEFAULT NULL
)
AS
$$
DECLARE rep JSON; role_id INT;
BEGIN
    role_id = get_role_id(new_role);
    IF (NOT EXISTS(SELECT id FROM PERSON WHERE id = person_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (NOT EXISTS(SELECT id FROM ROLE WHERE id = role_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'role', HINT = new_role;
    ELSEIF (NOT EXISTS(SELECT state FROM PERSON WHERE id = person_id AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'person';
    ELSEIF (NOT EXISTS(SELECT role FROM PERSON_ROLE WHERE person = person_id AND role = role_id)) THEN
        INSERT INTO PERSON_ROLE(person, role) VALUES (person_id, role_id);

        -- In case there is an employee or a manager, they need to be associated to a company
        IF ((new_role = 'employee' OR new_role = 'manager')) THEN
            CALL assign_person_to_company(rep, person_id, pcompany);
        END IF;
        IF (new_role = 'employee') THEN
            CALL add_skill_to_employee(rep, person_id, skill);
        END IF;
    END IF;
    person_rep = person_details_representation(person_id);
END$$LANGUAGE plpgsql;

/**
  * Remove a role from a person (admin only)
  * Returns the representation of the changes made
  * Throws exception if the person or role doesn't exist and if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE remove_role_from_person(person_rep OUT JSON, person_id UUID, remove_role TEXT)
AS
$$
DECLARE role_id INT;
BEGIN
    role_id = get_role_id(remove_role);
    IF (NOT EXISTS(SELECT id FROM PERSON WHERE id = person_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (NOT EXISTS(SELECT id FROM ROLE WHERE id = role_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'role', HINT = remove_role;
    ELSEIF (NOT EXISTS(SELECT state FROM PERSON WHERE id = person_id AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'person';
    ELSEIF ((SELECT COUNT(role) FROM PERSON_ROLE WHERE person = person_id) = 1) THEN
        RAISE 'minimum-roles';
    ELSEIF (EXISTS(SELECT role FROM PERSON_ROLE WHERE person = person_id AND role = role_id)) THEN
        DELETE FROM PERSON_ROLE WHERE person = person_id AND role = role_id;
        IF (remove_role = 'employee' OR remove_role = 'manager') THEN
            UPDATE PERSON_COMPANY SET state = 'inactive', timestamp = CURRENT_TIMESTAMP, reason = 'Role removed'
            WHERE person = person_id;
        END IF;
    END IF;
    person_rep = person_details_representation(person_id);
END$$LANGUAGE plpgsql;

/**
  * Adds a skill to an employee (manager/admin only)
  * Returns the representation of the changes made
  * Throws exception if the person or role doesn't exist and if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE add_skill_to_employee(person_rep OUT JSON, person_id UUID, skill BIGINT)
AS
$$
BEGIN
    IF (NOT EXISTS(SELECT id FROM PERSON WHERE id = person_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (NOT EXISTS(SELECT id FROM CATEGORY WHERE id = skill)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'skill', HINT = skill;
    ELSEIF (NOT EXISTS(SELECT state FROM CATEGORY WHERE id = skill AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'skill';
    ELSEIF (NOT EXISTS(SELECT state FROM PERSON WHERE id = person_id AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'person';
    ELSEIF (NOT EXISTS(SELECT person FROM PERSON_SKILL WHERE person = person_id AND category = skill)) THEN
        RAISE INFO 'OLAAA';
        INSERT INTO PERSON_SKILL(person, category) VALUES (person_id, skill);
    END IF;
    person_rep = person_item_representation(person_id);
END$$LANGUAGE plpgsql;

/**
  * Removes a skill from an employee (manager/admin only)
  * Returns the representation of the changes made
  * Throws exception if the person or role doesn't exist and if there was an error while updating to the new values.
  */
CREATE OR REPLACE PROCEDURE remove_skill_from_employee(person_rep OUT JSON, person_id UUID, skill BIGINT)
AS
$$
BEGIN
    IF (NOT EXISTS(SELECT id FROM PERSON WHERE id = person_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (NOT EXISTS(SELECT id FROM CATEGORY WHERE id = skill)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'skill', HINT = skill;
    ELSEIF (NOT EXISTS(SELECT state FROM PERSON WHERE id = person_id AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'person';
    ELSEIF ((SELECT COUNT(category) FROM PERSON_SKILL WHERE person = person_id) = 1) THEN
        RAISE 'minimum-skills';
    ELSEIF (EXISTS(SELECT person FROM PERSON_SKILL WHERE person = person_id AND category = skill)) THEN
        DELETE FROM PERSON_SKILL WHERE person = person_id AND category = skill;
    END IF;
    person_rep = person_item_representation(person_id);
END$$LANGUAGE plpgsql;

/**
  * Assigns an employee or a manager to a company (admin only)
  * Returns the representation of the changes made
  * Throws exception if the person or role doesn't exist.
  */
CREATE OR REPLACE PROCEDURE assign_person_to_company(person_rep OUT JSON, person_id UUID, pcompany BIGINT)
AS
$$
BEGIN
    IF (NOT EXISTS(SELECT id FROM PERSON WHERE id = person_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'person', HINT = person_id;
    ELSEIF (NOT EXISTS(SELECT id FROM COMPANY WHERE id = pcompany)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'company', HINT = pcompany;
    ELSEIF (NOT EXISTS(SELECT state FROM COMPANY WHERE id = pcompany AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'company';
    ELSEIF (NOT EXISTS(SELECT state FROM PERSON WHERE id = person_id AND state = 'active')) THEN
         RAISE 'inactive-resource' USING DETAIL = 'person';
    ELSEIF NOT('employee' = ANY(get_person_roles(person_id)) OR 'manager' = ANY(get_person_roles(person_id))) THEN
         RAISE 'company-persons';
    ELSEIF (NOT EXISTS(SELECT person FROM PERSON_COMPANY WHERE person = person_id AND company = pcompany)) THEN
        INSERT INTO PERSON_COMPANY(person, company) VALUES (person_id, pcompany);
    ELSEIF (EXISTS(SELECT person FROM PERSON_COMPANY WHERE person = person_id AND company = pcompany AND state = 'inactive')) THEN
        UPDATE PERSON_COMPANY SET state = 'active', reason = NULL WHERE person = person_id AND company = pcompany;
    END IF;

    person_rep = person_details_representation(person_id);
END$$LANGUAGE plpgsql;

/*
 * Switches the active role of a certain person
 */
CREATE OR REPLACE PROCEDURE switch_role(person_rep OUT JSON, person_id UUID, role TEXT)
AS
$$
BEGIN
    IF (NOT role = ANY(get_person_roles(person_id))) THEN
        RAISE 'invalid-role' USING DETAIL = 'switch-role';
    END IF;
    UPDATE PERSON SET active_role = get_role_id(role)
    WHERE id = person_id;
    person_rep = person_details_representation(person_id);
END$$LANGUAGE plpgsql;