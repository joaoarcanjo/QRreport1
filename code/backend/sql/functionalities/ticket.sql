/*
 * Ticket functionalities
 */

/**
  * Auxiliary function to obtain the ticket state name
  */
CREATE OR REPLACE FUNCTION get_ticket_state_name(ticket_id BIGINT)
RETURNS TEXT
AS
$$
BEGIN
    RETURN (SELECT name FROM EMPLOYEE_STATE WHERE id = (SELECT employee_state FROM TICKET WHERE id = ticket_id));
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a ticket has a parent ticket
  */
CREATE OR REPLACE FUNCTION is_child_ticket(ticket_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF (EXISTS (SELECT id FROM TICKET WHERE id = ticket_id AND parent_ticket IS NOT NULL)) THEN
        RETURN TRUE;
    END IF;
    RETURN FALSE;
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a ticket is archived
  */
CREATE OR REPLACE FUNCTION is_ticket_archived(ticket_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF EXISTS (
        SELECT id FROM TICKET WHERE id = ticket_id
            AND (employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived')
            OR employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Refused'))
    ) THEN
        RAISE 'archived-ticket';
    END IF;
    RETURN TRUE;
END$$LANGUAGE plpgsql;

/**
  * Auxiliary function to verify if a ticket is assigned
  */
CREATE OR REPLACE FUNCTION is_ticket_assigned(ticket_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF EXISTS (
        SELECT id FROM TICKET WHERE id = ticket_id AND
            employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')
        ) THEN
        RETURN FALSE;
    END IF;
    RETURN TRUE;
END$$LANGUAGE plpgsql;

/*
 * Auxiliary function to verify if a ticket exists
 */
CREATE OR REPLACE FUNCTION ticket_exists(ticket_id BIGINT)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT id FROM TICKET WHERE id = ticket_id)) THEN
        RAISE 'resource-not-found' USING DETAIL = 'ticket', HINT = ticket_id;
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to verify if a ticket belongs to the user
 */
CREATE OR REPLACE FUNCTION ticket_belongs_to_user(ticket_id BIGINT, person_id UUID)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT id FROM TICKET WHERE id = ticket_id AND reporter = person_id)) THEN
        RAISE 'resource-permission-denied';
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to verify if a ticket is responsibility of a certain employee
 */
CREATE OR REPLACE FUNCTION ticket_belongs_to_employee(ticket_id BIGINT, person_id UUID)
RETURNS BOOL
AS
$$
BEGIN
    IF (NOT EXISTS (SELECT person FROM FIXING_BY WHERE ticket = ticket_id AND person = person_id)) THEN
        RAISE 'resource-permission-denied';
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to verify if a ticket belongs to the person company
 */
CREATE OR REPLACE FUNCTION ticket_belongs_to_person_company(ticket_id BIGINT, person_id UUID)
RETURNS BOOL
AS
$$
DECLARE company_id BIGINT;
BEGIN
    SELECT c.id INTO company_id FROM TICKET t
        INNER JOIN ROOM r ON (t.room = r.id)
        INNER JOIN BUILDING b ON (r.building = b.id)
        INNER JOIN COMPANY c ON (b.company = c.id)
    WHERE t.id = ticket_id;
    IF (NOT EXISTS (SELECT person FROM PERSON_COMPANY WHERE person = person_id AND company = company_id)) THEN
        RAISE 'resource-permission-denied';
    END IF;
    RETURN TRUE;
END$$ LANGUAGE plpgsql;

/*
 * Auxiliary function to return the ticket item representation by its id
 */
CREATE OR REPLACE FUNCTION ticket_item_representation(ticket_id BIGINT)
RETURNS JSON
AS
$$
DECLARE tsubject TEXT; tdescription TEXT; tcompany TEXT; tbuilding TEXT; troom TEXT; empstate_id INT;
BEGIN
    SELECT subject, description, c.name, b.name, r.name, employee_state
    INTO tsubject, tdescription, tcompany, tbuilding, troom, empstate_id
    FROM TICKET t
        INNER JOIN ROOM r ON (t.room = r.id)
        INNER JOIN BUILDING b ON (r.building = b.id)
        INNER JOIN COMPANY c ON (b.company = c.id)
    WHERE t.id = ticket_id;
    RETURN json_build_object('id', ticket_id, 'subject', tsubject, 'description', tdescription, 'company', tcompany,
        'building', tbuilding, 'room', troom,
        'userState',
        (SELECT name FROM USER_STATE WHERE id = (SELECT user_state FROM EMPLOYEE_STATE WHERE id = empstate_id)),
        'employeeState',
        (SELECT name FROM EMPLOYEE_STATE WHERE id = empstate_id));
