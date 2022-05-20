/*
 * Ticket functionalities
 */

/*
 * Auxiliary function to return the ticket item representation
 */
CREATE OR REPLACE FUNCTION ticket_item_representation (t_id BIGINT, subject TEXT, description TEXT, employee_state_id INT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', t_id, 'subject', subject, 'description', description, 'userState',
        (SELECT name FROM USER_STATE WHERE id = (SELECT user_state FROM EMPLOYEE_STATE WHERE id = employee_state_id)));
END$$ LANGUAGE plpgsql;

/*
 * Creates a new ticket
 * Returns the ticket item representation
 * Throws exception in case there is no row added or when the hash does not exist
 */
CREATE OR REPLACE PROCEDURE create_ticket (
    subject TEXT,
    description TEXT,
    user_id UUID,
    t_room_id BIGINT,
    t_device_id BIGINT,
    t_hash TEXT,
    ticket_rep OUT JSON
)
AS
$$
DECLARE
    t_id BIGINT; t_creation_timestamp TIMESTAMP; t_employee_state INT;
BEGIN
    IF NOT EXISTS (SELECT device FROM ROOM_DEVICE WHERE device = t_device_id AND room = t_room_id AND qr_hash = t_hash) THEN
        RAISE EXCEPTION 'unknown_error_hash';
    END IF;
    INSERT INTO TICKET (subject, description, reporter, room, device)
        VALUES (subject, description, user_id, t_room_id, t_device_id)
            RETURNING id, creation_timestamp, employee_state INTO t_id, t_creation_timestamp, t_employee_state;

    IF (t_id IS NULL) THEN
        RAISE 'unknown_error_creating_resource';
    END IF;
    ticket_rep = ticket_item_representation(t_id, subject, description, t_employee_state);
END$$ LANGUAGE plpgsql;

/*
 * Rejects a ticket
 * Returns the rejected ticket item representation
 * Throws exception in case the current state does not match with 'to assign' state or when there is no row updated
 */
CREATE OR REPLACE PROCEDURE reject_ticket (t_id BIGINT, ticket_rep OUT JSON)
AS
$$
DECLARE
    t_subject TEXT;
    t_description TEXT;
    t_employee_state INT;
BEGIN
    IF NOT EXISTS (SELECT id FROM EMPLOYEE_STATE
    WHERE id = (SELECT employee_state FROM TICKET WHERE id = t_id) AND name != 'To assign') THEN
        RAISE 'assigned_or_completed_ticket';
    END IF;
    UPDATE TICKET SET employee_state = (SELECT id FROM employee_state WHERE name = 'Refused') WHERE id = t_id
    RETURNING subject, description, employee_state INTO t_subject, t_description, t_employee_state;
    IF (NOT FOUND) THEN
        RAISE 'unknown_error_deleting_resource';
    END IF;
    ticket_rep = ticket_item_representation(
        t_id, t_subject, t_description,
        (SELECT name FROM EMPLOYEE_STATE WHERE id = t_employee_state)
    );
END$$ LANGUAGE plpgsql;

/*
 * Updates a ticket
 * Returns the updated ticket item representation
 * Throws exception when the ticket id does not exist, when the current state does not match with 'to assign' state
 * when all updatable parameters are null, when there is no row updated
 */
CREATE OR REPLACE PROCEDURE update_ticket (
     t_id BIGINT,
     t_new_subject TEXT,
     t_new_desc TEXT,
     ticket_rep OUT JSON
)
AS
$$
DECLARE
    t_current_state INT;
    t_current_subject TEXT;
    t_current_description TEXT;
BEGIN
    SELECT employee_state, subject, description FROM TICKET WHERE id = t_id FOR SHARE
    INTO t_current_state, t_current_subject, t_current_description;
    CASE
        WHEN (t_current_state IS NULL) THEN
            RAISE 'ticket_not_found';
        WHEN (t_current_state = (SELECT id FROM EMPLOYEE_STATE WHERE name != 'To assign')) THEN
            RAISE EXCEPTION 'already_being_fixed_ticket';
        WHEN (t_new_subject = t_current_subject AND t_new_desc = t_current_description) THEN
            -- Does not update when the inputs are equals to current values, returns the representation with the same values.
        ELSE
            UPDATE TICKET SET subject = t_new_subject, description = t_new_desc WHERE id = t_id;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_resource';
            END IF;
    END CASE;

    ticket_rep = ticket_item_representation(t_id, t_new_subject, t_new_desc, t_current_state);
END$$ LANGUAGE plpgsql;

