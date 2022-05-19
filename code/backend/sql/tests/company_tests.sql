/*
 * Script to test all the implemented company functionalities
 */

/*
 * Tests the creation of a new company
 */
DO
$$
DECLARE
    id BIGINT;
    name TEXT = 'Benfica Campus';
    state TEXT = 'Active';
    company_rep JSON;
BEGIN
    RAISE INFO '---| Company creation test |---';

    CALL create_company(name, company_rep);
    id = company_rep->>'id';

    IF (assert_json_is_not_null(company_rep, 'id') AND
        assert_json_value(company_rep, 'name', name) AND
        assert_json_value(company_rep, 'state', state) AND
        assert_json_is_not_null(company_rep, 'timestamp')) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;

    -- Remove sequence inc
    IF (id = 1) THEN
        ALTER SEQUENCE company_id_seq RESTART;
        RETURN;
    END IF;
    PERFORM setval('company_id_seq', (SELECT last_value FROM company_id_seq) - 1);
END$$;

/*
 * Tests the creation of a new company with a non unique name, throws unique_company_name
 */
DO
$$
DECLARE
    name TEXT = 'ISEL';
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Throws unique_company_name constraint test |---';

    CALL create_company(name, company_rep);

    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_company_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the valid update of a company name
 */
DO
$$
DECLARE
    id BIGINT = 1;
    name TEXT = 'ISELv2.0';
    state TEXT = 'Active';
    company_rep JSON;
BEGIN
    RAISE INFO '---| Update company name test |---';

    CALL update_company(id, company_rep, name);

    IF (assert_json_is_not_null(company_rep, 'id') AND
        assert_json_value(company_rep, 'name', name) AND
        assert_json_value(company_rep, 'state', state) AND
        assert_json_is_not_null(company_rep, 'timestamp')) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the update of a company with a non unique name
 */
DO
$$
DECLARE
    id BIGINT = 1;
    name TEXT = 'ISCAL';
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Update company with a non unique name test |---';

    CALL update_company(id, company_rep, name);

    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'unique_company_name') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the get companies function
 */
DO
$$
DECLARE
    companies_col_size INT = 4;
    company_rep JSON;
BEGIN
    RAISE INFO '---| Get companies test |---';

    SELECT get_companies(10, 0) INTO company_rep;

    IF (assert_json_is_not_null(company_rep, 'companies') AND
        assert_json_value(company_rep, 'companiesCollectionSize', companies_col_size::TEXT)) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the get company function
 */
DO
$$
DECLARE
    id BIGINT = 1;
    name TEXT = 'ISEL';
    state TEXT = 'Active';
    company_rep JSON;
    buildings_col_size INT = 0;
BEGIN
    RAISE INFO '---| Get company test |---';

    SELECT get_company(id, 10, 0) INTO company_rep;

    IF (assert_json_value(company_rep, 'id', id::TEXT) AND
        assert_json_value(company_rep, 'name', name) AND
        assert_json_value(company_rep, 'state', state) AND
        assert_json_is_not_null(company_rep, 'timestamp') AND
        /*assert_json_is_not_null(company_rep, 'buildings') AND*/
        assert_json_value(company_rep, 'buildingsCollectionSize', buildings_col_size::TEXT)
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests in the get company function the exception thrown when the company id does not exist
 */
DO
$$
DECLARE
    id BIGINT = 99;
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Get company with non existent id test |---';

    SELECT get_company(id, 10, 0) INTO company_rep;
    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'company_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the company deactivation
 */
DO
$$
DECLARE
    id BIGINT = 1;
    name TEXT = 'ISEL';
    state TEXT = 'Inactive';
    company_rep JSON;
BEGIN
    RAISE INFO '---| Company deactivation test |---';

    CALL deactivate_company(id, company_rep);

    IF (assert_json_value(company_rep, 'id', id::TEXT) AND
        assert_json_value(company_rep, 'name', name) AND
        assert_json_value(company_rep, 'state', state) AND
        assert_json_is_not_null(company_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the exception thrown when the company id does not exist in the deactivation operation
 */
DO
$$
DECLARE
    id BIGINT = 99;
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Deactivate company with non existent id test |---';

    CALL deactivate_company(id, company_rep);
    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'company_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;

/*
 * Tests the company activation
 */
DO
$$
DECLARE
    id BIGINT = 4;
    name TEXT = 'ESD';
    state TEXT = 'Active';
    company_rep JSON;
BEGIN
    RAISE INFO '---| Company activation test |---';

    CALL activate_company(id, company_rep);

    IF (assert_json_value(company_rep, 'id', id::TEXT) AND
        assert_json_value(company_rep, 'name', name) AND
        assert_json_value(company_rep, 'state', state) AND
        assert_json_is_not_null(company_rep, 'timestamp')
    ) THEN
        RAISE INFO '-> Test succeeded!';
    ELSE
        RAISE INFO '-> Test failed!';
    END IF;
    ROLLBACK;
END$$;

/*
 * Tests the exception thrown when the company id does not exist in the activation operation
 */
DO
$$
DECLARE
    id BIGINT = 99;
    company_rep JSON;
    ex_constraint TEXT;
BEGIN
    RAISE INFO '---| Activate company with non existent id test |---';

    CALL activate_company(id, company_rep);
    RAISE INFO '-> Test failed!';
EXCEPTION
    WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS ex_constraint = MESSAGE_TEXT;
        IF (ex_constraint = 'company_not_found') THEN
            RAISE INFO '-> Test succeeded!';
        ELSE
            RAISE INFO '-> Test failed!';
        END IF;
END$$;