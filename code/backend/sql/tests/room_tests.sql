/*
 * Script to test all the implemented room functionalities
 */

/*
 * Tests the room representation function
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    room_name TEXT = 'Room name test';
    room_floor INT = 12;
    room_state TEXT = 'Active';
    room_timestamp TIMESTAMP;
    room_rep JSON;
BEGIN
    RAISE INFO '---| Room item representation test |---';
    room_timestamp = CURRENT_TIMESTAMP;
    room_rep = room_item_representation(
        room_id, room_name, room_floor, room_state, room_timestamp);
    IF (
        assert_json_value(room_rep, 'id', room_id::TEXT) AND
        assert_json_value(room_rep, 'name', room_name) AND
        assert_json_value(room_rep, 'floor', room_floor::TEXT) AND
        assert_json_value(room_rep, 'state', room_state) AND
        assert_json_is_not_null(room_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new room
 */
DO
$$
DECLARE
    id BIGINT;
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_name TEXT = 'Room name test';
    room_state TEXT = 'Active';
    room_floor INT = 12;
    room_rep JSON;
BEGIN
    RAISE INFO '---| Room creation test |---';

    CALL create_room(company_id, building_id, room_name, room_floor, room_rep);
    IF (
        assert_json_is_not_null(room_rep, 'id') AND
        assert_json_value(room_rep, 'name', room_name) AND
        assert_json_value(room_rep, 'floor', room_floor::TEXT) AND
        assert_json_value(room_rep, 'state', room_state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;

   -- Remove sequence inc
   IF (id = 1) THEN
        ALTER SEQUENCE room_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('room_id_seq', (SELECT last_value FROM room_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new room with a non unique name, throws unique_building_name
 */
DO
$$
DECLARE
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    room_name TEXT = 'Biblioteca';
    room_floor INT = 12;
    room_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Room creation, throws unique_room_name test |---';

    CALL create_room(company_id, building_id, room_name, room_floor, room_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_room_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Test update room name
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    new_name TEXT = 'Biblioteca v2.0';
    room_rep JSON;
BEGIN
    RAISE INFO '---| Update room name test |---';

    CALL update_room(room_id, new_name, room_rep);

    IF (assert_json_value(room_rep, 'name', new_name)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests update room name to a non unique name, throws unique_building_name
 */
DO
$$
DECLARE
    room_id BIGINT = 2;
    new_name TEXT = 'Biblioteca';
    room_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update room name, throws unique_room_name test |---';

    CALL update_room(room_id, new_name, room_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_room_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the get rooms function
 */
DO
$$
DECLARE
    rooms_col_size INT = 3;
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    rooms_rep JSON;
BEGIN
    RAISE INFO '---| Get rooms test |---';

    SELECT get_rooms(company_id, building_id, 10, 0) INTO rooms_rep;

    IF (assert_json_is_not_null(rooms_rep, 'rooms') AND
        assert_json_value(rooms_rep, 'roomsCollectionSize', rooms_col_size::TEXT)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the get room function
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    company_id BIGINT = 1;
    building_id BIGINT = 1;
    devices_col_size INT = 2;
    return_rep JSON;
    devices_rep JSON;
BEGIN
    RAISE INFO '---| Get building test |---';

    SELECT get_room(room_id, 10, 0) INTO return_rep;
    devices_rep = return_rep ->> 'devices';
    IF (
        assert_json_is_not_null(return_rep, 'room') AND
        assert_json_is_not_null(return_rep, 'devices') AND
        assert_json_value(return_rep, 'buildingId', company_id::TEXT) AND
        assert_json_value(return_rep, 'companyId', building_id::TEXT) AND
        assert_json_value(devices_rep, 'devicesCollectionSize', devices_col_size::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the get room function, throws room_not_found
 */
DO
$$
DECLARE
    room_id BIGINT = -1;
    room_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Get room with non existent id test |---';

    SELECT get_room(room_id, 10, 0) INTO room_rep;
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'room_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests add a device to a specific room
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    device_id BIGINT = 2;
    result_rep JSON;
    room_rep JSON;
    device_rep JSON;

BEGIN
    RAISE INFO '---| Add a device to a specific room test |---';

    CALL add_room_device(room_id, device_id, result_rep);
    room_rep = result_rep->>'room';
    device_rep = result_rep->>'device';

    IF (assert_json_value(room_rep, 'id', room_id::TEXT) AND
        assert_json_value(device_rep, 'id', device_id::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests remove a device from a specific room
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    device_id BIGINT = 1;
    result_rep JSON;
    room_rep JSON;
    device_rep JSON;

BEGIN
    RAISE INFO '---| Remove a device from a specific room test |---';

    CALL remove_room_device(room_id, device_id, result_rep);
    room_rep = result_rep->>'room';
    device_rep = result_rep->>'device';

    IF (assert_json_value(room_rep, 'id', room_id::TEXT) AND
        assert_json_value(device_rep, 'id', device_id::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the room deactivation
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    state TEXT = 'Inactive';
    room_rep JSON;
    return_rep JSON;
BEGIN
    RAISE INFO '---| Room deactivation test |---';

    CALL deactivate_room(room_id, return_rep);
    room_rep = return_rep ->> 'room';
    IF (
        assert_json_value(room_rep, 'id', room_id::TEXT) AND
        assert_json_value(room_rep, 'state', state) AND
        assert_json_is_not_null(room_rep, 'timestamp') AND
        assert_json_is_not_null(return_rep, 'buildingId') AND
        assert_json_is_not_null(return_rep, 'companyId')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the the room deactivation
 * thrown when the room id does not exist, throws room_not_found
 */
DO
$$
DECLARE
    room_id BIGINT = -1;
    room_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Deactivate room, throws room_not_found test |---';

    CALL deactivate_room(room_id, room_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'room_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the room activation
 */
DO
$$
DECLARE
    room_id BIGINT = 6;
    state TEXT = 'Active';
    room_rep JSON;
BEGIN
    RAISE INFO '---| Room activation test |---';

    CALL activate_room(room_id, room_rep);
    IF (
        assert_json_value(room_rep, 'id', room_id::TEXT) AND
        assert_json_value(room_rep, 'state', state) AND
        assert_json_is_not_null(room_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the the room activation
 * thrown when the room id does not exist, throws room_not_found
 */
DO
$$
DECLARE
    room_id BIGINT = -1;
    room_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Deactivate room, throws room_not_found test |---';

    CALL activate_room(room_id, room_rep);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'room_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/**
  Test trigger, when we inactivate a room, all qr_hash in the room will be deleted.
 */
DO
$$
DECLARE
    room_id BIGINT = 1;
    room_state TEXT;
    rec RECORD;
BEGIN
    RAISE INFO '---| Trigger -> Remove qr_hashes test |---';

    UPDATE ROOM SET state = 'Inactive' WHERE id = 1
    RETURNING state INTO room_state;

    IF (room_state != 'Inactive') THEN
            RAISE EXCEPTION '-> Test failed!';
    END IF;
    FOR rec IN
        SELECT qr_hash FROM ROOM_DEVICE WHERE room = room_id
    LOOP
        IF (rec.qr_hash IS NOT NULL) THEN
            RAISE EXCEPTION '-> Test failed!';
        END IF;
    END LOOP;
    RAISE INFO '-> Test succeeded!';
    ROLLBACK;
END$$;