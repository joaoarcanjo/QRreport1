/*
 * Device functionalities
 */

/*
 * Auxiliary function to return the device item representation
 */
CREATE OR REPLACE FUNCTION device_item_representation (d_id BIGINT, d_name TEXT, d_state TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', d_id, 'name', d_name, 'state', d_state);
END$$ LANGUAGE plpgsql;