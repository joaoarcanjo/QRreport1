/*
 * Company functionalities
 */

/*
 * Auxiliary function to return the company item representation
 */
CREATE OR REPLACE FUNCTION company_item_representation(id BIGINT, name TEXT, state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', id, 'name', name, 'state', state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new company
 * Returns the company item representation
 * Throws exception in case there is no row added or when the unique constraint is violated
 */
CREATE OR REPLACE PROCEDURE create_company(company_rep OUT JSON, cname TEXT)
AS
$$
DECLARE
    company_id BIGINT; cstate TEXT; tmstamp TIMESTAMP;
    prev_id BIGINT; current_id BIGINT; ex_constraint TEXT;
BEGIN
    SELECT last_value INTO prev_id FROM company_id_seq;

    INSERT INTO COMPANY(name) VALUES (cname) RETURNING id, name, state, timestamp INTO company_id, cname, cstate, tmstamp;
    IF (company_id IS NULL) THEN
        RAISE 'unknown-error-writing-resource';
    END IF;
    company_rep = company_item_representation(company_id, cname, cstate, tmstamp);

EXCEPTION
    WHEN unique_violation THEN
        SELECT last_value INTO current_id FROM company_id_seq;
        IF (prev_id < current_id) THEN
            PERFORM setval('company_id_seq', current_id - 1);
        END IF;

        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'unique_company_name') THEN
            RAISE 'unique-constraint' USING DETAIL = 'company name', HINT = cname;
        END IF;
END$$ LANGUAGE plpgsql;

/*
 * Updates a company
 * Returns the updated company item representation
 * Throws exception when the company id does not exist, when company has the state set to inactive,
 * when all updatable parameters are null, when the unique constraint is violated or when there is no row updated
 */
CREATE OR REPLACE PROCEDURE update_company(company_rep OUT JSON, company_id BIGINT, new_name TEXT)
AS
$$
DECLARE
    cid BIGINT; current_name TEXT; cstate TEXT; tmstamp TIMESTAMP; ex_constraint TEXT;
BEGIN
    SELECT id, name, state, timestamp INTO cid, current_name, cstate, tmstamp FROM COMPANY WHERE id = company_id;
    IF (cid IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'company', HINT = company_id;
    END IF;

    CASE
        WHEN (cstate = 'Inactive' ) THEN
            RAISE 'inactive-resource';
        WHEN (new_name = current_name) THEN
            -- Does not update when the names are the same, returns the representation with the same values.
        ELSE
            UPDATE COMPANY SET name = new_name WHERE id = company_id
            RETURNING id, name, state, timestamp INTO company_id, current_name, cstate, tmstamp;
            IF (NOT FOUND) THEN
                RAISE 'unknown-error-writing-resource' USING DETAIL = 'updating';
            END IF;
    END CASE;

    company_rep = company_item_representation(company_id, current_name, cstate, tmstamp);
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = CONSTRAINT_NAME;
        IF (ex_constraint = 'unique_company_name') THEN
            RAISE 'unique-constraint' USING DETAIL = 'company name', HINT = new_name;
        END IF;
END$$ LANGUAGE plpgsql;

/*
 * Gets all the companies
 * Returns a list with all the company item representation
 */
CREATE OR REPLACE FUNCTION get_companies(limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    companies JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id, name, state, timestamp
        FROM COMPANY
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        companies = array_append(
            companies,
            company_item_representation(rec.id, rec.name, rec.state, rec.timestamp)
        );
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('companies', companies, 'companiesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific company
 * Returns the company representation
 * Throws exception when the company id does not exist
 */
CREATE OR REPLACE FUNCTION get_company(company_id BIGINT, limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    buildings JSON[];
    collection_size INT = 0;
    cid BIGINT; cname TEXT; cstate TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT id, name, state, timestamp INTO cid, cname, cstate, tmstamp FROM COMPANY WHERE id = company_id;
    IF (cid IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'company', HINT = company_id;
    END IF;

    /*FOR rec IN
        SELECT id
        FROM BUILDING
        WHERE company = company_id LIMIT limit_rows OFFSET skip_rows
    LOOP
        buildings = array_append(buildings, building_item_representation(project_id, rec.id));
        collection_size = collection_size + 1;
    END LOOP;*/

    RETURN json_build_object(
        'id', company_id, 'name', cname, 'state', cstate, 'timestamp', tmstamp,
        'buildings', buildings, 'buildingsCollectionSize', collection_size
    );
END$$ LANGUAGE plpgsql;

/*
 * Deactivates a specific company, this is, sets its state to Inactive
 * Returns the company item representation
 * Throws exception when the company id does not exist
 */
CREATE OR REPLACE PROCEDURE deactivate_company(company_rep OUT JSON, company_id BIGINT)
AS
$$
DECLARE
    cid BIGINT; cname TEXT; cstate TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT id, name, state, timestamp INTO cid, cname, cstate, tmstamp FROM COMPANY WHERE id = company_id;
    CASE
        WHEN (cid IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'company', HINT = company_id;
        WHEN (cstate = 'Active') THEN
            UPDATE COMPANY SET state = 'Inactive', timestamp = CURRENT_TIMESTAMP WHERE id = company_id
            RETURNING state, timestamp INTO cstate, tmstamp;
        ELSE
            -- Do nothing when it's already inactive
    END CASE;

    company_rep = company_item_representation(company_id, cname, cstate, tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Activates a specific company, this is, sets its state to Active
 * Returns the company item representation
 * Throws exception when the company id does not exist
 */
CREATE OR REPLACE PROCEDURE activate_company(company_rep OUT JSON, company_id BIGINT)
AS
$$
DECLARE
    cid BIGINT; cname TEXT; cstate TEXT; tmstamp TIMESTAMP;
BEGIN
    SELECT id, name, state, timestamp INTO cid, cname, cstate, tmstamp FROM COMPANY WHERE id = company_id;
    CASE
        WHEN (cid IS NULL) THEN
            RAISE 'resource-not-found' USING DETAIL = 'company', HINT = company_id;
        WHEN (cstate = 'Inactive') THEN
            UPDATE COMPANY SET state = 'Active', timestamp = CURRENT_TIMESTAMP WHERE id = company_id
            RETURNING state, timestamp INTO cstate, tmstamp;
        ELSE
            -- Do nothing when it's already active
    END CASE;

    company_rep = company_item_representation(company_id, cname, cstate, tmstamp);
END$$ LANGUAGE plpgsql;