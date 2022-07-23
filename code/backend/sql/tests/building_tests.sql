/*
 * Script to test all the implemented building functionalities
 */

/*
 * Tests the building representation function
 */
DO
$$
DECLARE
    building_id BIGINT = 1;
    building_name TEXT = 'Building name test';
    building_floors INT = 12;
    building_state TEXT = 'active';
    building_timestamp TIMESTAMP;
    building_rep JSON;
BEGIN
    RAISE INFO '---| Building item representation test |---';

    building_timestamp = CURRENT_TIMESTAMP;
    building_rep = building_item_representation(
        building_id, building_name, building_floors, building_state, building_timestamp);
    IF (
        assert_json_value(building_rep, 'id', building_id::TEXT) AND
        assert_json_value(building_rep, 'name', building_name) AND
        assert_json_value(building_rep, 'floors', building_floors::TEXT) AND
        assert_json_value(building_rep, 'state', building_state) AND
        assert_json_is_not_null(building_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new building
 */
DO
$$
DECLARE
    id BIGINT;
    name TEXT = 'Building name';
    floors INT = 12;
    company_id BIGINT = 1;
    manager UUID = 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057';
    state TEXT = 'active';
    building_rep JSON;
BEGIN
    RAISE INFO '---| Building creation test |---';

    CALL create_building(building_rep, company_id, name, floors, manager);
    id = building_rep->>'id';
    IF (
        assert_json_is_not_null(building_rep, 'id') AND
        assert_json_value(building_rep, 'name', name) AND
        assert_json_value(building_rep, 'floors', floors::TEXT) AND
        assert_json_value(building_rep, 'state', state)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;

   -- Remove sequence inc
   IF (id = 1) THEN
        ALTER SEQUENCE building_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('building_id_seq', (SELECT last_value FROM building_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new building with a non unique name, throws unique_building_name
 */
DO
$$
DECLARE
    name TEXT = 'A';
    floors INT = 12;
    company_id BIGINT = 1;
    manager UUID = 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057';
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Building creation, throws unique_building_name test |---';

    CALL create_building(building_rep, company_id, name, floors, manager);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique-constraint') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the creation of a new building, throws invalid-role
 * -> Doesn't have the manager role
 */
DO
$$
DECLARE
    name TEXT = 'JD';
    floors INT = 12;
    company_id BIGINT = 1;
    manager UUID = 'c2b393be-d720-4494-874d-43765f5116cb'; -- ZÉ MANUEL, employee
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Building creation throws invalid-role, does not have the manager role test |---';

    CALL create_building(building_rep, company_id, name, floors, manager);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'invalid-role') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the creation of a new building, throws manager_not_valid
 * -> Belongs to other company, but have the manager role
 */
DO
$$
DECLARE
    name TEXT = 'JD';
    floors INT = 12;
    company_id BIGINT = 1;
    manager UUID = '9c06c8f3-ceda-48c5-99a7-29903a921a5b';
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Building creation throws invalid-company, belongs to other company test |---';

    CALL create_building(building_rep, company_id, name, floors, manager);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'invalid-company') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the valid update of a building name and floors
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    name TEXT = 'A.v2.0';
    floors INT = 12;
    building_rep JSON;
BEGIN
    RAISE INFO '---| Update building name and floors test |---';

    CALL update_building(building_rep, company_id, id, name, floors);

    IF (
        assert_json_value(building_rep, 'name', name) AND
        assert_json_value(building_rep, 'floors', floors::TEXT))
    THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the valid update of a building name
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    name TEXT = 'A.v2.0';
    building_rep JSON;
BEGIN
    RAISE INFO '---| Update building name test |---';

    CALL update_building(building_rep, company_id, id, new_name:= name);

    IF (assert_json_value(building_rep, 'name', name)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the valid update of a building floors
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    floors INT = 22;
    building_rep JSON;
BEGIN
    RAISE INFO '---| Update building floors test |---';

    CALL update_building(building_rep, company_id, id, new_floors:= floors);

    IF (assert_json_value(building_rep, 'floors', floors::TEXT)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests update not found building, throws building_not_found
 */
DO
$$
DECLARE
    id BIGINT = -1;
    company_id BIGINT = 1;
    building_rep JSON;
    name TEXT = 'A.v2.0';
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update building, throws resource-not-found test |---';

     CALL update_building(building_rep, company_id, id, new_name := name);
     RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests update inactive building, throws inactive_building
 */
DO
$$
DECLARE
    id BIGINT = 3;
    company_id BIGINT = 1;
    building_rep JSON;
    name TEXT = 'A.v2.0';
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update building, throws inactive_building test |---';

    CALL update_building(building_rep, company_id, id, new_name := name);
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
 * Tests update building name to a name already in use, throws inactive_building
 */
DO
$$
DECLARE
    id BIGINT = 2;
    company_id BIGINT = 1;
    building_rep JSON;
    name TEXT = 'A';
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update building, throws unique-constraint test |---';

    CALL update_building(building_rep, company_id, id, new_name := name);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique-constraint') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the get buildings function
 */
DO
$$
DECLARE
    buildings_col_size INT = 3;
    company_id BIGINT = 1;
    building_rep JSON;
BEGIN
    RAISE INFO '---| Get buildings test |---';

    SELECT get_buildings(company_id, 10, 0) INTO building_rep;

    IF (assert_json_is_not_null(building_rep, 'buildings') AND
        assert_json_value(building_rep, 'buildingsCollectionSize', buildings_col_size::TEXT)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the get building function
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    return_rep JSON;
    rooms_rep JSON;
    rooms_col_size INT = 2;
BEGIN
    RAISE INFO '---| Get building test |---';

    SELECT get_building(company_id, id, 10, 0) INTO return_rep;
    rooms_rep = return_rep ->> 'rooms';
    IF (
        assert_json_is_not_null(return_rep, 'building') AND
        assert_json_is_not_null(rooms_rep, 'rooms') AND
        assert_json_value(rooms_rep, 'roomsCollectionSize', rooms_col_size::TEXT) AND
        assert_json_is_not_null(return_rep, 'manager')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests in the get building function, throws building_not_found
 */
DO
$$
DECLARE
    id BIGINT =-1;
    company_id BIGINT = 1;
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Get company with non existent id test |---';

    SELECT get_building(company_id, id, 10, 0) INTO company_rep;
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the building deactivation
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    state TEXT = 'inactive';
    building_rep JSON;
BEGIN
    RAISE INFO '---| Building deactivation test |---';

    CALL deactivate_building(building_rep, company_id, id);
    IF (
        assert_json_value(building_rep, 'id', id::TEXT) AND
        assert_json_value(building_rep, 'state', state) AND
        assert_json_is_not_null(building_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the exception thrown when the building id does not exist, throws resource-not-found
 */
DO
$$
DECLARE
    id BIGINT = -1;
    company_id BIGINT = 1;
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Deactivate building, throws resource-not-found test |---';

    CALL deactivate_building(building_rep, company_id, id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the building activation
 */
DO
$$
DECLARE
    id BIGINT = 3;
    company_id BIGINT = 1;
    state TEXT = 'active';
    building_rep JSON;
BEGIN
    RAISE INFO '---| Building activation test |---';

    CALL activate_building(building_rep, company_id, id);

    IF (
        assert_json_value(building_rep, 'id', id::TEXT) AND
        assert_json_value(building_rep, 'state', state) AND
        assert_json_is_not_null(building_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the exception thrown when the building id does not exist, throws resource-not-found
 */
DO
$$
DECLARE
    id BIGINT = -1;
    company_id BIGINT = 1;
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Activate building, throws resource-not-found test |---';

    CALL activate_building(building_rep, company_id, id);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'resource-not-found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests changing manager
 */
DO
$$
DECLARE
    id BIGINT = 2;
    company_id BIGINT = 1;
    new_manager UUID = '4b341de0-65c0-4526-8898-24de463fc315';
    building_rep JSON;
BEGIN
    RAISE INFO '---| Changing manager test |---';

    CALL change_building_manager(building_rep, company_id, id, new_manager);
    IF (
        assert_json_value(building_rep, 'id', id::TEXT) AND
        assert_json_value(building_rep, 'manager', new_manager::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the exception thrown when changing for a invalid manager, throws invalid-company
 * -> Belongs to other company, but have the manager role
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    new_manager UUID = '9c06c8f3-ceda-48c5-99a7-29903a921a5b';
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Activate building, throws invalid-company test |---';

    CALL change_building_manager(building_rep, company_id, id, new_manager);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'invalid-company') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the exception thrown when changing for a invalid manager, throws invalid-role
 * -> Doesn't have the manager role
 */
DO
$$
DECLARE
    id BIGINT = 1;
    company_id BIGINT = 1;
    new_manager UUID = 'c2b393be-d720-4494-874d-43765f5116cb';
    building_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Activate building, throws invalid-role test |---';

    CALL change_building_manager(building_rep, company_id, id, new_manager);
    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'invalid-role') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/**
 * Tests trigger to change the state of all rooms belonging to a building whose status has changed.
 */
DO
$$
DECLARE
    building_id BIGINT = 1;
    company_id BIGINT = 1;
    building_state TEXT;
    rec RECORD;
BEGIN
    RAISE INFO '---| Trigger -> Change rooms states test |---';

    UPDATE BUILDING SET state = 'inactive' WHERE id = building_id AND company = company_id
    RETURNING state INTO building_state;

    IF (building_state != 'inactive') THEN
            RAISE EXCEPTION '-> Test failed!';
    END IF;
    FOR rec IN
        SELECT state FROM ROOM WHERE building = building_id
    LOOP
        IF (rec.state != 'inactive') THEN
            RAISE EXCEPTION '-> Test failed!';
        END IF;
    END LOOP;
    RAISE INFO '-> Test succeeded!';
    ROLLBACK;
END$$;