/*
 * qrcode functionalities
 */

/**
 * Creates a new one or replace the last hash relative a room-device relation
 * Throws exception when is not possible to create or replace hash value
 * Returns the hash item representation
 * -> It's not necessary to change default transaction isolation level
 */
CREATE OR REPLACE PROCEDURE create_hash(room_id BIGINT, device_id BIGINT, new_hash TEXT, hash_rep OUT JSON)
AS
$$
BEGIN
    UPDATE ROOM_DEVICE SET qr_hash = new_hash WHERE room = room_id AND device = device_id
    RETURNING room, device, qr_hash INTO room_id, device_id, new_hash;
    IF (room_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    hash_rep = json_build_object('qr_hash', new_hash);

END$$ LANGUAGE plpgsql;

/**
 * Gets the hash value of a device-room relation
 * Throws exception when doesn't exists any hash value
 * Returns the hash item representation
 * -> It's not necessary to change default transaction isolation level
 */
CREATE OR REPLACE FUNCTION get_hash(room_id BIGINT, device_id BIGINT) RETURNS JSON
AS
$$
DECLARE
    hash TEXT;
BEGIN
    SELECT qr_hash INTO hash FROM ROOM_DEVICE WHERE room = room_id AND device = device_id;
    IF (hash IS NULL) THEN
        RAISE 'hash_not_found';
    END IF;
    RETURN json_build_object('qr_hash', hash);
END$$ LANGUAGE plpgsql;