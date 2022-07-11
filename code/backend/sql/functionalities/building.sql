/*
 * Building functionalities
 */

 /*
  * Auxiliary function to return the building item representation
  */
CREATE OR REPLACE FUNCTION building_item_representation(id BIGINT, name TEXT, floors INT, state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', id, 'name', name, 'floors', floors, 'state', state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new building
 * Returns the building item representation
 * Throws exception in case there is no row added, when manager is invalid or when already exist a building
 * with the same name in the same company.
 */
CREATE OR REPLACE PROCEDURE create_building(
    building_rep OUT JSON,
    company_id BIGINT,
    building_name TEXT,
    floors INT,
    manager_id UUID
)
AS
$$
DECLARE
    building_id BIGINT; tmstamp TIMESTAMP; building_state TEXT;
BEGIN
    -- Verify if the person has the manager role and if belongs to the company
    IF ('manager' != ANY(get_person_roles(manager_id))) THEN
        RAISE 'invalid-role' USING DETAIL = 'building-manager';
    ELSEIF NOT EXISTS(SELECT person FROM PERSON_COMPANY WHERE person = manager_id AND company = company_id) THEN
        RAISE 'invalid-company' USING DETAIL = 'building-manager';
    END IF;

    IF EXISTS (SELECT id FROM BUILDING WHERE company = company_id AND name = building_name) THEN
        RAISE 'unique-constraint' USING DETAIL = 'building name', HINT = building_name, ERRCODE = 'unique_violation';
    END IF;
    INSERT INTO BUILDING (name, floors, company, manager) VALUES (building_name, floors, company_id, manager_id)
    RETURNING id, timestamp, state INTO building_id, tmstamp, building_state;

    building_rep = building_item_representation(building_id, building_name, floors, building_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Updates a building
 * Returns the updated building item representation
 * Throws exception when the building id does not exist, when both parameters are null, when the building is inactive
 * or when already exists a building with a name equal to the new_name
 */
CREATE OR REPLACE PROCEDURE update_building(
    building_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    new_name TEXT DEFAULT NULL,
    new_floors INT DEFAULT NULL
)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state, timestamp INTO building_name, building_floors, building_state, tmstamp
    FROM BUILDING WHERE id = building_id;
    IF (building_name IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'building', HINT = building_id;
    END IF;

    CASE
        WHEN (building_state = 'inactive') THEN
            RAISE 'inactive-resource';
        WHEN (new_name IS NOT NULL AND EXISTS ((SELECT id FROM BUILDING WHERE company = company_id AND name = new_name AND id != building_id))) THEN
            RAISE 'unique-constraint' USING DETAIL = 'building name', HINT = new_name, ERRCODE = 'unique_violation';
        WHEN ((new_name IS NULL AND new_floors IS NULL) OR (new_name = building_name AND new_floors = building_floors)) THEN
            -- Does not update when the new values are equal to the current ones, returns the representation with the same values.
        ELSE
            --update both, name and floors
            IF (new_name IS NOT NULL AND new_floors IS NOT NULL) THEN
                UPDATE BUILDING SET name = new_name, floors = new_floors WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            --update just the name
            ELSEIF (new_name IS NOT NULL AND building_name != new_name) THEN
                UPDATE BUILDING SET name = new_name WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            --update just the number of floors
            ELSEIF (new_floors IS NOT NULL AND building_floors != new_floors) THEN
                UPDATE BUILDING SET floors = new_floors WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            END IF;
        END CASE;

        building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Gets all the buildings of a company
 * Returns a list with all the buildings item representation
 */
CREATE OR REPLACE FUNCTION get_buildings(company_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    buildings JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id, name, floors, state, timestamp FROM BUILDING
        WHERE company = company_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        buildings = array_append(
            buildings,
            building_item_representation(rec.id, rec.name, rec.floors, rec.state, rec.timestamp)
        );
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM BUILDING WHERE company = company_id;
    RETURN json_build_object('buildings', buildings, 'buildingsCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific building
 * Returns the building representation
 * Throws exception when the building id does not exist
 */
CREATE OR REPLACE FUNCTION get_building(
    company_id BIGINT,
    building_id BIGINT,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    rooms JSON[];
    collection_size INT = 0;
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
    manager_id UUID;
BEGIN
    SELECT name, floors, state, timestamp, manager FROM BUILDING WHERE id = building_id AND company = company_id
    INTO building_name, building_floors, building_state, tmstamp, manager_id;
    IF (building_name IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'building', HINT = building_id;
    END IF;

    -- Get all rooms that belong to the building
    FOR rec IN
        SELECT id, name, floor, state, timestamp FROM ROOM
        WHERE building = building_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        rooms = array_append(rooms, room_item_representation(rec.id, rec.name, rec.floor, rec.state, rec.timestamp));
    END LOOP;

    SELECT COUNT(id) INTO collection_size FROM ROOM WHERE building = building_id;

    RETURN json_build_object(
        'building', building_item_representation(building_id, building_name, building_floors, building_state, tmstamp),
        'rooms', json_build_object('rooms', rooms, 'roomsCollectionSize', collection_size),
        'manager', person_item_representation(manager_id)
    );
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Deactivates a specific building, this is, sets its state to Inactive
 * Returns the building item representation
 * Throws exception when the building id does not exist
 */
CREATE OR REPLACE PROCEDURE deactivate_building(building_rep OUT JSON, company_id BIGINT, building_id BIGINT)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state INTO building_name, building_floors, building_state
    FROM BUILDING WHERE id = building_id AND company = company_id;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'building', HINT = building_id;
        WHEN (building_state = 'active') THEN
            UPDATE BUILDING SET state = 'inactive', timestamp = CURRENT_TIMESTAMP
            WHERE id = building_id AND company = COMPANY_ID
            RETURNING state, timestamp INTO building_state, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Activates a specific building, this is, sets its state to Active
 * Returns the building item representation
 * Throws exception when the building id does not exist
 */
CREATE OR REPLACE PROCEDURE activate_building(building_rep OUT JSON, company_id BIGINT, building_id BIGINT)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state INTO building_name, building_floors, building_state
    FROM BUILDING WHERE id = building_id AND company = company_id;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'building', HINT = building_id;
        WHEN (building_state = 'inactive') THEN
            UPDATE BUILDING SET state = 'active', timestamp = CURRENT_TIMESTAMP
            WHERE id = building_id AND company = company_id
            RETURNING state, timestamp INTO building_state, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Changes the manager of a specific building
 * Returns a representation with the id and name of the building and the new manager id
 * Throws exception when the building id does not exist or when the new manager is invalid
 * because doesn't have the necessary role
 */
CREATE OR REPLACE PROCEDURE change_building_manager(
    building_rep OUT JSON,
    company_id BIGINT,
    building_id BIGINT,
    new_manager UUID
)
AS
$$
DECLARE
    building_name TEXT; building_state TEXT; curr_manager UUID;
BEGIN
    SELECT name, manager, building_state INTO building_name, curr_manager, building_state FROM BUILDING
    WHERE id = building_id AND company = company_id;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'building', HINT = building_id;
        WHEN (curr_manager = new_manager) THEN
            -- Do nothing when try to change the manager to the same manager
        ELSE
            -- Verify if the person has the manager role and if belongs to the company
            IF (NOT 'manager' = ANY(get_person_roles(new_manager))) THEN
                RAISE 'invalid-role' USING DETAIL = 'building-manager';
            ELSEIF NOT EXISTS(SELECT person FROM PERSON_COMPANY WHERE person = new_manager AND company = company_id) THEN
                RAISE 'invalid-company' USING DETAIL = 'building-manager';
            END IF;
            UPDATE BUILDING SET manager = new_manager WHERE id = building_id AND company = company_id
            RETURNING manager INTO new_manager;
    END CASE;
    building_rep = json_build_object('id', building_id, 'name', building_name, 'manager', new_manager);
END$$
-- SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/**
  Trigger to change the state of all rooms belonging to the building whose state was changed
 */
CREATE OR REPLACE FUNCTION update_rooms_states() RETURNS TRIGGER
AS
$$
BEGIN
	IF NEW.state != OLD.state THEN
        UPDATE ROOM SET state = NEW.state, timestamp = CURRENT_TIMESTAMP WHERE building = NEW.id;
	END IF;
	RETURN NEW;
END$$LANGUAGE plpgsql;

CREATE TRIGGER change_building_state_trigger
    BEFORE UPDATE ON BUILDING
    FOR EACH ROW
    EXECUTE PROCEDURE update_rooms_states();