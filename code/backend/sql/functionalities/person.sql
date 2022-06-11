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