/*
 * Category functionalities
 */

/*
 * Auxiliary function to verify if a category exists
 */
CREATE OR REPLACE FUNCTION category_exists(category_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT id FROM CATEGORY WHERE id = category_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'category', HINT = category_id;
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

 /*
  * Auxiliary function to return the category item representation by id
  */
CREATE OR REPLACE FUNCTION category_item_representation(category_id BIGINT)
RETURNS JSON
AS
$$
DECLARE category_name TEXT; category_state TEXT; tmstamp TIMESTAMP; item JSON;
BEGIN
    SELECT name, state, timestamp INTO category_name, category_state, tmstamp
    FROM CATEGORY WHERE id = category_id;
    item = json_build_object('id', category_id, 'name', category_name, 'state', category_state, 'timestamp', tmstamp);
    RETURN json_build_object('category', item,
        'inUse', (EXISTS(SELECT person FROM PERSON_SKILL WHERE category = category_id) OR (EXISTS(SELECT id FROM DEVICE WHERE category = category_id)))
    );
END$$ LANGUAGE plpgsql;

 /*
  * Auxiliary function to return the category item representation
  */
CREATE OR REPLACE FUNCTION category_item_representation(cid BIGINT, name TEXT, state TEXT, tmstamp TIMESTAMP)
RETURNS JSON
AS
$$
DECLARE item JSON;
BEGIN
    item = json_build_object('id', cid, 'name', name, 'state', state, 'timestamp', tmstamp);
    RETURN json_build_object('category', item,
        'inUse', (EXISTS(SELECT person FROM PERSON_SKILL WHERE category = cid) OR (EXISTS(SELECT category FROM DEVICE WHERE category = cid)))
    );
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
    END LOOP;
    SELECT COUNT(id) INTO collection_size FROM CATEGORY;
    RETURN json_build_object('categories', categories, 'categoriesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Creates a new category
 * Returns the category item representation
 * Throws exception in case there is no row added
 */
CREATE OR REPLACE PROCEDURE create_category(category_rep OUT JSON, category_name TEXT)
AS
$$
DECLARE
    category_id INT; category_state TEXT; tmstamp TIMESTAMP;
    prev_id BIGINT; current_id BIGINT;
BEGIN
    INSERT INTO CATEGORY (name) VALUES (category_name)
    RETURNING id, name, state, timestamp INTO category_id, category_name, category_state, tmstamp;
    category_rep = category_item_representation(category_id, category_name, category_state, tmstamp);
EXCEPTION
    WHEN unique_violation THEN
        SELECT last_value INTO current_id FROM category_id_seq;
        IF (prev_id < current_id) THEN
            PERFORM setval('category_id_seq', current_id - 1);
        END IF;
        RAISE 'unique-constraint' USING DETAIL = 'category', HINT = category_name;
END$$ LANGUAGE plpgsql;

/*
 * Updates a category
 * Returns the category item representation
 * Throws exception in case there is no row affected
 */
CREATE OR REPLACE PROCEDURE update_category(category_rep OUT JSON, category_id BIGINT, new_name TEXT)
AS
$$
BEGIN
    PERFORM category_exists(category_id);

    UPDATE CATEGORY SET name = new_name WHERE id = category_id AND name != new_name;

    category_rep = category_item_representation(category_id);
END$$ LANGUAGE plpgsql;

/*
 * Deactivate a category
 * Returns the category item representation
 */
CREATE OR REPLACE PROCEDURE deactivate_category(category_rep OUT JSON, category_id BIGINT)
AS
$$
BEGIN
    PERFORM category_exists(category_id);
    IF (EXISTS (SELECT id FROM DEVICE WHERE category = category_id) OR
        EXISTS (SELECT category FROM PERSON_SKILL WHERE category = category_id)
    ) THEN
        RAISE 'category-being-used';
    END IF;
    UPDATE CATEGORY SET state = 'inactive', timestamp = CURRENT_TIMESTAMP WHERE id = category_id AND state = 'active';
    category_rep = category_item_representation(category_id);
END$$
-- SET default_transaction_isolation = 'serializable'
LANGUAGE plpgsql;

/*
 * Activate a category
 * Returns the category item representation
 */
CREATE OR REPLACE PROCEDURE activate_category(category_rep OUT JSON, category_id BIGINT)
AS
$$
BEGIN
    PERFORM category_exists(category_id);

    UPDATE CATEGORY SET state = 'active', timestamp = CURRENT_TIMESTAMP WHERE id = category_id AND state = 'inactive';

    category_rep = category_item_representation(category_id);
END$$ LANGUAGE plpgsql;
