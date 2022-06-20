/*
 * Script to test all the implemented hash functionalities
 */

/*
 * Tests the creation of a new hash
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    new_hash TEXT = '01990A29E0B14336E9EA75D69C8F536DFE887DB5A41A5AA05D6CD795F141EA3E';
    success BOOL;
BEGIN

    RAISE INFO '---| Hash creation test |---';

    CALL create_hash(room_id, device_id, new_hash, success);
    IF (success) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the creation of a new hash, throws inactive_room_device
 */
DO
$$
DECLARE
    room_id BIGINT = 6;
    device_id BIGINT = 1;
    new_hash TEXT = '01990A29E0B14336E9EA75D69C8F536DFE887DB5A41A5AA05D6CD795F141EA3E';
    success BOOL;
    type TEXT;
BEGIN
    RAISE INFO '---| Hash creation test, throws inactive-resource |---';

    CALL create_hash(room_id, device_id, new_hash, success);
    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS type = MESSAGE_TEXT;
        IF (type = 'inactive-resource') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;

/*
 * Gets the hash value
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    expected_hash TEXT = 'D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90';
    current_hash TEXT;
BEGIN
    RAISE INFO '---| Get hash test |---';

    SELECT qr_hash INTO current_hash FROM HASH WHERE room = room_id AND device = device_id;

    IF (current_hash = expected_hash) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
END$$;