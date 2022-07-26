/*
 * Device functionalities
 */

/*
 * Auxiliary function to verify if a device exists
 */
CREATE OR REPLACE FUNCTION device_exists(device_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT id FROM DEVICE WHERE id = device_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'device', HINT = device_id;
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the device item representation by device id
 */
CREATE OR REPLACE FUNCTION device_item_representation(device_id BIGINT)
RETURNS JSON
AS
$$
DECLARE
    device_name TEXT; device_category TEXT; device_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, state, timestamp, (SELECT name FROM CATEGORY WHERE id = category)
    INTO device_name, device_state, tmstamp, device_category FROM DEVICE WHERE id = device_id;
    RETURN json_build_object('id', device_id, 'name', device_name, 'category', device_category, 'state', device_state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the device item representation
 */
CREATE OR REPLACE FUNCTION device_item_representation(d_id BIGINT, d_name TEXT, d_category TEXT, d_state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', d_id, 'name', d_name, 'category', d_category, 'state', d_state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new device
 * Returns the device item representation
 * Throws exception in case there is no row added or when the category no exists/is inactive.
 */
CREATE OR REPLACE PROCEDURE create_device(device_rep OUT JSON, device_name TEXT, category_id BIGINT)
AS
$$
DECLARE
    device_id BIGINT; device_state TEXT; device_category TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, state INTO device_category, device_state FROM CATEGORY WHERE id = category_id;
    IF (device_category IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'category', HINT = category_id;
    ELSEIF (device_state = 'inactive') THEN
        RAISE 'inactive-resource';
    END IF;
    INSERT INTO DEVICE (name, category) VALUES (device_name, category_id)
    RETURNING id, state, timestamp INTO device_id, device_state, tmstamp;

    device_rep = device_item_representation(device_id, device_name, device_category, device_state, tmstamp);
END$$
LANGUAGE plpgsql;

/*
 * Updates a device
 * Returns the device item representation
 * Throws exception in case there is no row affected
 */
CREATE OR REPLACE PROCEDURE update_device(device_rep OUT JSON, device_id BIGINT, new_name TEXT)
AS
$$
DECLARE
    category_id INT; device_state TEXT;
BEGIN
    SELECT category, state INTO category_id, device_state FROM DEVICE WHERE id = device_id;
    IF (category_id IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'device', HINT = device_id;
    ELSEIF (device_state = 'inactive') THEN
        RAISE 'inactive-resource';
    END IF;

    UPDATE DEVICE SET name = new_name WHERE id = device_id;

    device_rep = device_item_representation(device_id);
END$$
LANGUAGE plpgsql;

/**
 * Gets all devices
 */
CREATE OR REPLACE FUNCTION get_devices(limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    devices JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT d.id, d.name, c.name AS category, d.state, d.timestamp
        FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
        ORDER BY d.id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(
            devices,
            device_item_representation(rec.id, rec.name, rec.category, rec.state, rec.timestamp)
        );
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM DEVICE;
    RETURN json_build_object('devices', devices, 'devicesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/**
 * Get device
 */
CREATE OR REPLACE FUNCTION get_device(device_id BIGINT)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    anomalies JSON[];
    collection_size INT = 0;
BEGIN
    PERFORM device_exists(device_id);

    --get all device anomalies
    FOR rec IN
        SELECT id, anomaly FROM ANOMALY WHERE device = device_id
    LOOP
        anomalies = array_append(anomalies, anomaly_item_representation(rec.id, rec.anomaly));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM ANOMALY WHERE device = device_id;
    RETURN json_build_object(
            'device', device_item_representation(device_id),
            'anomalies', json_build_object('anomalies', anomalies, 'anomaliesCollectionSize', collection_size));
END$$
LANGUAGE plpgsql;

/*
 * Change the device category
 * Returns the device item representation
 * Throws exception when the category is inactive or when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE change_device_category(device_rep OUT JSON, device_id BIGINT, new_category_id BIGINT)
AS
$$
DECLARE
    device_category TEXT; device_state TEXT;
BEGIN
    SELECT name, state INTO device_category, device_state FROM CATEGORY WHERE id = new_category_id;
    IF (device_category IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'category', HINT = new_category_id;
    ELSEIF (device_state = 'inactive') THEN
        RAISE 'inactive-resource';
    END IF;

    UPDATE DEVICE SET category = new_category_id WHERE id = device_id AND category != new_category_id;

    device_rep = device_item_representation(device_id);
END$$
LANGUAGE plpgsql;

/*
 * Change the device state to inactive
 * Returns the device item representation
 * Throws exception when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE deactivate_device(device_rep OUT JSON, device_id BIGINT)
AS
$$
BEGIN
    PERFORM device_exists(device_id);
    UPDATE DEVICE SET state = 'inactive', timestamp = CURRENT_TIMESTAMP WHERE id = device_id AND state = 'active';
    device_rep = device_item_representation(device_id);
END$$
LANGUAGE plpgsql;

/*
 * Change the device state to active
 * Returns the device item representation
 * Throws exception when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE activate_device(device_rep OUT JSON, device_id BIGINT)
AS
$$
BEGIN
    PERFORM device_exists(device_id);
    UPDATE DEVICE SET state = 'active', timestamp = CURRENT_TIMESTAMP WHERE id = device_id AND state = 'inactive';

    device_rep = device_item_representation(device_id);
END$$
LANGUAGE plpgsql;

/*
 * Get all room devices
 * Returns all devices presents in a room
 */
CREATE OR REPLACE FUNCTION get_room_devices(
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
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    FOR rec IN
        SELECT d.id, d.name, c.name AS category, d.state, d.timestamp FROM DEVICE d
            INNER JOIN CATEGORY c ON (d.category = c.id)
            INNER JOIN ROOM_DEVICE ON (device = d.id AND room = room_id)
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(
            devices,
            device_item_representation(rec.id, rec.name, rec.category, rec.state, rec.timestamp)
        );
    END LOOP;
    SELECT COUNT(device) INTO collection_size FROM ROOM_DEVICE WHERE room = room_id;
    RETURN json_build_object('devices', devices, 'devicesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Get a room device
 */
CREATE OR REPLACE FUNCTION get_room_device(
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    device_id BIGINT
)
RETURNS JSON
AS
$$
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    PERFORM device_exists(device_id);

    RETURN json_build_object(
        'device', device_item_representation(device_id),
        'hash', (SELECT qr_hash FROM ROOM_DEVICE WHERE room = room_id AND device = device_id));
END$$ LANGUAGE plpgsql;

/**
  Trigger to remove all qr codes from room-device relations
 */
CREATE OR REPLACE FUNCTION device_deactivate_delete_qr_codes() RETURNS TRIGGER
AS
$$
BEGIN
	IF NEW.state = 'inactive' THEN
        UPDATE ROOM_DEVICE SET qr_hash = NULL WHERE device = NEW.id;
	END IF;
	RETURN NEW;
END; $$LANGUAGE PLPGSQL;

CREATE TRIGGER change_device_state_trigger
    BEFORE UPDATE ON DEVICE
    FOR EACH ROW
    EXECUTE PROCEDURE device_deactivate_delete_qr_codes();