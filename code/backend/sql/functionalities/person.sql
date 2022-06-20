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
DROP PROCEDURE create_person(person_rep JSON, person_role SMALLINT,
person_name TEXT, person_email TEXT, person_password TEXT, person_phone TEXT);
CREATE OR REPLACE PROCEDURE create_person(
    person_rep OUT JSON,
    person_role SMALLINT,
    person_name TEXT,
    person_email TEXT,
    person_password TEXT,
    person_phone TEXT DEFAULT NULL
)AS
$$
DECLARE
    person_id UUID;
BEGIN
    INSERT INTO PERSON(name, email, password, phone)
    VALUES(person_name, person_email, person_password, person_phone) RETURNING id INTO person_id;

    INSERT INTO PERSON_ROLE(person, role) VALUES(person_id, person_role);

    person_rep = person_item_representation(person_id, person_name, person_phone, person_email);
END$$LANGUAGE plpgsql;