/*
 * Change ticket employee_state
 * Returns the ticket item representation
 * Throws exception when the ticket id does not exist, when the ticket is archived or when no rows affected.
 */
CREATE OR REPLACE PROCEDURE change_ticket_state (
    t_id BIGINT,
    t_state_id INT,
    t_current_subject TEXT,
    t_current_desc TEXT,
    ticket_rep OUT JSON
)
AS
$$
DECLARE
    t_name TEXT;
    t_subject TEXT;
    t_description TEXT;
    t_employee_state INT;
BEGIN
    SELECT name, subject, description, employee_state FROM TICKET WHERE id = t_id
    INTO t_name, t_subject, t_description, t_employee_state;
    CASE
        WHEN (t_name IS NULL) THEN
            RAISE 'ticket_not_found';
        WHEN (t_employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')) THEN
            RAISE EXCEPTION 'cant_update_archived_ticket';
        ELSE
            UPDATE TICKET SET employee_state = t_state_id WHERE id = t_id;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;
            ticket_rep = ticket_item_representation(t_id, t_current_subject, t_current_desc, t_state_id);
    END CASE;
END$$ LANGUAGE plpgsql;

/*
 * Gets a specific ticket
 * Returns the ticket representation
 * Throws exception when the ticket id does not exist
 */
CREATE OR REPLACE PROCEDURE get_ticket (
    t_id BIGINT,
    ticket_rep OUT JSON,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL
)
AS
$$
DECLARE
    rec RECORD;
    t_subject TEXT;
    t_desc TEXT;
    t_creation_time TIMESTAMP;
    t_employee_state TEXT;
    t_user_state TEXT;
    t_possibleTransitions JSON[];
BEGIN

    --Obtain all values to represent ticket
    SELECT subject, description, creation_timestamp, es.name, us.name FROM TICKET t
        INNER JOIN EMPLOYEE_STATE es ON t.employee_state = es.id
        INNER JOIN USER_STATE us ON es.user_state = us.id
        INTO t_subject, t_desc, t_creation_time, t_employee_state, t_user_state;
    IF (t_subject IS NULL) THEN
        RAISE 'ticket_not_found';
    END IF;

    --Obtain all possible employee_state_transitions
    FOR rec IN
        SELECT id, name FROM EMPLOYEE_STATE
        WHERE id IN (SELECT second_employee_state FROM EMPLOYEE_STATE_TRANS
        WHERE first_employee_state = (SELECT employee_state FROM TICKET WHERE id = t_id))
    LOOP
        t_possibleTransitions = array_append(t_possibleTransitions, json_build_object('id', rec.id, 'name', rec.name));
    END LOOP;

    ticket_rep = json_build_object(
        'ticket', json_build_object(
            'id', t_id,
            'subject', t_subject,
            'description', t_desc,
            'creationTimestamp', t_creation_time,
            'employeeState', t_employee_state,
            'userState', t_user_state,
            'possibleTransitions', t_possibleTransitions),
        'comments', list_of_comments(t_id, limit_rows, skip_rows));
END$$ LANGUAGE plpgsql;

/*
 * Gets all the tickets
 * Returns a list with all the tickets item representation
 */
CREATE OR REPLACE FUNCTION get_tickets(
    user_id UUID,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL,
    sort_by TEXT DEFAULT NULL,     --NOT CONCLUDED
    direction TEXT DEFAULT 'DESC', --NOT CONCLUDED
    company_id BIGINT DEFAULT NULL,
    building_id BIGINT DEFAULT NULL,
    room_id BIGINT DEFAULT NULL,
    category_id BIGINT DEFAULT NULL,
    search TEXT DEFAULT NULL,
    reporter_id UUID DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD;
    tickets JSON[];
    collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT t.id, t.subject, t.description, t.employee_state
        FROM TICKET t
            INNER JOIN DEVICE d ON t.device = d.id
            INNER JOIN ROOM r ON t.room = r.id
            INNER JOIN BUILDING b ON b.id = r.building
            INNER JOIN COMPANY c ON c.id = b.company
            INNER JOIN FIXING_BY fb ON t.id = fb.ticket
        WHERE
            CASE
                WHEN (category_id IS NOT NULL) THEN
                    d.category = category_id
                WHEN (room_id IS NOT NULL) THEN
                    r.id = room_id
                WHEN (building_id IS NOT NULL) THEN
                    b.id = building_id
                WHEN (company_id IS NOT NULL) THEN
                    c.id = company_id
                WHEN (reporter_id IS NOT NULL) THEN
                    t.reporter = reporter_id
                WHEN (search IS NOT NULL) THEN
                    t.subject LIKE search
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = user_id AND role = (SELECT id FROM ROLE WHERE name = 'manager')) = user_id) THEN
                    b.manager = user_id
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = user_id AND role = (SELECT id FROM ROLE WHERE name = 'user')) = user_id) THEN
                    t.reporter = user_id
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = user_id AND role = (SELECT id FROM ROLE WHERE name = 'employee')) = user_id) THEN
                    fb.person = user_id
            END
        ORDER BY subject
        LIMIT limit_rows OFFSET skip_rows

    LOOP
        tickets = array_append(
            tickets,
            ticket_item_representation(rec.id, rec.subject, rec.description, rec.employee_state)
        );
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('tickets', tickets, 'companiesCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Set a employee to a ticket
 * Returns the ticket and the employee representation
 */
CREATE OR REPLACE PROCEDURE set_ticket_employee(employee_id UUID, ticket_id BIGINT, ticket_rep OUT JSON)
AS
$$
DECLARE
    t_employee_state INT;
    t_subject TEXT;
    t_description TEXT;
    t_employeeState INT;
    employee_name TEXT;
    employee_email TEXT;
    employee_phone TEXT;
BEGIN
    IF ((SELECT category FROM DEVICE WHERE id = (SELECT device FROM TICKET WHERE id = ticket_id))
            IN (SELECT category FROM PERSON_SKILL WHERE person = employee_id)) THEN
        RAISE EXCEPTION 'employee_doenst_have_necessary_skill';
    END IF;
    SELECT employee_state FROM TICKET WHERE id = ticket_id INTO t_employee_state;
    IF (t_employee_state != (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')) THEN
        RAISE EXCEPTION 'must_have_to_assign_state';
    ELSE
        UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Not started')
        WHERE id = ticket_id RETURNING subject, description, employee_state INTO t_subject, t_description, t_employeeState;
        IF (NOT FOUND) THEN
            RAISE 'unknown_error_updating_state_resource';
        END IF;
        INSERT INTO FIXING_BY (ticket, person) VALUES (ticket_id, employee_id);
        IF (NOT FOUND) THEN
            RAISE 'unknown_error_creating_resource';
        END IF;
    END IF;
    SELECT name, email, phone FROM PERSON WHERE id = employee_id INTO employee_name, employee_email, employee_phone;
    ticket_rep = json_build_object(
        'ticket', ticket_item_representation(ticket_id, t_subject, t_description, t_employeeState),
        'person', person_item_representation(employee_id, employee_name, employee_phone, employee_email));
END$$ LANGUAGE plpgsql;

/**
  * Remove ticket employee
  */
CREATE OR REPLACE PROCEDURE remove_ticket_employee(employee_id UUID, ticket_id BIGINT)
AS
$$
DECLARE
    t_employee_state INT;
BEGIN
    SELECT employee_state FROM TICKET WHERE id = ticket_id INTO t_employee_state;
    CASE
        WHEN (t_employee_state == (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')) THEN
            RAISE EXCEPTION 'must_have_employee';
        WHEN (t_employee_state == (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Refused' OR name = 'Archived' OR name = 'Concluded')) THEN
            RAISE EXCEPTION 'must_be_a_running_ticket';
        ELSE
            UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')
            WHERE id = ticket_id;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;
            UPDATE FIXING_BY SET end_timestamp = CURRENT_TIMESTAMP
            WHERE person = employee_id AND ticket = ticket_id AND end_timestamp == NULL;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;
    END CASE;
END$$ LANGUAGE plpgsql;

/**
  * Add ticket rate
  */
CREATE OR REPLACE PROCEDURE add_ticket_rate(person_id UUID, ticket_id BIGINT, rate_value INT)
AS
$$
BEGIN
    IF (SELECT employee_state FROM TICKET
    WHERE id = ticket_id AND employee_state != (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Concluded')) THEN
        RAISE 'ticket_must_be_concluded';
    END IF;
    IF (person_id = (SELECT reporter FROM TICKET WHERE id = ticket_id)
        OR EXISTS(
            SELECT person FROM PERSON_ROLE
            WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'manager')
              AND person_id = (SELECT manager FROM BUILDING
              WHERE id = (SELECT building FROM ROOM WHERE id = (SELECT room FROM TICKET WHERE id = ticket_id))))
        OR EXISTS(
            SELECT person FROM PERSON_ROLE
            WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'admin'))) THEN
        INSERT INTO RATE (person, ticket, rate) VALUES (person_id, ticket_id, rate_value);
        IF (NOT FOUND) THEN
            RAISE 'unknown_error_creating_resource';
        END IF;
    END IF;
END$$ LANGUAGE plpgsql;