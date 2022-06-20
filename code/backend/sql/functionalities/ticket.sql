/*
 * Ticket functionalities
 */

/*
 * Auxiliary function to return the ticket item representation
 */
CREATE OR REPLACE FUNCTION ticket_item_representation(ticket_id BIGINT, subject TEXT, description TEXT, employee_state_id INT)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', ticket_id, 'subject', subject, 'description', description,
        'userState',
        (SELECT name FROM USER_STATE WHERE id = (SELECT user_state FROM EMPLOYEE_STATE WHERE id = employee_state_id)),
        'employeeState',
        (SELECT name FROM EMPLOYEE_STATE WHERE id = employee_state_id));
END$$ LANGUAGE plpgsql;

/*
 * Creates a new ticket
 * Returns the ticket item representation
 * Throws exception in case there is no row added or when the hash does not exist
 */
DROP PROCEDURE create_ticket(ticket_rep JSON, hash TEXT, subject TEXT,
description TEXT, person_name TEXT, person_email TEXT, person_phone TEXT);
CREATE OR REPLACE PROCEDURE create_ticket(
    ticket_rep OUT JSON,
    hash TEXT,
    subject TEXT,
    description TEXT,
    person_name TEXT,
    person_email TEXT,
    person_phone TEXT DEFAULT NULL
)
AS
$$
DECLARE
    t_id BIGINT; t_creation_timestamp TIMESTAMP; t_employee_state INT; room_id BIGINT; device_id BIGINT;
    person_id UUID; person_rep JSON; role_id SMALLINT;
