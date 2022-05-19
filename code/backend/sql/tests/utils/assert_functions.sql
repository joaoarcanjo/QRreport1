/*
 * Functions to assert the results returned by the tests scripts
 */

/*
 * Auxiliary function to verify the values inside a json object
 * Returns true if the value is equal to the expected and false otherwise
 */
CREATE OR REPLACE FUNCTION assert_json_value(json_val JSON, key TEXT, expected_value TEXT)
RETURNS BOOLEAN
AS
$$
BEGIN
    RETURN (json_val->>key) LIKE expected_value;
END$$LANGUAGE plpgsql;

/*
 * Auxiliary function to verify if the values are not null inside the json object
 * Returns true if is null and false otherwise
 */
CREATE OR REPLACE FUNCTION assert_json_is_not_null(json_val JSON, key TEXT)
RETURNS BOOLEAN
AS
$$
BEGIN
    RETURN (json_val->>key) IS NOT NULL;
END$$LANGUAGE plpgsql;
