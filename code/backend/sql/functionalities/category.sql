/*
 * Category functionalities
 */

 /*
  * Auxiliary function to return the category item representation
  */
CREATE OR REPLACE FUNCTION category_item_representation(id BIGINT, name TEXT, state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', id, 'name', name, 'state', state, 'timestamp', tmstamp);
END$$ LANGUAGE plpgsql;

/**
 * Gets all the categories
 */
CREATE OR REPLACE FUNCTION get_categories(limit_rows INT DEFAULT NULL, skip_rows INT DEFAULT NULL)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    categories JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT id, name, state, timestamp FROM CATEGORY LIMIT limit_rows OFFSET skip_rows
    LOOP
        categories = array_append(categories, category_item_representation(rec.id, rec.name,rec.state, rec.timestamp));
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('categories', categories, 'categoriesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new category
 * Returns the category item representation
 * Throws exception in case there is no row added
 */
CREATE OR REPLACE PROCEDURE create_category(category_name TEXT, category_rep OUT JSON)
AS
$$
DECLARE
    category_id INT; category_state TEXT; tmstamp TIMESTAMP;
BEGIN
    INSERT INTO CATEGORY (name) VALUES (category_name)
    RETURNING id, name, state, timestamp INTO category_id, category_name, category_state, tmstamp;
    IF (category_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    category_rep = category_item_representation(category_id, category_name, category_state, tmstamp);
END$$ LANGUAGE plpgsql;

/*
 * Updates a category
 * Returns the category item representation
 * Throws exception in case there is no row affected
 */
CREATE OR REPLACE PROCEDURE update_category(category_id BIGINT, new_name TEXT, category_rep OUT JSON)
AS
$$
DECLARE
    category_state TEXT; tmstamp TIMESTAMP;
BEGIN
    IF (NOT EXISTS (SELECT id FROM DEVICE WHERE category = category_id) AND
        NOT EXISTS (SELECT category FROM PERSON_SKILL WHERE category = category_id)) THEN

        UPDATE CATEGORY SET name = new_name WHERE id = category_id
        RETURNING id, name, state, timestamp INTO category_id, new_name, category_state, tmstamp;
        IF (category_state IS NULL) THEN
            RAISE 'unknown_error_updating_resource';
        END IF;
        category_rep = category_item_representation(category_id, new_name, category_state, tmstamp);
    ELSE
        RAISE 'category_in_use';
    END IF;
END$$ LANGUAGE plpgsql;

/*
 * Deactivate a category
 * Returns the category item representation
 */
CREATE OR REPLACE PROCEDURE deactivate_category(category_id BIGINT, category_rep OUT JSON)
AS
$$
DECLARE
    category_state TEXT; category_name TEXT; tmstamp TIMESTAMP;
BEGIN
    IF (NOT EXISTS (SELECT id FROM DEVICE WHERE category = category_id) AND
        NOT EXISTS (SELECT category FROM PERSON_SKILL WHERE category = category_id)) THEN

        UPDATE CATEGORY SET state = 'Inactive', timestamp = CURRENT_TIMESTAMP WHERE id = category_id
        RETURNING id, name, state, timestamp INTO category_id, category_name, category_state, tmstamp;
        IF (category_state IS NULL) THEN
            RAISE 'unknown_error_updating_resource';
        END IF;
        category_rep = category_item_representation(category_id, category_name, category_state, tmstamp);
    ELSE
        RAISE 'category_in_use';
    END IF;
END$$
SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Activate a category
 * Returns the category item representation
 */
CREATE OR REPLACE PROCEDURE activate_category(category_id BIGINT, category_rep OUT JSON)
AS
$$
DECLARE
    category_state TEXT; category_name TEXT; tmstamp TIMESTAMP;
BEGIN
    UPDATE CATEGORY SET state = 'Active', timestamp = CURRENT_TIMESTAMP WHERE id = category_id
    RETURNING id, name, state, timestamp INTO category_id, category_name, category_state, tmstamp;
    IF (category_state IS NULL) THEN
        RAISE 'unknown_error_updating_resource';
    END IF;
    category_rep = category_item_representation(category_id, category_name, category_state, tmstamp);
END$$ LANGUAGE plpgsql;
