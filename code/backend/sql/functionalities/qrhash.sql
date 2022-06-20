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
CREATE OR REPLACE PROCEDURE create_hash(room_id BIGINT, device_id BIGINT, new_hash TEXT, success OUT BOOL)
AS
$$
DECLARE
   room_state TEXT; device_state TEXT;
BEGIN
    SELECT state INTO room_state FROM ROOM WHERE id = room_id;
    SELECT state INTO device_state FROM DEVICE WHERE id = device_id;

    CASE
        WHEN (room_state IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'room';
        WHEN (device_state IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'device';
        WHEN (room_state = 'Inactive') THEN
            RAISE 'inactive-resource' USING DETAIL = 'room';
        WHEN (device_state = 'Inactive') THEN
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
  * View to get the hash of a specific room device.
  */
CREATE VIEW HASH AS SELECT room, device, qr_hash FROM ROOM_DEVICE;
