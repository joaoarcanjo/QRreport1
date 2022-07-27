/*
 * Anomaly functionalities
 */

/*
 * Auxiliary function to return the anomaly item representation
 */
CREATE OR REPLACE FUNCTION anomaly_item_representation(a_id BIGINT, a_anomaly TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', a_id, 'anomaly', a_anomaly);
END$$ LANGUAGE plpgsql;

/*
 * Get all anomalies
 */
CREATE OR REPLACE FUNCTION get_anomalies(device_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    anomalies JSON[];
    collection_size INT = 0;
    rec RECORD;
BEGIN
    PERFORM device_exists(device_id);
    FOR rec IN
        SELECT id, anomaly FROM ANOMALY WHERE device = device_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        anomalies = array_append(anomalies, anomaly_item_representation(rec.id, rec.anomaly));
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM ANOMALY WHERE device = device_id;
    RETURN json_build_object('anomalies', anomalies, 'anomaliesCollectionSize', collection_size,
        'deviceState', (SELECT state FROM DEVICE WHERE id = device_id));
END$$ LANGUAGE plpgsql;

/*
 * Creates a new anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row added or when the unique constraint is violated
 */
CREATE OR REPLACE PROCEDURE create_anomaly(anomaly_rep OUT JSON, device_id BIGINT, new_anomaly TEXT)
AS
$$
DECLARE anomaly_id BIGINT; prev_id BIGINT; current_id BIGINT; ex_constraint TEXT;
BEGIN
    PERFORM device_exists(device_id);
    INSERT INTO ANOMALY (device, anomaly) VALUES (device_id, new_anomaly)
    RETURNING id INTO anomaly_id;

    anomaly_rep = anomaly_item_representation(anomaly_id, new_anomaly);
EXCEPTION
    WHEN unique_violation THEN
        SELECT last_value INTO current_id FROM anomaly_id_seq;
        IF (prev_id < current_id) THEN
            PERFORM setval('anomaly_id_seq', current_id - 1);
        END IF;

        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'anomaly_pkey') THEN
            RAISE 'unique-constraint' USING DETAIL = 'anomaly', HINT = new_anomaly;
        END IF;
END$$
LANGUAGE plpgsql;

/*
 * Updates a anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row added or when the unique constraint is violated
 */
CREATE OR REPLACE PROCEDURE update_anomaly(anomaly_rep OUT JSON, device_id BIGINT, anomaly_id BIGINT, new_anomaly TEXT)
AS
$$
DECLARE ex_constraint TEXT;
BEGIN
    PERFORM device_exists(device_id);
    UPDATE ANOMALY SET anomaly = new_anomaly WHERE id = anomaly_id AND device = device_id;

    anomaly_rep = anomaly_item_representation(anomaly_id, new_anomaly);
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'anomaly_pkey') THEN
            RAISE 'unique-constraint' USING DETAIL = 'anomaly', HINT = new_anomaly;
        END IF;
END$$
LANGUAGE plpgsql;

/*
 * Deletes a anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row deleted.
 */
CREATE OR REPLACE PROCEDURE delete_anomaly(anomaly_rep OUT JSON, device_id BIGINT, anomaly_id BIGINT)
AS
$$
DECLARE anomaly_value TEXT;
BEGIN
    PERFORM device_exists(device_id);
    IF (NOT EXISTS(SELECT id FROM ANOMALY WHERE id = anomaly_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'anomaly', HINT = anomaly_id;
    END IF;
    DELETE FROM ANOMALY WHERE id = anomaly_id AND device = device_id RETURNING anomaly INTO anomaly_value;

    anomaly_rep = anomaly_item_representation(anomaly_id, anomaly_value);
END$$ LANGUAGE plpgsql;