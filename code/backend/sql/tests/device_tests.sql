/*
 * Script to test all the implemented device functionalities
 */

/*
 * Tests the device representation function
 */
DO
$$
DECLARE
    id BIGINT = 1;
    device_name TEXT = 'Device name test';
    device_category TEXT = 'Device category';
    device_state TEXT = 'active';
    device_timestamp TIMESTAMP;
    device_rep JSON;
BEGIN
    RAISE INFO '---| Device item representation test |---';

    device_timestamp = CURRENT_TIMESTAMP;
    device_rep = device_item_representation(id, device_name, device_category, device_state, device_timestamp);
    IF (
        assert_json_value(device_rep, 'id', id::TEXT) AND
        assert_json_value(device_rep, 'name', device_name) AND
        assert_json_value(device_rep, 'category', device_category) AND
        assert_json_value(device_rep, 'state', device_state) AND
        assert_json_is_not_null(device_rep, 'timestamp')

    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new device
 */
DO
$$
DECLARE
    id BIGINT;
    device_name TEXT = 'Device name test';
    device_category INT = 1;
    category_name TEXT = 'water';
    device_state TEXT = 'active';
    device_rep JSON;
BEGIN
    RAISE INFO '---| Device creation test |---';

    CALL create_device(device_rep, device_name, device_category);
    id = device_rep->>'id';
    IF (
        assert_json_is_not_null(device_rep, 'id') AND
        assert_json_value(device_rep, 'name', device_name) AND
        assert_json_value(device_rep, 'category', category_name) AND
        assert_json_value(device_rep, 'state', device_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;

   -- Remove sequence inc
   IF (id = 1) THEN
        ALTER SEQUENCE device_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('device_id_seq', (SELECT last_value FROM device_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new device with a inactive category
 */
DO
$$
DECLARE
    device_name TEXT = 'Device name test';
    device_category INT = 3;
    ex_constraint TEXT;
    device_rep JSON;
BEGIN
    RAISE INFO '---| Device creation, throws inactive-resource test |---';

    CALL create_device(device_rep, device_name, device_category);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'inactive-resource') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests update the name of a device
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    device_name TEXT = 'Device name test';
    device_rep JSON;
BEGIN
    RAISE INFO '---| Device update test |---';

    CALL update_device(device_rep, device_id, device_name);
    IF (
        assert_json_value(device_rep, 'name', device_name)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests get devices function
 */
DO
$$
DECLARE
    devices_rep JSON;
    expected_collection_size INT = 4;
BEGIN
    RAISE INFO '---| Get devices function test |---';

    devices_rep = get_devices(10, 0);
    IF (
        assert_json_is_not_null(devices_rep, 'devices') AND
        assert_json_value(devices_rep, 'devicesCollectionSize', expected_collection_size::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests get a device
 */
DO
$$
DECLARE
    return_rep JSON;
    device_rep JSON;
    anomalies_rep JSON;
    device_id BIGINT = 1;
    device_name TEXT = 'Toilet1';
    device_category TEXT = 'water';
    device_state TEXT = 'active';
    anomalies_collection_size INT = 4;
BEGIN
    RAISE INFO '---| Get device function test |---';

    return_rep = get_device(device_id);
    device_rep = return_rep ->> 'device';
    anomalies_rep = return_rep ->> 'anomalies';

    IF (
        assert_json_value(device_rep, 'id', device_id::TEXT) AND
        assert_json_value(device_rep, 'name', device_name) AND
        assert_json_value(device_rep, 'category', device_category) AND
        assert_json_value(device_rep, 'state', device_state) AND
        assert_json_is_not_null(device_rep, 'timestamp') AND
        assert_json_is_not_null(return_rep, 'anomalies') AND
        assert_json_value(anomalies_rep, 'anomaliesCollectionSize', anomalies_collection_size::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests update a device category
 */
DO
$$
DECLARE
    device_rep JSON;
    device_id BIGINT = 1;
    device_category TEXT = 'electricity';
    new_category_id BIGINT = 2;
BEGIN
    RAISE INFO '---| Update device category test |---';

    CALL change_device_category(device_rep, device_id, new_category_id);

    IF (
        assert_json_value(device_rep, 'category', device_category)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the update of a device category with an inactive category, throws inactive-resource
 */
DO
$$
DECLARE
    device_id INT = 1;
    inactive_category BIGINT = 3;
    device_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update device category, throws inactive-resource test |---';

    CALL change_device_category(device_rep, device_id, inactive_category);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'inactive-resource') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Deactivate device test
 */
DO
$$
DECLARE
    device_rep JSON;
    device_id BIGINT = 1;
    expected_state TEXT = 'inactive';
BEGIN
    RAISE INFO '---| Deactivate device test |---';

    CALL deactivate_device(device_rep, device_id);

    IF (
        assert_json_value(device_rep, 'state', expected_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Activate device test
 */
DO
$$
DECLARE
    device_rep JSON;
    device_id BIGINT = 4;
    expected_state TEXT = 'active';
BEGIN
    RAISE INFO '---| Activate device test |---';

    CALL activate_device(device_rep, device_id);

    IF (
        assert_json_value(device_rep, 'state', expected_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Get room devices function test
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_id BIGINT = 1;
    devices_rep JSON;
    expected_collection_size INT = 1;
BEGIN
    RAISE INFO '---| Get room devices function test |---';

    devices_rep = get_room_devices(company_id, building_id, room_id, 10, 0);
    IF (
        assert_json_is_not_null(devices_rep, 'devices') AND
        assert_json_value(devices_rep, 'devicesCollectionSize', expected_collection_size::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Get room device function test
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    return_rep JSON;
BEGIN
    RAISE INFO '---| Get room device function test |---';

    return_rep = get_room_device(company_id, building_id, room_id, device_id);
    IF (
        assert_json_is_not_null(return_rep, 'device') AND
        assert_json_is_not_null(return_rep, 'hash')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;