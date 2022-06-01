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
    company_id BIGINT,
    bname TEXT,
    floors INT,
    manager UUID,
    building_rep OUT JSON
)
AS
$$
DECLARE
    building_id BIGINT; tmstamp TIMESTAMP; building_state TEXT; prev_id BIGINT;
BEGIN
    SELECT last_value INTO prev_id FROM building_id_seq;
    -- verify if the person has the manager role and if the manager belongs to the company
    IF NOT EXISTS (SELECT role FROM PERSON_ROLE
        WHERE person = (SELECT person FROM PERSON_COMPANY WHERE person = manager AND company = company_id)
        AND role = (SELECT id FROM ROLE WHERE name = 'manager') FOR SHARE) THEN
        raise 'manager_not_valid';
    END IF;
    -- this no other building is added until the transaction ends
    -- LOCK TABLE BUILDING IN SHARE ROW EXCLUSIVE MODE;
    -- ensure does not exist two buildings with the same name in the same company
    IF EXISTS (SELECT id FROM BUILDING WHERE company = company_id AND name = bname) THEN
        RAISE 'unique_building_name' USING ERRCODE = 'unique_violation';
    END IF;
    INSERT INTO BUILDING (name, floors, company, manager) VALUES (bname, floors, company_id, manager)
    RETURNING id, timestamp, state INTO building_id, tmstamp, building_state;
    IF (building_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    building_rep = building_item_representation(building_id, bname, floors, building_state, tmstamp);

END$$ LANGUAGE plpgsql;

/*
 * Updates a building
 * Returns the updated building item representation
 * Throws exception when the building id does not exist, when both parameters are null, when the building is inactive
 * or when already exists a building with a name equal to the new_name
 */
 CREATE OR REPLACE PROCEDURE update_building(
    company_id BIGINT,
    building_id BIGINT,
    building_rep OUT JSON,
    new_name TEXT DEFAULT NULL,
    new_floors INT DEFAULT NULL
)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state, timestamp INTO building_name, building_floors, building_state, tmstamp
    FROM BUILDING WHERE id = building_id FOR SHARE;
    IF (building_name IS NULL) THEN
        RAISE 'building_not_found';
    END IF;

    CASE
        WHEN (building_state = 'Inactive' ) THEN
            RAISE 'inactive_building';
        WHEN (new_name IS NULL AND new_floors IS NULL) THEN
            RAISE 'update_parameters_all_null';
        WHEN (new_name IS NOT NULL AND EXISTS ((SELECT id FROM BUILDING WHERE company = company_id AND name = new_name AND id != building_id))) THEN
            RAISE 'unique_building_name' USING ERRCODE = 'unique_violation';
        WHEN ((new_name IS NULL AND new_floors = building_floors) OR (new_floors IS NULL AND new_name = building_name)
            OR (new_name = building_name AND new_floors = building_floors)) THEN
            -- Does not update when the new values are equals to current values, returns the representation with the same values.
        ELSE
            --update both, name and floors
            IF (new_name IS NOT NULL AND new_floors IS NOT NULL) THEN
                UPDATE BUILDING SET name = new_name, floors = new_floors WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            --update just the name
            ELSEIF (new_name IS NOT NULL) THEN
                UPDATE BUILDING SET name = new_name WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            --update just the number of floors
            ELSE
                UPDATE BUILDING SET floors = new_floors WHERE id = building_id
                RETURNING name, floors, timestamp INTO building_name, building_floors, tmstamp;
            END IF;
            IF (building_name IS NULL) THEN
                RAISE 'unknown_error_updating_resource';
            END IF;
        END CASE;

        building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$ LANGUAGE plpgsql;

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
        SELECT id, name, floors, state, timestamp
        FROM BUILDING WHERE company = company_id
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        buildings = array_append(
            buildings,
            building_item_representation(rec.id, rec.name, rec.floors, rec.state, rec.timestamp)
        );
       collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('buildings', buildings, 'buildingsCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific building
 * Returns the building representation
 * Throws exception when the building id does not exist
 */

CREATE OR REPLACE FUNCTION get_building(company_id BIGINT, building_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    rooms JSON[];
    collection_size INT = 0;
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
    manager_id UUID; manager_name TEXT; manager_phone TEXT; manager_email TEXT;
BEGIN
    SELECT name, floors, state, timestamp, manager FROM BUILDING
    WHERE id = building_id AND company = company_id FOR SHARE
    INTO building_name, building_floors, building_state, tmstamp, manager_id;
    IF (building_name IS NULL) THEN
        RAISE 'building_not_found';
    END IF;

    --get all rooms that belong to the building
    FOR rec IN
        SELECT id, name, state FROM ROOM
        WHERE building = building_id LIMIT limit_rows OFFSET skip_rows
    LOOP
        rooms = array_append(rooms, room_item_representation(rec.id, rec.name, rec.state));
        collection_size = collection_size + 1;
    END LOOP;

    SELECT name, phone, email FROM PERSON
    WHERE id = manager_id INTO manager_name, manager_phone, manager_email;

    RETURN json_build_object(
        'id', building_id, 'name', building_name, 'floors', building_floors, 'state', building_state, 'timestamp', tmstamp,
        'rooms', rooms, 'roomsCollectionSize', collection_size,
        'manager', person_item_representation(manager_id, manager_name, manager_phone, manager_email)
    );
END$$ LANGUAGE plpgsql;

/*
 * Deactivates a specific building, this is, sets its state to Inactive
 * Returns the building item representation
 * Throws exception when the building id does not exist
 */
CREATE OR REPLACE PROCEDURE deactivate_building(company_id BIGINT, building_id BIGINT, building_rep OUT JSON)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state, timestamp INTO building_name, building_floors, building_state, tmstamp
    FROM BUILDING WHERE id = building_id AND company = company_id FOR UPDATE;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'building_not_found';
        WHEN (building_state = 'Active') THEN
            UPDATE BUILDING SET state = 'Inactive', timestamp = CURRENT_TIMESTAMP
            WHERE id = building_id AND company = COMPANY_ID
            RETURNING state, timestamp INTO building_state, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Activates a specific building, this is, sets its state to Active
 * Returns the building item representation
 * Throws exception when the building id does not exist
 */
CREATE OR REPLACE PROCEDURE activate_building(company_id BIGINT, building_id BIGINT, building_rep OUT JSON)
AS
$$
DECLARE
    building_name TEXT; building_floors INT; building_state TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT name, floors, state, timestamp INTO building_name, building_floors, building_state, tmstamp
    FROM BUILDING WHERE id = building_id AND company = company_id FOR UPDATE;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'building_not_found';
        WHEN (building_state = 'Inactive') THEN
            UPDATE BUILDING SET state = 'Active', timestamp = CURRENT_TIMESTAMP
            WHERE id = building_id AND company = company_id
            RETURNING state, timestamp INTO building_state, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    building_rep = building_item_representation(building_id, building_name, building_floors, building_state, tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Changes the manager of a specific building
 * Returns a representation with the id and name of the building and the new manager id
 * Throws exception when the building id does not exist or when the new manager is invalid
 * because doesn't have the necessary role
 */
CREATE OR REPLACE PROCEDURE change_building_manager(
    company_id BIGINT,
    building_id BIGINT,
    new_manager UUID,
    building_rep OUT JSON
)
AS
$$
DECLARE
    building_name TEXT; building_state TEXT; curr_manager UUID;
BEGIN
    IF NOT EXISTS (SELECT role FROM PERSON_ROLE
        WHERE person = (SELECT person FROM PERSON_COMPANY WHERE person = new_manager AND company = company_id)
        AND role = (SELECT id FROM ROLE WHERE name = 'manager') FOR SHARE) THEN
        raise 'manager_not_valid';
    END IF;
    SELECT name, manager, building_state INTO building_name, curr_manager, building_state FROM BUILDING
    WHERE id = building_id AND company = company_id FOR UPDATE;
    CASE
        WHEN (building_name IS NULL) THEN
            RAISE 'building_not_found';
        WHEN (curr_manager = new_manager) THEN
            -- Do nothing when try to change the manager to the same manager
        ELSE
            UPDATE BUILDING SET manager = new_manager WHERE id = building_id AND company = company_id
            RETURNING manager INTO new_manager;
            IF (new_manager IS NULL) THEN
                RAISE 'unknown_error_updating_resource';
            END IF;
    END CASE;
    building_rep =  json_build_object('id', building_id, 'name', building_name, 'manager', new_manager);
END$$ LANGUAGE plpgsql;