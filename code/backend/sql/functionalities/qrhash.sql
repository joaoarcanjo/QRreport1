/*
 * qrcode functionalities
 */

/*
 * Auxiliary function to return the qrhash item representation
 */
CREATE OR REPLACE FUNCTION qrhash_item_representation (hash TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('qrhash', hash);
END$$ LANGUAGE plpgsql;

/**
 * Creates or replaces an hash associated to a room device
 * Throws exception when is not possible to create or replace an hash and when the room or device are inactive
 * Returns the hash item representation
 */
CREATE OR REPLACE PROCEDURE create_hash(
    success OUT BOOL,
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    device_id BIGINT,
    new_hash TEXT
)
AS
$$
DECLARE
   room_state TEXT; device_state TEXT;
BEGIN
    SELECT state INTO room_state FROM ROOM WHERE id = room_id AND building = building_id AND building IN
        (SELECT id FROM BUILDING WHERE company = company_id);
    SELECT state INTO device_state FROM DEVICE WHERE id = device_id;

    CASE
        WHEN (room_state IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'room';
        WHEN (device_state IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'device';
        WHEN (room_state = 'inactive') THEN
            RAISE 'inactive-resource' USING DETAIL = 'room';
        WHEN (device_state = 'inactive') THEN
            RAISE 'inactive-resource' USING DETAIL = 'device';
        ELSE
            --
    END CASE;

    UPDATE ROOM_DEVICE SET qr_hash = new_hash WHERE room = room_id AND device = device_id RETURNING room INTO room_id;
    IF (room_id IS NULL) THEN
        RAISE 'unknown-error-writing-resource';
    END IF;
    success = TRUE;
END$$LANGUAGE plpgsql;

/**
  * Function to get the hash of a specific room device.
  */
CREATE OR REPLACE FUNCTION get_room_device_hash(
    company_id BIGINT,
    building_id BIGINT,
    room_id BIGINT,
    device_id BIGINT
)
RETURNS TEXT
AS
$$
BEGIN
    PERFORM room_exists(company_id, building_id, room_id);
    PERFORM device_exists(device_id);
    RETURN (SELECT qr_hash FROM ROOM_DEVICE WHERE device = device_id AND room = room_id);
END$$LANGUAGE plpgsql;

/**
  * Function to get the hash of a specific room device.
  */
CREATE OR REPLACE FUNCTION get_hash_data(hash TEXT)
RETURNS JSON
AS
$$
DECLARE ret_company TEXT; ret_building TEXT; ret_room TEXT; ret_device TEXT;
BEGIN
    SELECT c.name, b.name, r.name, d.name
    INTO ret_company, ret_building, ret_room, ret_device FROM ROOM_DEVICE rd
        INNER JOIN DEVICE d ON (rd.device = d.id)
        INNER JOIN ROOM r ON (rd.room = r.id)
        INNER JOIN BUILDING b ON (r.building = b.id)
        INNER JOIN COMPANY c ON (b.company = c.id)
    WHERE qr_hash = hash;

    IF (ret_company IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'hash', HINT = hash;
    END IF;

    RETURN json_build_object('company', ret_company, 'building', ret_building, 'room', ret_room, 'device', ret_device);
END$$LANGUAGE plpgsql;
