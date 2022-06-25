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
