/*
 * Script to test all the implemented anomaly functionalities
 */

/*
 * Tests the anomaly representation function
 */
DO
$$
DECLARE
    anomaly_id BIGINT = 1;
    anomaly TEXT = 'Anomaly test';
    anomaly_rep JSON;
BEGIN
    RAISE INFO '---| Anomaly item representation test |---';

    anomaly_rep = anomaly_item_representation(anomaly_id, anomaly);
    IF (
        assert_json_value(anomaly_rep, 'id', anomaly_id::TEXT) AND
        assert_json_value(anomaly_rep, 'anomaly', anomaly)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
END$$;

/*
 * Tests the creation of a new anomaly
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    new_anomaly TEXT = 'Anomaly test';
    anomaly_rep JSON;
BEGIN
    RAISE INFO '---| Anomaly creation test |---';

    CALL create_anomaly(device_id, new_anomaly, anomaly_rep);
    IF (
        assert_json_is_not_null(anomaly_rep, 'id') AND
        assert_json_value(anomaly_rep, 'anomaly', new_anomaly)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the creation of a new anomaly with a non unique name, throws unique_anomaly_name
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    new_anomaly TEXT = 'Cheira a queimado';
    anomaly_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Anomaly creation, throws unique_anomaly_name test |---';

    CALL create_anomaly(device_id, new_anomaly, anomaly_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_anomaly_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the update of a anomaly
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    anomaly_id BIGINT = 1;
    new_anomaly TEXT = 'Anomaly test';
    anomaly_rep JSON;
BEGIN
    RAISE INFO '---| Anomaly update test |---';

    CALL update_anomaly(device_id, anomaly_id, new_anomaly, anomaly_rep);
    IF (
        assert_json_is_not_null(anomaly_rep, 'id') AND
        assert_json_value(anomaly_rep, 'anomaly', new_anomaly)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests anomaly update with a non unique name, throws unique_anomaly_name
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    anomaly_id BIGINT = 1;
    new_anomaly TEXT = 'Cheira a queimado';
    anomaly_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Anomaly update, throws unique_anomaly_name test |---';

    CALL update_anomaly(device_id, anomaly_id, new_anomaly, anomaly_rep);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_anomaly_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE EXCEPTION '-> Test failed!';
        END IF;
END$$;


/*
 * Tests delete anomaly
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    anomaly_id BIGINT = 1;
    anomaly TEXT = 'Não funciona';
    anomaly_rep JSON;
BEGIN
    RAISE INFO '---| Anomaly delete test |---';

    CALL delete_anomaly(device_id, anomaly_id, anomaly_rep);
    IF (
        assert_json_is_not_null(anomaly_rep, 'id') AND
        assert_json_value(anomaly_rep, 'anomaly', anomaly)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE EXCEPTION '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;