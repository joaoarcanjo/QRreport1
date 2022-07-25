/*
 * Room functionalities
 */

/**
  * Auxiliary function to verify if a room exists
  */
CREATE OR REPLACE FUNCTION room_exists(company_id BIGINT, building_id BIGINT, room_id BIGINT)
RETURNS BOOL
AS
$$
DECLARE room_name TEXT;
BEGIN
    SELECT name INTO room_name FROM ROOM WHERE id = room_id AND building = building_id AND building_id IN
        (SELECT id FROM BUILDING WHERE company = company_id);
    IF (room_name IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'room', HINT = room_id;
    END IF;
    RETURN TRUE;
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a room name already exists inside a company
  */
CREATE OR REPLACE FUNCTION verify_unique_room_name(building_id BIGINT, room_name TEXT)
RETURNS BOOL
AS
$$
BEGIN
    IF EXISTS (SELECT id FROM ROOM WHERE building = building_id AND name = room_name) THEN
        RAISE 'unique-constraint' USING DETAIL = 'building name', HINT = room_name, ERRCODE = 'unique_violation';
    END IF;
    RETURN TRUE;
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a room state is active
  */
CREATE OR REPLACE FUNCTION is_room_active(room_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF EXISTS (SELECT id FROM ROOM WHERE id = room_id AND state = 'inactive') THEN
        RAISE 'inactive-resource';
    END IF;
    RETURN TRUE;
END$$LANGUAGE plpgsql;

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
  * Auxiliary function to return the room item representation by providing only the id
  */
CREATE OR REPLACE FUNCTION room_item_representation(room_id BIGINT)
RETURNS JSON
AS
$$
DECLARE room_name TEXT; room_floor INT; room_state TEXT; room_tmstmp TIMESTAMP;
BEGIN
    SELECT name, floor, state, timestamp INTO room_name, room_floor, room_state, room_tmstmp
    FROM ROOM WHERE id = room_id;
    RETURN json_build_object('id', room_id, 'name', room_name, 'floor', room_floor, 'state', room_state, 'timestamp', room_tmstmp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new room
 * Returns the room item representation
 * Throws exception when the building doesn't exists, when the name is already in use
 * or when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE create_room(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_name TEXT,
    room_floor INT
)
AS
$$
DECLARE
    room_id BIGINT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    PERFORM verify_unique_room_name(building_id, room_name);

    INSERT INTO ROOM (name, floor, building) VALUES (room_name, room_floor, building_id)
    RETURNING id, state, timestamp INTO room_id, room_state, tmstamp;

    room_rep = room_item_representation(room_id, room_name, room_floor, room_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Update the name of a specific room
 * Returns the room item representation
 * Throws exception when the building doesn't exists or when the name is already in use
 */
CREATE OR REPLACE PROCEDURE update_room(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    new_name TEXT
)
AS
$$
DECLARE room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    PERFORM is_room_active(room_id);
    PERFORM verify_unique_room_name(building_id, new_name);

    UPDATE ROOM SET name = new_name WHERE id = room_id
    RETURNING floor, state, timestamp INTO room_floor, room_state, tmstamp;

    room_rep = room_item_representation(room_id, new_name, room_floor, room_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'serializable'
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
    END LOOP;

    SELECT COUNT(id) INTO collection_size FROM ROOM
    WHERE building = (SELECT id FROM BUILDING WHERE company = company_id AND id = building_id);

    RETURN json_build_object('rooms', rooms, 'roomsCollectionSize', collection_size,
        'buildingState', (SELECT state FROM BUILDING WHERE id = building_id));
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific room
 * Returns the room representation
 * Throws exception when the room id does not exist
 */
CREATE OR REPLACE FUNCTION get_room(
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    devices JSON[];
    collection_size INT = 0;
    room_name TEXT; room_floor INT; room_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floor, state, timestamp INTO room_name, room_floor, room_state, tmstamp FROM ROOM
    WHERE id = room_id AND building = building_id AND building_id IN (SELECT id FROM BUILDING WHERE company = company_id);
    IF (room_name IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'room', HINT = room_id;
    END IF;

    --get all devices that belong to the room
    FOR rec IN
        SELECT d.id, d.name, c.name as category, d.state, d.timestamp FROM ROOM_DEVICE rd
            INNER JOIN DEVICE d ON rd.device = d.id INNER JOIN CATEGORY c ON d.category = c.id
        WHERE rd.room = room_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(
            devices,
            device_item_representation(rec.id, rec.name, rec.category, rec.state, rec.timestamp)
        );
    END LOOP;

    SELECT COUNT(d.id) INTO collection_size FROM ROOM_DEVICE rd
        INNER JOIN DEVICE d ON rd.device = d.id INNER JOIN CATEGORY c ON d.category = c.id
    WHERE rd.room = room_id;

    RETURN json_build_object(
        'room', room_item_representation(room_id, room_name, room_floor, room_state, tmstamp),
        'devices', json_build_object('devices', devices, 'devicesCollectionSize', collection_size)
    );
END$$
LANGUAGE plpgsql;

/*
 * Add a device to a specific room
 * Returns the room and device representation
 * Throws exception when there is no row added
 */
CREATE OR REPLACE PROCEDURE add_room_device(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    device_id BIGINT
)
AS
$$
DECLARE
    device_name TEXT; device_category TEXT; device_state TEXT; device_timestamp TIMESTAMP;
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    PERFORM is_room_active(room_id);

    SELECT d.name, d.state, c.name, d.timestamp INTO device_name, device_state, device_category, device_timestamp
    FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
    WHERE d.id = device_id;

    INSERT INTO ROOM_DEVICE (room, device) VALUES (room_id, device_id) RETURNING room INTO room_id;

    room_rep = json_build_object(
        'room', room_item_representation(room_id),
        'device', device_item_representation(device_id, device_name, device_category, device_state, device_timestamp)
    );
END$$
LANGUAGE plpgsql;

/*
 * Remove a device from a specific room
 * Returns the room and device representation or when there is no row deleted.
 */
CREATE OR REPLACE PROCEDURE remove_room_device(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    device_id BIGINT
)AS
$$
DECLARE
    device_name TEXT; device_state TEXT; device_category TEXT; device_timestamp TIMESTAMP;
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    PERFORM is_room_active(room_id);

    DELETE FROM ROOM_DEVICE WHERE room = room_id AND device = device_id RETURNING room INTO room_id;

    SELECT d.name, d.state, c.name, d.timestamp INTO device_name, device_state, device_category, device_timestamp
    FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
    WHERE d.id = device_id;
    room_rep = json_build_object(
        'room',room_item_representation(room_id),
        'device', device_item_representation(device_id, device_name, device_category, device_state, device_timestamp)
    );
END$$
LANGUAGE plpgsql;

/*
 * Deactivates a specific room, this is, sets its state to Inactive
 * Returns the room item representation
 * Throws exception when the room id does not exist or when there is no row updated
 */
CREATE OR REPLACE PROCEDURE deactivate_room(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT
)
AS
$$
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    IF (EXISTS (SELECT state FROM ROOM WHERE id = room_id AND state = 'active')) THEN
        UPDATE ROOM SET state = 'inactive', timestamp = CURRENT_TIMESTAMP WHERE id = room_id;
    ELSE
        -- Do nothing when it's already inactive
    END IF;

    room_rep = room_item_representation(room_id);
END$$
LANGUAGE plpgsql;

/*
 * Activates a specific room, this is, sets its state to Active
 * Returns the building item representation
 * Throws exception when the room id does not exist
 */
CREATE OR REPLACE PROCEDURE activate_room(
    room_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT
)
AS
$$
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    IF (EXISTS (SELECT state FROM ROOM WHERE id = room_id AND state = 'inactive')) THEN
        UPDATE ROOM SET state = 'active', timestamp = CURRENT_TIMESTAMP WHERE id = room_id;
    ELSE
        -- Do nothing when it's already inactive
    END IF;
    room_rep = room_item_representation(room_id);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/**
  Trigger to remove all qr codes from room-device relations
 */
CREATE OR REPLACE FUNCTION room_deactivate_delete_qr_codes() RETURNS TRIGGER
AS
$$
BEGIN
	IF NEW.state = 'inactive' THEN
        UPDATE ROOM_DEVICE SET qr_hash = NULL WHERE room = NEW.id;
	END IF;
	RETURN NEW;
END$$ LANGUAGE plpgsql;

CREATE TRIGGER change_room_state_trigger
    BEFORE UPDATE ON ROOM
    FOR EACH ROW
    EXECUTE PROCEDURE room_deactivate_delete_qr_codes();