/*
 * Anomaly functionalities
 */

/*
 * Auxiliary function to return the anomaly item representation
 */
CREATE OR REPLACE FUNCTION anomaly_item_representation (a_id BIGINT, a_anomaly TEXT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', a_id, 'anomaly', a_anomaly);
END$$ LANGUAGE plpgsql;