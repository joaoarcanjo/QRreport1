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
 * Tests get anomalies function
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    anomaliesCollectionSizeExpected INT = 4;
    anomalies_rep JSON;
BEGIN
    RAISE INFO '---| Get anomalies test |---';

    anomalies_rep = get_anomalies(device_id, 10, 0);
    IF (
        assert_json_is_not_null(anomalies_rep, 'anomalies') AND
        assert_json_value(anomalies_rep, 'anomaliesCollectionSize', anomaliesCollectionSizeExpected::TEXT)
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

    CALL create_anomaly(anomaly_rep, device_id, new_anomaly);
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
 * Tests the creation of a new anomaly with a non unique name, throws unique-constraint
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    new_anomaly TEXT = 'The water is overflowing';
    anomaly_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Anomaly creation, throws unique-constraint test |---';

    CALL create_anomaly(anomaly_rep, device_id, new_anomaly);

    RAISE EXCEPTION '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique-constraint') THEN
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

    CALL update_anomaly(anomaly_rep, device_id, anomaly_id, new_anomaly);
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
 * Tests anomaly update with a non unique name, throws unique-constraint
 */
DO
$$
DECLARE
    device_id BIGINT = 1;
    anomaly_id BIGINT = 1;
    new_anomaly TEXT = 'The water is overflowing';
    anomaly_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Anomaly update, throws unique-constraint test |---';

    CALL update_anomaly(anomaly_rep, device_id, anomaly_id, new_anomaly);
    RAISE '-> Test failed!';
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique-constraint') THEN
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
    anomaly_id BIGINT = 3;
    anomaly TEXT = 'The toilet is clogged';
    anomaly_rep JSON;
BEGIN
    RAISE INFO '---| Anomaly delete test |---';

    CALL delete_anomaly(anomaly_rep, device_id, anomaly_id);
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