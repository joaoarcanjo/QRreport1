/**
  * Role functionalities
  */

/**
  * Auxiliary function to obtain a role id by giving a role name
  */
CREATE OR REPLACE FUNCTION get_role_id(role_name TEXT)
RETURNS INT
AS
$$
BEGIN
    RETURN (SELECT id FROM ROLE WHERE name = role_name);
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to obtain the role name by giving its id
  */
CREATE OR REPLACE FUNCTION get_role_name(role_id INT)
RETURNS TEXT
AS
$$
BEGIN
    RETURN (SELECT name FROM ROLE WHERE id = role_id);
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to obtain in an array the roles names of a person
  */
CREATE OR REPLACE FUNCTION get_person_roles(person_id UUID)
RETURNS TEXT[]
AS
$$
BEGIN
    RETURN (SELECT array_agg((SELECT name FROM ROLE WHERE id = role))
            FROM PERSON_ROLE WHERE person = person_id);
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to obtain in an array the roles names of a person
  */
CREATE OR REPLACE FUNCTION get_person_active_role(person_id UUID)
RETURNS TEXT
AS
$$
BEGIN
    RETURN (SELECT (SELECT name FROM ROLE WHERE id = active_role)
            FROM PERSON WHERE id = person_id);
END$$LANGUAGE plpgsql;


/**
  * Auxiliary function to obtain in an array the skills names of an employee
  */
CREATE OR REPLACE FUNCTION get_employee_skills(person_id UUID)
RETURNS TEXT[]
AS
$$
BEGIN
    RETURN (SELECT array_agg((SELECT name FROM CATEGORY WHERE id = p.category))
            FROM PERSON_SKILL p WHERE person = person_id);
END$$LANGUAGE plpgsql;