END$$ LANGUAGE plpgsql;

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
 * Auxiliary function to return the ticket item representation, with company, building and room names
 */
CREATE OR REPLACE FUNCTION ticket_item_representation(
    ticket_id BIGINT,
    subject TEXT,
    description TEXT,
    employee_state_id INT,
    company TEXT,
    building TEXT,
    room TEXT
)
RETURNS JSON
AS
$$
BEGIN
    RETURN json_build_object('id', ticket_id, 'subject', subject, 'description', description, 'company', company,
        'building', building, 'room', room,
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
CREATE OR REPLACE PROCEDURE create_ticket(
    ticket_rep OUT JSON,
    hash TEXT,
    tsubject TEXT,
    tdescription TEXT,
    person_name TEXT,
    person_email TEXT,
    person_phone TEXT DEFAULT NULL
)
AS
$$
DECLARE
    t_id BIGINT; room_id BIGINT; device_id BIGINT;
    person_id UUID; person_rep JSON;
BEGIN
    SELECT room, device INTO room_id, device_id FROM ROOM_DEVICE WHERE qr_hash = hash;
    IF (room_id IS NULL OR device_id IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'hash', HINT = hash;
    END IF;

    SELECT person_exists(person_email) INTO person_id;
    IF (person_id IS NULL) THEN
        CALL create_person(
            person_rep,
            (SELECT id FROM ROLE WHERE name = 'guest'),
            person_name,
            person_email,
            gen_random_uuid(),
            person_phone
        );
        person_id = person_rep->>'id';
    END IF;

    INSERT INTO TICKET (subject, description, reporter, room, device)
    VALUES (tsubject, tdescription, person_id, room_id, device_id)
    RETURNING id INTO t_id;

    ticket_rep = ticket_item_representation(t_id);
END$$LANGUAGE plpgsql;

/*
 * Updates a ticket
 * Returns the updated ticket item representation
 * Throws exception when the ticket id does not exist, when the current state does not match with 'to assign' state
 * when all updatable parameters are null, when there is no row updated
 */
CREATE OR REPLACE PROCEDURE update_ticket(
     ticket_rep OUT JSON,
     ticket_id BIGINT,
     person_id UUID,
     t_new_subject TEXT DEFAULT NULL,
     t_new_desc TEXT DEFAULT NULL
)
AS
$$
DECLARE role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    IF (is_ticket_assigned(ticket_id)) THEN
        RAISE 'fixing-ticket';
    END IF;
    IF (role = 'user') THEN
        PERFORM ticket_belongs_to_user(ticket_id, person_id);
    ELSEIF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;

    IF (t_new_subject IS NULL AND t_new_desc IS NOT NULL) THEN
        UPDATE TICKET SET description = t_new_desc WHERE id = ticket_id AND description != t_new_desc;
    ELSEIF (t_new_subject IS NOT NULL AND t_new_desc IS NULL) THEN
        UPDATE TICKET SET subject = t_new_subject WHERE id = ticket_id AND subject != t_new_subject;
    ELSEIF (t_new_subject IS NOT NULL AND t_new_desc IS NOT NULL) THEN
        UPDATE TICKET SET subject = t_new_subject, description = t_new_desc
        WHERE id = ticket_id AND subject != t_new_subject AND description != t_new_desc;
    END IF;

    ticket_rep = ticket_item_representation(ticket_id);
END$$
LANGUAGE plpgsql;

/*
 * Rejects a ticket
 * Returns the rejected ticket item representation
 * Throws exception when the ticket id does not exist, when the current state does not match with 'to assign' state
 * when all updatable parameters are null, when there is no row updated
 */
CREATE OR REPLACE PROCEDURE refuse_ticket(ticket_rep OUT JSON, ticket_id BIGINT, person_id UUID)
AS
$$
DECLARE
    employee_state_id INT = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Refused');
    role TEXT = get_person_active_role(person_id);
BEGIN
    -- If is admin or manager and belongs to the same company of the ticket can refuse the ticket
    IF (role = 'admin' OR role = 'manager' AND ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        CALL change_ticket_state(ticket_id, employee_state_id, ticket_rep);
    END IF;
END$$
LANGUAGE plpgsql;

/*
 * Change ticket employee_state
 * Returns the ticket item representation
 * Throws exception when the ticket id does not exist, when the ticket is archived or when no rows affected.
 */
CREATE OR REPLACE PROCEDURE change_ticket_state(
    ticket_rep OUT JSON,
    ticket_id BIGINT,
    person_id UUID,
    t_new_employee_state INT
)
AS
$$
DECLARE
    t_curr_employee_state INT = (SELECT employee_state FROM TICKET WHERE id = ticket_id);
    role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    IF (role = 'employee') THEN
        PERFORM ticket_belongs_to_employee(ticket_id, person_id);
    ELSEIF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;

    --if the current employee_state does not have a transition to the new_state
    IF NOT EXISTS (SELECT first_employee_state FROM EMPLOYEE_STATE_TRANS
        WHERE first_employee_state = t_curr_employee_state AND second_employee_state = t_new_employee_state
    ) THEN
        RAISE 'resource-not-found' USING DETAIL = 'transition', HINT = t_new_employee_state;
    --if the new state is the end of the ticket, will set the close_timestamp
    ELSEIF (t_new_employee_state IN (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Archived' OR name = 'Refused')) THEN
        UPDATE TICKET SET employee_state = t_new_employee_state, close_timestamp = CURRENT_TIMESTAMP
        WHERE id = ticket_id OR parent_ticket = ticket_id;
    ELSE
        IF (t_new_employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Completed')) THEN
            UPDATE FIXING_BY SET end_timestamp = CURRENT_TIMESTAMP WHERE ticket = ticket_id;
        END IF;
        UPDATE TICKET SET employee_state = t_new_employee_state WHERE id = ticket_id OR parent_ticket = ticket_id;
    END IF;

    ticket_rep = ticket_item_representation(ticket_id);
END$$
LANGUAGE plpgsql;

/*
 * Gets a specific ticket
 * Returns the ticket representation
 * Throws exception when the ticket id does not exist
 */
CREATE OR REPLACE FUNCTION get_ticket (
    ticket_id BIGINT,
    person_id UUID,
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
    role TEXT = get_person_active_role(person_id);
    employeeId UUID = (SELECT person FROM FIXING_BY WHERE (ticket = (SELECT parent_ticket FROM TICKET WHERE id = ticket_id) OR ticket = ticket_id) AND end_timestamp IS NULL);
    employee JSON = NULL;
    users_rate INT;
BEGIN
    IF(employeeId IS NOT NULL) THEN employee = person_item_representation(employeeId); END IF;

    PERFORM ticket_exists(ticket_id);
    IF (role = 'user') THEN
        PERFORM ticket_belongs_to_user(ticket_id, person_id);
    ELSEIF (role = 'employee') THEN
        PERFORM ticket_belongs_to_employee(ticket_id, person_id);
    ELSEIF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;
    --Obtain all values to represent ticket
    SELECT
           subject, description, creation_timestamp, es.name, us.name, p.id, p.name, p.phone, p.email,
           c.id, c.name, c.state, c.timestamp, b.id, b.name, b.floors, b.state, b.timestamp, r.id, r.name,
           r.floor, r.state, r.timestamp, d.id, d.name, d.state, d.timestamp, ct.name
    FROM TICKET t
        INNER JOIN EMPLOYEE_STATE es ON t.employee_state = es.id
        INNER JOIN USER_STATE us ON es.user_state = us.id
        INNER JOIN PERSON p ON p.id = t.reporter
        INNER JOIN DEVICE d on t.device = d.id
        INNER JOIN ROOM r on t.room = r.id
        INNER JOIN BUILDING b on b.id = r.building
        INNER JOIN COMPANY c on c.id = b.company
        INNER JOIN CATEGORY ct on ct.id = d.category
    WHERE t.id = ticket_id
        INTO t_subject, t_desc, t_creation_time, t_employee_state, t_user_state, p_id, p_name, p_phone, p_email,
        c_id, c_name, c_state, c_timestamp, b_id, b_name, b_floors, b_state, b_timestamp, r_id, r_name, r_floor,
        r_state, r_timestamp, d_id, d_name, d_state, d_timestamp, ct_name;
    IF (t_subject IS NULL) THEN
        RAISE 'resource-not-found' USING DETAIL = 'ticket', HINT = ticket_id;
    END IF;

    IF (t_employee_state = 'Archived') THEN
        users_rate = (SELECT AVG(rate) FROM RATE WHERE ticket = ticket_id);
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
            'rate', users_rate, 'possibleTransitions', t_possibleTransitions),
        'ticketComments', get_comments(ticket_id, comments_direction, limit_rows, skip_rows),
        'person', person_item_representation(p_id),
        'company', company_item_representation(c_id, c_name, c_state, c_timestamp),
        'building', building_item_representation(b_id, b_name, b_floors, b_state, b_timestamp),
        'room', room_item_representation(r_id, r_name, r_floor, r_state, r_timestamp),
        'device', device_item_representation(d_id, d_name, ct_name, d_state, d_timestamp),
        'employee', employee,
        'parentTicket', (SELECT parent_ticket FROM TICKET WHERE id = ticket_id)
    );
END$$ LANGUAGE plpgsql;

/*
 * Gets all the tickets
 * Returns a list with all the tickets item representation
 */
CREATE OR REPLACE FUNCTION get_tickets(
    person_id UUID,
    company_id BIGINT DEFAULT NULL,
    building_id BIGINT DEFAULT NULL,
    limit_rows INT DEFAULT NULL,
    skip_rows INT DEFAULT NULL,
    sort_by TEXT DEFAULT 'date',
    direction TEXT DEFAULT 'desc',
    state_id INT DEFAULT NULL
)
RETURNS JSON
AS
$$
DECLARE
    rec RECORD; tickets JSON[]; collection_size INT = 0;
BEGIN
    FOR rec IN
        SELECT t.id, t.subject, t.description, t.employee_state, c.name as company, b.name as building, r.name as room
        FROM TICKET t
            INNER JOIN DEVICE d ON t.device = d.id
            INNER JOIN CATEGORY ct ON d.category = ct.id
            INNER JOIN ROOM r ON t.room = r.id
            INNER JOIN BUILDING b ON b.id = r.building
            INNER JOIN COMPANY c ON c.id = b.company
            FULL JOIN FIXING_BY fb ON t.id = fb.ticket
       WHERE
             (company_id IS NULL OR c.id = company_id) AND
             (building_id IS NULL OR b.id = building_id) AND
             (state_id IS NULL OR state_id = t.employee_state)
         AND
            CASE
                WHEN (get_person_active_role(person_id) = 'manager') THEN
                    b.manager = person_id
                WHEN (get_person_active_role(person_id) = 'user') THEN
                    t.reporter = person_id
                WHEN (get_person_active_role(person_id) = 'employee') THEN
                    fb.person = person_id
                ELSE TRUE
            END
        ORDER BY
            CASE WHEN direction='desc' AND sort_by='date' THEN creation_timestamp END DESC,
            CASE WHEN direction='asc' AND sort_by='date'  THEN creation_timestamp END ASC,
            CASE WHEN direction='desc' AND sort_by='name' THEN t.subject END DESC,
            CASE WHEN direction='asc' AND sort_by='name'  THEN t.subject END ASC
        LIMIT limit_rows OFFSET skip_rows
    LOOP
        tickets = array_append(
            tickets, ticket_item_representation(rec.id, rec.subject, rec.description,
                rec.employee_state, rec.company, rec.building, rec.room)
        );

    END LOOP;
    SELECT COUNT(t.id) INTO collection_size
        FROM TICKET t
            INNER JOIN DEVICE d ON t.device = d.id
            INNER JOIN CATEGORY ct ON d.category = ct.id
            INNER JOIN ROOM r ON t.room = r.id
            INNER JOIN BUILDING b ON b.id = r.building
            INNER JOIN COMPANY c ON c.id = b.company
            FULL JOIN FIXING_BY fb ON t.id = fb.ticket
       WHERE
            CASE
                WHEN (get_person_active_role(person_id) = 'manager') THEN
                    b.manager = person_id
                WHEN (get_person_active_role(person_id) = 'user') THEN
                    t.reporter = person_id
                WHEN (get_person_active_role(person_id) = 'employee') THEN
                    fb.person = person_id
                ELSE TRUE
            END;
    RETURN json_build_object('tickets', tickets, 'ticketsCollectionSize', collection_size);
END$$ LANGUAGE plpgsql;

/*
 * Set a employee to a ticket
 * Returns the ticket and the employee representation
 * Throws exception when the employee does not has the necessary skill or if the ticket already have a employee
 */
CREATE OR REPLACE PROCEDURE set_ticket_employee(
    ticket_rep OUT JSON,
    person_id UUID,
    new_employee_id UUID,
    ticket_id BIGINT
)
AS
$$
DECLARE role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    IF (is_ticket_assigned(ticket_id)) THEN
        RAISE 'fixing-ticket';
    END IF;
    IF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;

    --verify if the employee has the necessary skill to resolve the ticket problem
    IF NOT EXISTS (SELECT category FROM PERSON_SKILL WHERE person = new_employee_id
      AND category = (SELECT category FROM DEVICE WHERE id = (SELECT device FROM TICKET WHERE id = ticket_id))) THEN
        RAISE EXCEPTION 'ticket-employee-skill-mismatch';
    END IF;

    UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'Not started')
    WHERE id = ticket_id OR parent_ticket = ticket_id;

    INSERT INTO FIXING_BY (ticket, person) VALUES (ticket_id, new_employee_id);

    ticket_rep = json_build_object(
        'ticket', ticket_item_representation(ticket_id),
        'person', person_item_representation(new_employee_id));
END$$
LANGUAGE plpgsql;

/**
  * Remove ticket employee
  * Returns the removed ticket and the employee representation
  * Throws exception when employee does not has the necessary skill or if the ticket already have a employee
  */
CREATE OR REPLACE PROCEDURE remove_ticket_employee(
    ticket_rep OUT JSON,
    ticket_id BIGINT,
    person_id UUID
)
AS
$$
DECLARE
    t_employee_state INT; employee_id UUID;
    role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);

    IF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;

    SELECT employee_state INTO t_employee_state FROM TICKET WHERE id = ticket_id;
    IF (t_employee_state IN (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')) THEN
        -- If doesn't have an employee just returns
    ELSE
        UPDATE TICKET SET employee_state = (SELECT id FROM EMPLOYEE_STATE WHERE name = 'To assign')
        WHERE id = ticket_id OR parent_ticket = ticket_id;

        employee_id = (SELECT person FROM FIXING_BY WHERE ticket = ticket_id AND end_timestamp IS NULL);

        UPDATE FIXING_BY SET end_timestamp = CURRENT_TIMESTAMP WHERE person = employee_id AND ticket = ticket_id;
    END IF;

    ticket_rep = json_build_object(
        'ticket', ticket_item_representation(ticket_id),
        'person', person_item_representation(employee_id));
END$$
LANGUAGE plpgsql;

/**
  * Add ticket rate
  * Throws exception when the ticket isn't completed or when no rows affected
  */
CREATE OR REPLACE PROCEDURE add_ticket_rate(
    ticket_rep OUT JSON,
    ticket_id BIGINT,
    person_id UUID,
    rate_value INT
)
AS
$$
DECLARE
    t_subject TEXT; t_description TEXT; t_employeeSate TEXT; t_userState TEXT;
    role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    IF (role = 'user') THEN
        PERFORM ticket_belongs_to_user(ticket_id, person_id);
        SELECT employee_state INTO t_employeeSate FROM TICKET WHERE id = ticket_id;
        CASE
            WHEN (t_employeeSate = 'Archived') THEN
                RAISE 'ticket-rate';
            ELSE
                INSERT INTO RATE (person, ticket, rate) VALUES (person_id, ticket_id, rate_value);

                SELECT subject, description, es.name, us.name FROM TICKET t
                    INNER JOIN EMPLOYEE_STATE es ON t.employee_state = es.id
                    INNER JOIN USER_STATE us ON es.user_state = us.id
                    WHERE t.id = ticket_id INTO t_subject, t_description, t_employeeSate, t_userState;

                ticket_rep = json_build_object('id', ticket_id, 'subject', t_subject, 'description', t_description,
                    'employeeState', t_employeeSate, 'userState', t_userState, 'rate', rate_value);
            END CASE;
    END IF;
END$$
LANGUAGE plpgsql;

/**
  * Groups a ticket
  * Returns the ticket
 */
CREATE OR REPLACE PROCEDURE group_ticket(
    ticket_rep OUT JSON,
    ticket_id BIGINT,
    parent_tid BIGINT,
    person_id UUID
)
AS
$$
DECLARE
    role TEXT = get_person_active_role(person_id);
BEGIN
    PERFORM ticket_exists(ticket_id);
    PERFORM is_ticket_archived(ticket_id);
    PERFORM ticket_exists(parent_tid);
    PERFORM is_ticket_archived(parent_tid);

    IF (role = 'manager' AND NOT ticket_belongs_to_person_company(ticket_id, person_id)) THEN
        RAISE 'invalid-company' USING DETAIL = 'manager-ticket';
    END IF;

    UPDATE TICKET SET parent_ticket = parent_tid WHERE id = ticket_id;

    ticket_rep = ticket_item_representation(ticket_id);
END$$
LANGUAGE plpgsql;