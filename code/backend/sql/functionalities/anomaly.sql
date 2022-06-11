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
    FOR rec IN
        SELECT id, anomaly FROM ANOMALY WHERE device = device_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        anomalies = array_append(anomalies, anomaly_item_representation(rec.id, rec.anomaly));
        collection_size = collection_size + 1;
    END LOOP;
    RETURN json_build_object('anomalies', anomalies, 'anomaliesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row added or when the unique constraint is violated
 */
CREATE OR REPLACE PROCEDURE create_anomaly(device_id BIGINT, new_anomaly TEXT, anomaly_rep OUT JSON)
AS
$$
DECLARE
    anomaly_id BIGINT;
BEGIN
    IF EXISTS (SELECT id FROM ANOMALY WHERE device = device_id AND anomaly = new_anomaly) THEN
        RAISE unique_violation USING MESSAGE = 'unique_anomaly_name';
    END IF;
    anomaly_id = (SELECT MAX(id) + 1 FROM ANOMALY a WHERE a.device = device_id);
    IF (anomaly_id IS NULL) THEN
        anomaly_id = 1;
    END IF;
    INSERT INTO ANOMALY (id, device, anomaly) VALUES (anomaly_id, device_id, new_anomaly)
    RETURNING id, anomaly INTO anomaly_id, new_anomaly;
    IF (anomaly_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    anomaly_rep = anomaly_item_representation(anomaly_id, new_anomaly);
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Updates a anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row added or when the unique constraint is violated
 */
CREATE OR REPLACE PROCEDURE update_anomaly(device_id BIGINT, anomaly_id BIGINT, new_anomaly TEXT, anomaly_rep OUT JSON)
AS
$$
BEGIN
    IF EXISTS (SELECT id FROM ANOMALY WHERE device = device_id AND anomaly = new_anomaly) THEN
        RAISE unique_violation USING MESSAGE = 'unique_anomaly_name';
    END IF;
    UPDATE ANOMALY SET anomaly = new_anomaly WHERE id = anomaly_id AND device = device_id
    RETURNING id, device, anomaly INTO anomaly_id, device_id, new_anomaly;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    anomaly_rep = anomaly_item_representation(anomaly_id, new_anomaly);
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Deletes a anomaly
 * Returns the anomaly item representation
 * Throws exception in case there is no row deleted.
 */
CREATE OR REPLACE PROCEDURE delete_anomaly(device_id BIGINT, anomaly_id BIGINT, anomaly_rep OUT JSON)
AS
$$
DECLARE
    anomaly_value TEXT;
BEGIN
    DELETE FROM ANOMALY WHERE id = anomaly_id AND device = device_id
    RETURNING id, device, anomaly INTO anomaly_id, device_id, anomaly_value;
    IF (device_id IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    anomaly_rep = anomaly_item_representation(anomaly_id, anomaly_value);
END$$ LANGUAGE plpgsql;