/*
 * Authentication functionalities
 */

/**
  * Auxiliary function to get the buildings that a person manages inside a certain company
  */
CREATE OR REPLACE FUNCTION get_manager_buildings(person_id UUID, company_id BIGINT)
RETURNS BIGINT[] AS
$$
DECLARE
BEGIN
    RETURN (SELECT array_agg(id) FROM BUILDING WHERE company = company_id AND manager = person_id);
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to build the companies item for each authenticated person
  */
CREATE OR REPLACE FUNCTION get_person_auth_companies_item(person_id UUID)
RETURNS JSON[] AS
$$
DECLARE rec RECORD; companies JSON[];
BEGIN
    FOR rec IN
        SELECT company, state, (SELECT name FROM COMPANY WHERE id = pc.company) AS name
        FROM PERSON_COMPANY pc WHERE person = person_id
    LOOP
        companies = array_append(
            companies,
            json_build_object('id', rec.company, 'name', rec.name, 'state', rec.state,
                'manages', get_manager_buildings(person_id, rec.company))
        );
    END LOOP;
    RETURN companies;
END$$LANGUAGE plpgsql;

/**
  *  function to build the JSON object with the representation of an authenticated person
  */
CREATE OR REPLACE FUNCTION auth_representation(person_id UUID)
RETURNS JSON AS
$$
DECLARE
    pname TEXT; pphone TEXT; pemail TEXT; pstate TEXT; ptimestamp TIMESTAMP; preason TEXT;
BEGIN
    SELECT name, phone, email, state, timestamp, reason
    INTO pname, pphone, pemail, pstate, ptimestamp, preason
    FROM PERSON WHERE id = person_id;

    RETURN json_build_object('id', person_id, 'name', pname, 'phone', pphone, 'email', pemail,
        'activeRole', get_person_active_role(person_id), 'state', pstate, 'timestamp', ptimestamp, 'reason', preason,
        'skills', get_employee_skills(person_id), 'companies', get_person_auth_companies_item(person_id));
END$$LANGUAGE plpgsql;

/**
  * Gets a person credentials by email, in order to validate the password in the server side
  * Returns the email and the password in a JSON object
  * Throws exception when the provided email doesn't exist
  */
CREATE OR REPLACE FUNCTION get_credentials(person_email TEXT)
RETURNS JSON
AS
$$
DECLARE pw TEXT;
BEGIN
    SELECT password INTO pw FROM PERSON WHERE email = person_email;
    IF (pw IS NULL) THEN
        RAISE 'invalid-credentials';
    END IF;
    RETURN json_build_object('email', person_email, 'password', pw);
END$$LANGUAGE plpgsql;

/**
  * Gets the person details after the credentials have been validated on the server side
  * Returns the person details, personal info
  * Throws exception when the credentials are invalid
  */
CREATE OR REPLACE FUNCTION login(person_email TEXT)
RETURNS JSON
AS
$$
DECLARE
    person_id UUID; pstate TEXT;
BEGIN
    SELECT id, state INTO person_id, pstate FROM PERSON WHERE email = person_email;
    IF (pstate = 'banned' AND pstate = 'inactive') THEN
        RAISE 'inactive-or-banned-person-access';
    END IF;

    RETURN auth_representation(person_id);
END$$LANGUAGE plpgsql;

/**
  * Creates a new person in the system
  * Returns the authenticated person details
  * Throws exception if the provided email is not unique
  */
CREATE OR REPLACE PROCEDURE signup(person_rep OUT JSON, pname TEXT, pphone TEXT, pemail TEXT, ppassword TEXT)
AS
$$
DECLARE person_id UUID; role_id INT; ex_constraint TEXT;
BEGIN
    role_id = get_role_id('user');
    INSERT INTO PERSON(name, phone, email, password, active_role)
    VALUES (pname, pphone, pemail, ppassword, role_id) RETURNING id INTO person_id;

    INSERT INTO PERSON_ROLE(person, role) VALUES(person_id, role_id);

    person_rep = auth_representation(person_id);
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'unique_person_email') THEN
            RAISE 'unique-constraint' USING DETAIL = 'email', HINT = pemail;
        END IF;
END$$LANGUAGE plpgsql;