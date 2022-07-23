/*
 * Script to test all the implemented hash functionalities
 */

/*
 * Tests the creation of a new hash
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    new_hash TEXT = '5abd4089b7921fd6af09d1cc1cbe5220';
    success BOOL;
BEGIN
    RAISE INFO '---| Hash creation test |---';

    CALL create_hash(success, company_id, building_id, room_id, device_id, new_hash);
    IF (success) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the creation of a new hash, throws resource-not-found
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_id BIGINT = 3;
    device_id BIGINT = 2;
    new_hash TEXT = '5abd4089b7921fd6af09d1cc1cbe5220';
    success BOOL;
    type TEXT;
BEGIN
    RAISE INFO '---| Hash creation test, throws inactive-resource |---';

    CALL create_hash(success, company_id, building_id, room_id, device_id, new_hash);
    RAISE '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS type = MESSAGE_TEXT;

        IF (type = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE '-> Test failed!';
        END IF;
END$$;

/*
 * Gets the hash value
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    expected_hash TEXT = '5abd4089b7921fd6af09d1cc1cbe5220';
    current_hash TEXT;
BEGIN
    RAISE INFO '---| Get hash test |---';

    SELECT get_room_device_hash(company_id, building_id, room_id, device_id) INTO current_hash;

    IF (current_hash = expected_hash) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Test the obtainment of the data associated to the hash
 */
DO
$$
DECLARE
    company TEXT = 'ISEL';
    building TEXT = 'A';
    room TEXT = '1 - Bathroom';
    device TEXT = 'Toilet1';
    hash TEXT = '5abd4089b7921fd6af09d1cc1cbe5220';
    response JSON;
BEGIN
    RAISE INFO '---| Get hash data test |---';

    response = get_hash_data(hash);

    IF (
        assert_json_value(response, 'company', company) AND
        assert_json_value(response, 'building', building) AND
        assert_json_value(response, 'room', room) AND
        assert_json_value(response, 'device', device)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;