BEGIN
    SELECT room, device INTO room_id, device_id FROM ROOM_DEVICE WHERE qr_hash = hash;
    IF (room_id IS NULL OR device_id IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'hash';
    END IF;

    SELECT person_exists(person_email) INTO person_id;
    IF (person_id IS NULL) THEN
        SELECT id INTO role_id FROM ROLE WHERE name = 'guest';
        CALL create_person(
            person_rep,
            role_id,
            person_name,
            person_email,
            'password',
            person_phone);
        person_id = person_rep->>'id';
    END IF;

    INSERT INTO TICKET (subject, description, reporter, room, device)
        VALUES (subject, description, person_id, room_id, device_id)
        RETURNING id, creation_timestamp, employee_state INTO t_id, t_creation_timestamp, t_employee_state;
    IF (t_id IS NULL) THEN
        RAISE 'unknown-error-writing-resource' USING DETAIL = 'creating';
    END IF;

    ticket_rep = ticket_item_representation(t_id, subject, description, t_employee_state);
END$$LANGUAGE plpgsql;

/*
 * Updates a ticket
 * Returns the updated ticket item representation
 * Throws exception when the ticket id does not exist, when the current state does not match with 'to assign' state
 * when all updatable parameters are null, when there is no row updated
 */
CREATE OR REPLACE PROCEDURE update_ticket(
     ticket_id BIGINT,
     ticket_rep OUT JSON,
     t_new_subject TEXT DEFAULT NULL,
     t_new_desc TEXT DEFAULT NULL
)
AS
$$
DECLARE
    t_current_state INT; t_current_subject TEXT; t_current_description TEXT;
BEGIN
    SELECT employee_state, subject, description FROM TICKET
    WHERE id = ticket_id
    INTO t_current_state, t_current_subject, t_current_description;

    CASE
        --if does not exist any the ticket with id equal to ticket_id
        WHEN (t_current_state IS NULL) THEN
            RAISE 'ticket_not_found';
        --just can update a ticket with employee_state equal to "To assign"
        WHEN (t_current_state != (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')) THEN
            RAISE EXCEPTION 'ticket_being_fixed_or_concluded';
        WHEN (t_new_subject = t_current_subject AND t_new_desc = t_current_description) THEN
            -- Does not update when the inputs are equals to current values, returns the representation with the same values.
        ELSE
            IF (t_new_subject IS NULL AND t_new_desc IS NOT NULL) THEN
                UPDATE TICKET SET description = t_new_desc WHERE id = ticket_id
                RETURNING id, subject, description INTO ticket_id, t_current_subject, t_current_description;
            ELSEIF (t_new_subject IS NOT NULL AND t_new_desc IS NULL) THEN
                UPDATE TICKET SET subject = t_new_subject WHERE id = ticket_id
                RETURNING id, subject, description INTO ticket_id, t_current_subject, t_current_description;
            ELSEIF (t_new_subject IS NOT NULL AND t_new_desc IS NOT NULL) THEN
                UPDATE TICKET SET subject = t_new_subject, description = t_new_desc WHERE id = ticket_id
                RETURNING id, subject, description INTO ticket_id, t_current_subject, t_current_description;
            END IF;
            IF (ticket_id IS NULL) THEN
                RAISE 'unknown_error_updating_resource';
            END IF;
    END CASE;

    ticket_rep = ticket_item_representation(ticket_id, t_current_subject, t_current_description, t_current_state);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Rejects a ticket
 * Returns the rejected ticket item representation
 * Throws exception when the ticket id does not exist, when the current state does not match with 'to assign' state
 * when all updatable parameters are null, when there is no row updated
 */
CREATE OR REPLACE PROCEDURE delete_ticket(ticket_id BIGINT, ticket_rep OUT JSON)
AS
$$
DECLARE
    employee_state_id INT = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Refused');
BEGIN
    CALL change_ticket_state(ticket_id, employee_state_id, ticket_rep);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Change ticket employee_state
 * Returns the ticket item representation
 * Throws exception when the ticket id does not exist, when the ticket is archived or when no rows affected.
 */
CREATE OR REPLACE PROCEDURE change_ticket_state(ticket_id BIGINT, t_new_employee_state INT, ticket_rep OUT JSON)
AS
$$
DECLARE
    t_subject TEXT; t_description TEXT; t_curr_employee_state INT; updated_ticket BIGINT; currentTimestamp TIMESTAMP;
BEGIN
    SELECT subject, description, employee_state FROM TICKET
    WHERE id = ticket_id
    INTO t_subject, t_description, t_curr_employee_state;

    CASE
        --if does not exist any the ticket with id equal to ticket_id
        WHEN (t_subject IS NULL) THEN
            RAISE 'ticket_not_found';
        --cant change the state if the current state is "Archived"
        WHEN (t_curr_employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')) THEN
            RAISE EXCEPTION 'cant_update_archived_ticket';
        --if the current employee_state does not have a transition to the new_state
        WHEN NOT EXISTS (SELECT first_employee_state FROM EMPLOYEE_STATE_TRANS
        WHERE first_employee_state = t_curr_employee_state AND second_employee_state = t_new_employee_state) THEN
            RAISE EXCEPTION 'impossible_state_transition';
        --if the new state is the end of the ticket, will set the close_timestamp
        ELSE IF (t_new_employee_state IN (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Completed' OR name = 'Refused')) THEN
                currentTimestamp = CURRENT_TIMESTAMP;
                UPDATE TICKET SET employee_state = t_new_employee_state, close_timestamp = currentTimestamp
                WHERE id = ticket_id RETURNING id INTO updated_ticket;

                IF (t_new_employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Completed')) THEN
                    UPDATE FIXING_BY SET end_timestamp = currentTimestamp
                    WHERE ticket = ticket_id AND end_timestamp IS NULL;
                END IF;
            ELSE
                UPDATE TICKET SET employee_state = t_new_employee_state
                WHERE id = ticket_id RETURNING id INTO updated_ticket;
            END IF;
             IF (updated_ticket IS NULL) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;
    END CASE;

    ticket_rep = ticket_item_representation(ticket_id, t_subject, t_description, t_new_employee_state);
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/*
 * Gets a specific ticket
 * Returns the ticket representation
 * Throws exception when the ticket id does not exist
 */
CREATE OR REPLACE FUNCTION get_ticket (
    ticket_id BIGINT,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL,
    comments_direction TEXT DEFAULT 'DESC'
) RETURNS JSON
AS
$$
DECLARE
    rec RECORD; t_subject TEXT; t_desc TEXT; t_creation_time TIMESTAMP; t_employee_state TEXT;
    t_user_state TEXT; t_possibleTransitions JSON[]; p_id UUID; p_name TEXT; p_phone TEXT; p_email TEXT;
    c_id BIGINT; c_name TEXT; c_state TEXT; c_timestamp TIMESTAMP ;b_id BIGINT; b_name TEXT; b_state TEXT;
    b_floors INT; b_timestamp TIMESTAMP; r_id BIGINT; r_name TEXT; r_state TEXT; r_floor INT; r_timestamp TIMESTAMP;
    d_id BIGINT; d_name TEXT; d_state TEXT; d_timestamp TIMESTAMP; ct_name TEXT;
BEGIN
    --Obtain all values to represent ticket
    SELECT
           subject, description, creation_timestamp, es.name, us.name, p.id, p.name, p.phone, p.email,
           c.id, c.name, c.state, c.timestamp, b.id, b.name, b.floors, b.state, b.timestamp, r.id, r.name,
           r.floor, r.state, r.timestamp, d.id, d.name, d.state, d.timestamp, ct.name
    FROM TICKET t
        INNER JOIN EMPLOYEE_STATE es ON t.employee_state = es.id
        INNER JOIN USER_STATE us ON es.user_state = us.id
        INNER JOIN PERSON p ON p.id = t.reporter
        INNER JOIN device d on t.device = d.id
        INNER JOIN room r on t.room = r.id
        INNER JOIN building b on b.id = r.building
        INNER JOIN company c on c.id = b.company
        INNER JOIN category ct on ct.id = d.category
    WHERE t.id = ticket_id
        INTO t_subject, t_desc, t_creation_time, t_employee_state, t_user_state, p_id, p_name, p_phone, p_email,
        c_id, c_name, c_state, c_timestamp, b_id, b_name, b_floors, b_state, b_timestamp, r_id, r_name, r_floor,
        r_state, r_timestamp, d_id, d_name, d_state, d_timestamp, ct_name;
    IF (t_subject IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'ticket';
    END IF;

    --Obtain all possible employee_state_transitions
    FOR rec IN
        SELECT id, name FROM EMPLOYEE_STATE
        WHERE id IN (SELECT second_employee_state FROM EMPLOYEE_STATE_TRANS
        WHERE first_employee_state = (SELECT employee_state FROM TICKET WHERE id = ticket_id))
    LOOP
        t_possibleTransitions = array_append(t_possibleTransitions, json_build_object('id', rec.id, 'name', rec.name));
    END LOOP;

    return json_build_object(
        'ticket', json_build_object('id', ticket_id, 'subject', t_subject, 'description', t_desc,
            'creationTimestamp', t_creation_time, 'employeeState', t_employee_state, 'userState', t_user_state,
            'possibleTransitions', t_possibleTransitions),
        'ticketComments', get_comments(ticket_id, limit_rows, skip_rows, comments_direction),
        'person', person_item_representation(p_id, p_name, p_phone, p_email),
        'company', company_item_representation(c_id, c_name, c_state, c_timestamp),
        'building', building_item_representation(b_id, b_name, b_floors, b_state, b_timestamp),
        'room', room_item_representation(r_id, r_name, r_floor, r_state, r_timestamp),
        'device', device_item_representation(d_id, d_name, ct_name, d_state, d_timestamp));
END$$ LANGUAGE plpgsql;


/*
 * Gets all the tickets
 * Returns a list with all the tickets item representation
 */
CREATE OR REPLACE FUNCTION get_tickets(
    person_id UUID,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL,
    sort_by TEXT DEFAULT 'date',
    direction TEXT DEFAULT 'DESC',
    company_name TEXT DEFAULT NULL,
    building_name TEXT DEFAULT NULL,
    room_name TEXT DEFAULT NULL,
    category_name TEXT DEFAULT NULL,
    search TEXT DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD; tickets JSON[]; collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT t.id, t.subject, t.description, t.employee_state
        FROM TICKET t
            INNER JOIN DEVICE d ON t.device = d.id
            INNER JOIN CATEGORY ct ON d.category = ct.id
            INNER JOIN ROOM r ON t.room = r.id
            INNER JOIN BUILDING b ON b.id = r.building
            INNER JOIN COMPANY c ON c.id = b.company
            FULL JOIN FIXING_BY fb ON t.id = fb.ticket
       WHERE
             ct.name LIKE CONCAT('%',category_name,'%') AND
             LOWER(t.subject) LIKE LOWER(CONCAT('%',search,'%')) AND
             LOWER(r.name) LIKE LOWER(CONCAT('%',room_name,'%')) AND
             LOWER(b.name) LIKE LOWER(CONCAT('%',building_name,'%')) AND
             LOWER(c.name) LIKE LOWER(CONCAT('%',company_name,'%')) AND
            CASE
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'manager')) = person_id) THEN
                    b.manager = person_id
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'user')) = person_id) THEN
                    t.reporter = person_id
                WHEN ((SELECT pr.person FROM PERSON_ROLE pr
                WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'employee')) = person_id) THEN
                    fb.person = person_id
                ELSE t.id > 0
            END
        ORDER BY
            CASE WHEN direction='DESC' AND sort_by='date' THEN creation_timestamp END DESC,
            CASE WHEN direction='ASC' AND sort_by='date'  THEN creation_timestamp END ASC,
            CASE WHEN direction='DESC' AND sort_by='name' THEN t.subject END DESC,
            CASE WHEN direction='ASC' AND sort_by='name'  THEN t.subject END ASC
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        tickets = array_append(
            tickets, ticket_item_representation(rec.id, rec.subject, rec.description, rec.employee_state)
        );
        collection_size = collection_size + 1;
    END LOOP;

    RETURN json_build_object('tickets', tickets, 'ticketsCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Set a employee to a ticket
 * Returns the ticket and the employee representation
 * Throws exception when the employee does not has the necessary skill or if the ticket already have a employee
 */
--DROP PROCEDURE set_ticket_employee(new_employee_id uuid, ticket_id bigint, ticket_rep json);
CREATE OR REPLACE PROCEDURE set_ticket_employee(ticket_rep OUT JSON, new_employee_id UUID, ticket_id BIGINT)
AS
$$
DECLARE
    t_subject TEXT; t_description TEXT; t_employeeState INT; employee_name TEXT;
    employee_email TEXT; employee_phone TEXT;
BEGIN
    SELECT subject, description INTO t_subject, t_description FROM TICKET WHERE id = ticket_id;
    IF (NOT FOUND) THEN
        RAISE 'resource-not-found';
    END IF;

    SELECT name, email, phone FROM PERSON WHERE id = new_employee_id
    INTO employee_name, employee_email, employee_phone;

    --verify if the employee has the necessary skill to resolve the ticket problem
    IF NOT EXISTS (SELECT category FROM PERSON_SKILL WHERE person = new_employee_id
      AND category = (SELECT category FROM DEVICE WHERE id = (SELECT device FROM TICKET WHERE id = ticket_id))) THEN
        RAISE EXCEPTION 'missing_necessary_skill';
    END IF;

    --verify if the current ticket employee_state is to assign or Waiting for new employee
    IF NOT EXISTS (SELECT employee_state FROM TICKET WHERE id = ticket_id AND employee_state IN
        (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')) THEN
        RAISE EXCEPTION 'already_has_an_employee';
    ELSE
        UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Not started')
        WHERE id = ticket_id RETURNING employee_state INTO t_employeeState;
        IF (NOT FOUND) THEN
            RAISE 'unknown_error_updating_state_resource';
        END IF;
        INSERT INTO FIXING_BY (ticket, person) VALUES (ticket_id, new_employee_id);
        IF (NOT FOUND) THEN
            RAISE 'unknown_error_creating_resource';
        END IF;
    END IF;

    SELECT name, email, phone FROM PERSON WHERE id = new_employee_id INTO employee_name, employee_email, employee_phone;
    ticket_rep = json_build_object(
        'ticket', ticket_item_representation(ticket_id, t_subject, t_description, t_employeeState),
        'person', person_item_representation(new_employee_id, employee_name, employee_phone, employee_email));
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/**
  * Remove ticket employee
  * Returns the removed ticket and the employee representation
  * Throws exception when employee does not has the necessary skill or if the ticket already have a employee
  */
CREATE OR REPLACE PROCEDURE remove_ticket_employee(ticket_id BIGINT, ticket_rep OUT JSON)
AS
$$
DECLARE
    t_employee_state INT; t_subject TEXT; t_description TEXT; employee_name TEXT; employee_email TEXT;
    employee_phone TEXT; employee_id UUID;
BEGIN

    SELECT employee_state FROM TICKET WHERE id = ticket_id INTO t_employee_state;
    CASE
        WHEN (NOT FOUND) THEN
            RAISE 'ticket_not_found';
        --cant remove a employee from a ticket without employee
        WHEN (t_employee_state IN (SELECT id FROM EMPLOYEE_STATE
        WHERE name = 'To assign')) THEN
            RAISE EXCEPTION 'must_have_employee';
        WHEN (t_employee_state IN (SELECT id FROM EMPLOYEE_STATE
        WHERE name = 'Refused' OR name = 'Archived' OR name = 'Concluded')) THEN
            RAISE EXCEPTION 'must_be_a_running_ticket';
        ELSE
            UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')
            WHERE id = ticket_id
            RETURNING subject, description, employee_state INTO t_subject, t_description, t_employee_state;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;

            employee_id = (SELECT person FROM FIXING_BY WHERE ticket = ticket_id AND end_timestamp IS NULL);
            SELECT name, email, phone FROM PERSON
            WHERE id = employee_id INTO employee_name, employee_email, employee_phone;

            UPDATE FIXING_BY SET end_timestamp = CURRENT_TIMESTAMP
            WHERE person = employee_id AND ticket = ticket_id AND end_timestamp IS NULL;
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_updating_state_resource';
            END IF;
    END CASE;

    ticket_rep = json_build_object(
        'ticket', ticket_item_representation(ticket_id, t_subject, t_description, t_employee_state),
        'person', person_item_representation(employee_id, employee_name, employee_phone, employee_email));
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;

/**
  * Add ticket rate
  * Throws exception when the ticket isn't completed or when no rows affected
  */
CREATE OR REPLACE PROCEDURE add_ticket_rate(person_id UUID, ticket_id BIGINT, rate_value INT, ticket_rep OUT JSON)
AS
$$
DECLARE
    t_subject TEXT; t_description TEXT; t_employeeSate TEXT; t_userState TEXT;
BEGIN
    --verify if the person is the ticket reporter, or the a manager of the building, or the admin
    /*IF (person_id = (SELECT reporter FROM TICKET WHERE id = ticket_id)
        OR (
            person_id = (SELECT manager FROM BUILDING
            WHERE id = (SELECT building FROM ROOM WHERE id = (SELECT room FROM TICKET WHERE id = ticket_id))))
        OR EXISTS (
            SELECT person FROM PERSON_ROLE
            WHERE person = person_id AND role = (SELECT id FROM ROLE WHERE name = 'admin'))
        ) THEN*/
    SELECT employee_state INTO t_employeeSate FROM TICKET WHERE id = ticket_id;
    CASE
        WHEN (NOT FOUND) THEN
            RAISE 'ticket_not_found';
        WHEN (t_employeeSate = 'Concluded') THEN
            RAISE 'ticket_must_be_concluded';
        ELSE
            INSERT INTO RATE (person, ticket, rate) VALUES (person_id, ticket_id, rate_value);
            IF (NOT FOUND) THEN
                RAISE 'unknown_error_creating_resource';
            END IF;
            SELECT subject, description, es.name, us.name FROM TICKET t
                INNER JOIN EMPLOYEE_STATE es ON t.employee_state = es.id
                INNER JOIN USER_STATE us ON es.user_state = us.id
                WHERE t.id = ticket_id INTO t_subject, t_description, t_employeeSate, t_userState;

            ticket_rep = json_build_object('id', ticket_id, 'subject', t_subject, 'description', t_description,
                'employeeState', t_employeeSate, 'userState', t_userState, 'rate', rate_value);
        END CASE;
        /*ELSE
            RAISE EXCEPTION 'invalid_access_exception';
        END IF;*/
END$$
SET default_transaction_isolation = 'repeatable read'
LANGUAGE plpgsql;