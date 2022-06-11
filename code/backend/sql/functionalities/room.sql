/*
 * Room functionalities
 */

 /*
  * Auxiliary function to return the room item representation
  */
CREATE OR REPLACE FUNCTION room_item_representation(id BIGINT, name TEXT, floor INT, state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', id, 'name', name, 'floor', floor, 'state', state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new room
 * Returns the room item representation
 * Throws exception when the building doesn't exists, when the name is already in use
 * or when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE create_room(
    company_id BIGINT,
    building_id BIGINT,
    room_name TEXT,
    room_floor INT,
    room_rep OUT JSON
)
AS
$$
DECLARE
    room_id BIGINT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    PERFORM id FROM BUILDING WHERE id = building_id AND company = company_id;
    IF (NOT FOUND) THEN
        RAISE 'building_not_found';
    END IF;

    IF EXISTS (SELECT id FROM ROOM WHERE building = building_id AND name = room_name) THEN
        RAISE 'unique_room_name' USING ERRCODE = 'unique_violation';
    END IF;
    INSERT INTO ROOM (name, floor, building) VALUES (room_name, room_floor, building_id)
    RETURNING id, state, timestamp INTO room_id, room_state, tmstamp;
    IF (room_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    room_rep = room_item_representation(room_id, room_name, room_floor, room_state, tmstamp);
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Update the name of a specific room
 * Returns the room item representation
 * Throws exception when the building doesn't exists or when the name is already in use
 */
CREATE OR REPLACE PROCEDURE update_room(room_id BIGINT, new_name TEXT, room_rep OUT JSON)
AS
$$
DECLARE
    room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    IF EXISTS (SELECT id FROM ROOM
    WHERE building = (SELECT building FROM ROOM WHERE id = room_id) AND name = new_name) THEN
        RAISE 'unique_room_name' USING ERRCODE = 'unique_violation';
    END IF;
    UPDATE ROOM SET name = new_name WHERE id = room_id
    RETURNING floor, state, timestamp INTO room_floor, room_state, tmstamp;
    IF (room_floor IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    room_rep = room_item_representation(room_id, new_name, room_floor, room_state, tmstamp);
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Gets all the rooms of a building
 * Returns a list with all the rooms item representation
 */
CREATE OR REPLACE FUNCTION get_rooms(
    company_id BIGINT,
    building_id BIGINT,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    rooms JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id, name, floor, state, timestamp
        FROM ROOM WHERE building = (SELECT id FROM BUILDING WHERE company = company_id AND id = building_id)
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        rooms = array_append(rooms, room_item_representation(rec.id, rec.name, rec.floor, rec.state, rec.timestamp));
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('rooms', rooms, 'roomsCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific room
 * Returns the room representation
 * Throws exception when the room id does not exist
 */
CREATE OR REPLACE FUNCTION get_room(room_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    devices JSON[];
    collection_size INT = 0;
    room_name TEXT; room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floor, state, timestamp INTO room_name, room_floor, room_state, tmstamp FROM ROOM WHERE id = room_id;
    IF (room_name IS NULL) THEN
        RAISE 'room_not_found';
    END IF;

    --get all devices that belong to the room
    FOR rec IN
        SELECT d.id, d.name, c.name as category, d.state, d.timestamp
        FROM ROOM_DEVICE rd INNER JOIN DEVICE d ON rd.device = d.id
        INNER JOIN CATEGORY c ON d.category = c.id
        WHERE rd.room = room_id LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(devices, device_item_representation(rec.id, rec.name, rec.category, rec.state, rec.timestamp));
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object(
        'id', room_id, 'name', room_name, 'floor', room_floor, 'state', room_state, 'timestamp', tmstamp,
        'devices', devices, 'devicesCollectionSize', collection_size
    );
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Add a device to a specific room
 * Returns the room and device representation
 * Throws exception when there is no row added
 */
CREATE OR REPLACE PROCEDURE add_room_device(room_id BIGINT, device_id BIGINT, room_rep OUT JSON)
AS
$$
DECLARE
    room_name TEXT; room_floor INT; room_state TEXT; room_timestamp TIMESTAMP;
    device_name TEXT; device_category TEXT; device_state TEXT; device_timestamp TIMESTAMP;
BEGIN
    SELECT name, floor, state, timestamp INTO room_name, room_floor, room_state, room_timestamp
    FROM ROOM WHERE id = room_id;
    SELECT d.name, d.state, c.name, d.timestamp INTO device_name, device_state, device_category, device_timestamp
    FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
    WHERE d.id = device_id;

    INSERT INTO ROOM_DEVICE (room, device) VALUES (room_id, device_id) RETURNING room INTO room_id;
    IF (NOT FOUND) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;

    room_rep = json_build_object(
        'room', room_item_representation(room_id, room_name, room_floor, room_state, room_timestamp),
        'device', device_item_representation(device_id, device_name, device_category, device_state, device_timestamp)
    );
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Remove a device from a specific room
 * Returns the room and device representation or when there is no row deleted.
 */
CREATE OR REPLACE PROCEDURE remove_room_device(room_id BIGINT, device_id BIGINT, room_rep OUT JSON)
AS
$$
DECLARE
    room_name TEXT; room_floor INT; room_state TEXT; room_timestamp TIMESTAMP;
    device_name TEXT; device_state TEXT; device_category TEXT; device_timestamp TIMESTAMP;
BEGIN
    DELETE FROM ROOM_DEVICE WHERE room = room_id AND device = device_id RETURNING room INTO room_id;
    IF (NOT FOUND) THEN
        RAISE 'unknown_error_deleting_resource';
    END IF;
    SELECT name, floor, state, timestamp INTO room_name, room_floor, room_state, room_timestamp
    FROM ROOM WHERE id = room_id;
    SELECT d.name, d.state, c.name, d.timestamp INTO device_name, device_state, device_category, device_timestamp
    FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
    WHERE d.id = device_id;
    room_rep = json_build_object(
        'room',room_item_representation(room_id, room_name, room_floor, room_state, room_timestamp),
        'device', device_item_representation(device_id, device_name, device_category, device_state, device_timestamp)
    );
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Deactivates a specific room, this is, sets its state to Inactive
 * Returns the room item representation
 * Throws exception when the room id does not exist or when there is no row updated
 */
CREATE OR REPLACE PROCEDURE deactivate_room(room_id BIGINT, room_rep OUT JSON)
AS
$$
DECLARE
    room_name TEXT; room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floor, state INTO room_name, room_floor, room_state  FROM ROOM WHERE id = room_id;
    CASE
        WHEN (room_name IS NULL) THEN
            RAISE 'room_not_found';
        WHEN (room_state = 'Active') THEN
            RAISE INFO 'LAST STATE: %', room_state;
            UPDATE ROOM SET state = 'Inactive', timestamp = CURRENT_TIMESTAMP
            WHERE id = room_id RETURNING state, timestamp INTO room_state, tmstamp;
            RAISE INFO 'NEW STATE: %', room_state;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    room_rep = room_item_representation(room_id, room_name, room_floor, room_state, tmstamp);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Activates a specific room, this is, sets its state to Active
 * Returns the building item representation
 * Throws exception when the room id does not exist
 */
CREATE OR REPLACE PROCEDURE activate_room(room_id BIGINT, room_rep OUT JSON)
AS
$$
DECLARE
    room_name TEXT; room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floor, state INTO room_name, room_floor, room_state FROM ROOM WHERE id = room_id;
    CASE
        WHEN (room_name IS NULL) THEN
            RAISE 'room_not_found';
        WHEN (room_state = 'Inactive') THEN
            UPDATE ROOM SET state = 'Active', timestamp = CURRENT_TIMESTAMP
            WHERE id = room_id RETURNING state, timestamp INTO room_state, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;
    room_rep = room_item_representation(room_id, room_name, room_floor, room_state, tmstamp);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/**
  Trigger to remove all qr codes from room-device relations
 */
CREATE OR REPLACE FUNCTION room_deactivate_delete_qr_codes() RETURNS TRIGGER
AS
$$
BEGIN
	IF NEW.state = 'Inactive' THEN
        UPDATE ROOM_DEVICE SET qr_hash = NULL WHERE room = NEW.id;
	END IF;
	RETURN NEW;
END$$ LANGUAGE plpgsql;

CREATE TRIGGER change_room_state_trigger
    BEFORE UPDATE ON ROOM
    FOR EACH ROW
    EXECUTE PROCEDURE room_deactivate_delete_qr_codes();