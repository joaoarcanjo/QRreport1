/*
 * Script to test all the implemented qrhash functionalities
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
    hash_rep JSON;
BEGIN
    RAISE INFO '---| Hash creation test |---';

    CALL create_hash(room_id, device_id, new_hash, hash_rep);
    IF (
        assert_json_value(hash_rep, 'qr_hash', new_hash)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
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
    hash_rep JSON;
BEGIN
    RAISE INFO '---| Get hash test |---';

    SELECT get_hash(room_id, device_id) INTO hash_rep;
    IF (
        assert_json_value(hash_rep, 'qr_hash', expected_hash)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;