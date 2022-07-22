/**
  * State functionalities
  */

/**
  * Auxiliary function to obtain the state of a person
  */
CREATE OR REPLACE FUNCTION get_person_state(person_id UUID)
RETURNS TEXT
AS
$$
BEGIN
    RETURN (SELECT state FROM PERSON WHERE id = person_id);
END$$LANGUAGE plpgsql;

/**
 * Function to obtain all employee possible states
 */
CREATE OR REPLACE FUNCTION get_employee_states(limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD; states JSON[]; collection_size INT = 0;
BEGIN
    FOR rec IN SELECT id, name FROM employee_state LIMIT limit_rows OFFSET skip_rows
    LOOP
        states = array_append(states, json_build_object('id', rec.id, 'name', rec.name));
    END LOOP;
    SELECT COUNT(name) INTO collection_size FROM employee_state;

    RETURN json_build_object('employeeStates', states, 'statesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;