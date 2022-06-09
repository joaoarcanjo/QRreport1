/*
 * Device functionalities
 */

/*
 * Auxiliary function to return the device item representation
 */
CREATE OR REPLACE FUNCTION device_item_representation (d_id BIGINT, d_name TEXT, d_category TEXT, d_state TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', d_id, 'name', d_name, 'category', d_category, 'state', d_state);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new device
 * Returns the device item representation
 * Throws exception in case there is no row added or when the category no exists/is inactive.
 */
CREATE OR REPLACE PROCEDURE create_device(device_name TEXT, category_id BIGINT, device_rep OUT JSON)
AS
$$
DECLARE
    device_id BIGINT; device_state TEXT; device_category TEXT;
BEGIN
    SELECT name INTO device_category FROM CATEGORY WHERE id = category_id AND state != 'Inactive';
    IF (device_category IS NULL) THEN
        RAISE 'not_valid_category';
    END IF;
    INSERT INTO DEVICE (name, category) VALUES (device_name, category_id)
    RETURNING id, state INTO device_id, device_state;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    device_rep = device_item_representation(device_id, device_name, device_category, device_state);
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Updates a device
 * Returns the device item representation
 * Throws exception in case there is no row affected
 */
CREATE OR REPLACE PROCEDURE update_device(device_id BIGINT, new_name TEXT, device_rep OUT JSON)
AS
$$
DECLARE
    category_id INT; device_category TEXT; device_state TEXT;
BEGIN
    UPDATE DEVICE SET name = new_name WHERE id = device_id
    RETURNING id, name, category, state INTO device_id, new_name, category_id, device_state;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    SELECT name INTO device_category FROM CATEGORY WHERE id = category_id;
    device_rep = device_item_representation(device_id, new_name, device_category, device_state);
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
        SELECT d.id, d.name, c.name AS category, d.state
        FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(devices, device_item_representation(rec.id, rec.name, rec.category, rec.state));
        collection_size = collection_size + 1;
    END LOOP;

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
    device_name TEXT; device_category TEXT; device_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT d.name, c.name AS category, d.state, d.timestamp
    INTO device_name, device_category, device_state, tmstamp
    FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
    WHERE d.id = device_id;

    --get all device anomalies
    FOR rec IN
        SELECT id, anomaly FROM ANOMALY WHERE device = device_id
    LOOP
        anomalies = array_append(anomalies, anomaly_item_representation(rec.id, rec.anomaly));
        collection_size = collection_size + 1;
    END LOOP;
    return json_build_object('id', device_id, 'name', device_name, 'category', device_category,
            'state', device_state, 'timestamp', tmstamp, 'anomalies', anomalies,
            'anomaliesCollectionSize', collection_size);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Change the device category
 * Returns the device item representation
 * Throws exception when the category is inactive or when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE change_device_category(device_id BIGINT, new_category_id BIGINT, device_rep OUT JSON)
AS
$$
DECLARE
    device_category TEXT; device_name TEXT; device_state TEXT;
BEGIN
    SELECT name INTO device_category FROM CATEGORY WHERE id = new_category_id AND state != 'Inactive';
    IF (device_category IS NULL) THEN
        RAISE 'not_valid_category';
    END IF;
    UPDATE DEVICE SET category = new_category_id WHERE id = device_id
    RETURNING id, name, state INTO device_id, device_name, device_state;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    device_rep = device_item_representation(device_id, device_name, device_category, device_state);
END$$
LANGUAGE plpgsql;

/*
 * Change the device state to inactive
 * Returns the device item representation
 * Throws exception when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE deactivate_device (device_id BIGINT, device_rep OUT JSON)
AS
$$
DECLARE
    device_name TEXT; device_state TEXT; tmstamp TIMESTAMP; category_id BIGINT; device_category TEXT;
BEGIN
    UPDATE DEVICE SET state = 'Inactive', timestamp = CURRENT_TIMESTAMP WHERE id = device_id
    RETURNING id, name, category, state, timestamp INTO device_id, device_name, category_id, device_state, tmstamp;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    SELECT name INTO device_category FROM CATEGORY WHERE id = category_id;
    device_rep = json_build_object('id', device_id, 'name', device_name, 'category', device_category,
            'state', device_state, 'timestamp', tmstamp);
END$$
LANGUAGE plpgsql;

/*
 * Change the device state to active
 * Returns the device item representation
 * Throws exception when no rows was affected.
 */
CREATE OR REPLACE PROCEDURE activate_device (device_id BIGINT, device_rep OUT JSON)
AS
$$
DECLARE
    device_name TEXT; device_state TEXT; tmstamp TIMESTAMP; category_id BIGINT; device_category TEXT;
BEGIN
    UPDATE DEVICE SET state = 'Active', timestamp = CURRENT_TIMESTAMP WHERE id = device_id
    RETURNING id, name, category, state, timestamp INTO device_id, device_name, category_id, device_state, tmstamp;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    SELECT name INTO device_category FROM CATEGORY WHERE id = category_id;
    device_rep = json_build_object('id', device_id, 'name', device_name, 'category', device_category,
            'state', device_state, 'timestamp', tmstamp);
END$$
LANGUAGE plpgsql;

/*
 * Get all room devices
 * Returns all devices presents in a room
 */
CREATE OR REPLACE FUNCTION get_room_devices(room_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    devices JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT d.id, d.name, c.name AS category, d.state
        FROM DEVICE d INNER JOIN CATEGORY c ON d.category = c.id
        INNER JOIN ROOM_DEVICE ON device = d.id AND room = room_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        devices = array_append(devices, device_item_representation(rec.id, rec.name, rec.category, rec.state));
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('devices', devices, 'devicesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Get a room device
 */
CREATE OR REPLACE FUNCTION get_room_device(room_id BIGINT, device_id BIGINT)
RETURNS JSON
AS
$$
DECLARE
    device_id BIGINT; device_name TEXT; device_category TEXT; device_state TEXT; hash TEXT;
BEGIN
    SELECT d.id, d.name, d.state, c.name AS category, rd.qr_hash
    INTO device_id, device_name, device_state, device_category, hash
    FROM ROOM_DEVICE rd
        INNER JOIN DEVICE d ON rd.device = d.id
        INNER JOIN CATEGORY c ON d.category = c.id
    WHERE rd.room = room_id;

    RETURN json_build_object(
        'device', device_item_representation(device_id, device_name, device_category, device_state),
        'qrcode', qrhash_item_representation(hash));
END$$ LANGUAGE plpgsql;

/**
  Trigger to remove all qr codes from room-device relations
 */
CREATE OR REPLACE FUNCTION device_deactivate_delete_qr_codes() RETURNS TRIGGER
AS
$$
BEGIN
	IF NEW.state = 'Inactive' THEN
        UPDATE ROOM_DEVICE SET qr_hash = NULL WHERE device = NEW.id;
	END IF;
	RETURN NEW;
END; $$LANGUAGE PLPGSQL;

CREATE TRIGGER change_device_state_trigger
    BEFORE UPDATE ON DEVICE
    FOR EACH ROW
    EXECUTE PROCEDURE device_deactivate_delete_qr_codes();