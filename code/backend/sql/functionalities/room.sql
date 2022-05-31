/*
 * Room functionalities
 */

 /*
  * Auxiliary function to return the room item representation
  */
CREATE OR REPLACE FUNCTION room_item_representation(id BIGINT, name TEXT, state TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', id, 'name', name, 'state', state);
END$$ LANGUAGE plpgsql;