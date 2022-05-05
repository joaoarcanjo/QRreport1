BEGIN;
    CREATE TABLE COMPANY
    (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT company_name_max_length CHECK ( char_length(name) <= 50 ),
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT company_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP
    );

    CREATE TABLE PERSON
    (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        name TEXT NOT NULL CONSTRAINT company_name_max_length CHECK ( char_length(name) <= 50 ),
        phone TEXT, -- TODO: check if there are only digits
        email TEXT NOT NULL
            CONSTRAINT person_email_not_unique UNIQUE
            CONSTRAINT person_email_valid_format CHECK ( email LIKE '%@%' )
            CONSTRAINT person_email_max_length CHECK ( char_length(email) <= 320 ),
        password TEXT NOT NULL CONSTRAINT person_password_max_length CHECK ( char_length(password) <= 127 ),
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT person_valid_state CHECK ( state IN ('active', 'inactive', 'banned') ),
        start_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        inactive_timestamp TIMESTAMP,
        reason TEXT CONSTRAINT person_reason_max_length CHECK ( char_length(name) <= 150 ),
        banned_by UUID REFERENCES PERSON(id) CONSTRAINT person_cannot_banish_herself CHECK ( banned_by != id )
    );

    CREATE TABLE BUILDING
    (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT building_name_max_length CHECK ( char_length(name) <= 50 ),
        floors SMALLINT NOT NULL,
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT company_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP,
        company BIGINT NOT NULL REFERENCES COMPANY(id),
        manager UUID NOT NULL REFERENCES PERSON(id)
    );

    CREATE TABLE ROOM
    (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT room_name_max_length CHECK ( char_length(name) <= 50 ),
        floor SMALLINT NOT NULL, -- TODO: Verificar se está dentro dos floors de building
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT room_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP,
        building BIGINT NOT NULL REFERENCES BUILDING(id),
        qr_hash TEXT UNIQUE
    );

    CREATE TABLE CATEGORY
    (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT category_name_max_length CHECK ( char_length(name) <= 50 ),
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT room_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP
    );

    CREATE TABLE ROLE
    (
        id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT role_valid_name CHECK ( name IN ('guest', 'user', 'employee', 'manager', 'admin') )
    );

    CREATE TABLE PERSON_SKILL
    (
        person UUID NOT NULL REFERENCES PERSON(id),
        category INT NOT NULL REFERENCES CATEGORY(id),
        PRIMARY KEY (person, category)
    );

    CREATE TABLE PERSON_ROLE
    (
        person UUID NOT NULL REFERENCES PERSON(id),
        role INT NOT NULL REFERENCES ROLE(id),
        PRIMARY KEY (person, role)
    );

    CREATE TABLE USER_STATE
    (
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT user_state_name_max_length CHECK ( char_length(name) <= 50 )
    );

    CREATE TABLE EMPLOYEE_STATE
    (
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL CONSTRAINT employee_state_name_max_length CHECK ( char_length(name) <= 50 ),
        user_state INT NOT NULL REFERENCES USER_STATE(id)
    );

    CREATE TABLE TICKET
    (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        subject TEXT NOT NULL CONSTRAINT ticket_name_max_length CHECK ( char_length(subject) <= 50 ),
        description TEXT CONSTRAINT ticket_description_max_length CHECK ( char_length(description) <= 200 ),
        creation_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        close_timestamp TIMESTAMP CONSTRAINT ticket_valid_close_timestamp CHECK ( creation_timestamp > close_timestamp ),
        room BIGINT NOT NULL REFERENCES ROOM(id),
        reporter UUID NOT NULL REFERENCES PERSON(id),
        employee_state INT NOT NULL REFERENCES EMPLOYEE_STATE(id)
    );

    CREATE TABLE FIXING_BY
    (
        person UUID NOT NULL REFERENCES PERSON(id),
        ticket BIGINT NOT NULL REFERENCES TICKET(id),
        start_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (person, ticket)
    );

    CREATE TABLE RATE
    (
        person UUID NOT NULL REFERENCES PERSON(id),
        ticket BIGINT NOT NULL REFERENCES TICKET(id),
        rate SMALLINT NOT NULL CONSTRAINT rate_valid_value CHECK ( rate BETWEEN 1 AND 5),
        PRIMARY KEY (person, ticket)
    );

    CREATE TABLE COMMENT
    (
        id BIGINT NOT NULL,
        comment TEXT NOT NULL CONSTRAINT comment_max_length CHECK ( char_length(comment) <= 200 ),
        timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        person UUID NOT NULL REFERENCES PERSON(id),
        ticket BIGINT NOT NULL REFERENCES TICKET(id),
        PRIMARY KEY (id, person, ticket)
    );

    -- TODO: Trigger para quando este estado se alterar para inativo, alterar os employees em PERSON para inativo e colocar
    -- a razão que a company ficou inativa, mas só se o employee não estiver em mais NENHUMA empresa!!!
    CREATE TABLE PERSON_COMPANY
    (
        person UUID NOT NULL REFERENCES PERSON(id),
        company BIGINT NOT NULL REFERENCES COMPANY(id),
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT person_company_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP
    );

    CREATE TABLE DEVICE(
        id BIGINT GENERATED ALWAYS AS IDENTITY,
        name TEXT NOT NULL CONSTRAINT device_name_max_length CHECK ( char_length(name) <= 50 ),
        state TEXT NOT NULL DEFAULT 'active' CONSTRAINT room_valid_state CHECK ( state IN ('active', 'inactive') ),
        inactive_timestamp TIMESTAMP
    );

    CREATE TABLE ANOMALY(
        id BIGINT NOT NULL,
        device BIGINT NOT NULL REFERENCES DEVICE(id),
        anomaly TEXT NOT NULL CONSTRAINT device_anomaly_max_length CHECK ( char_length(anomaly) <= 150 )
    );

    CREATE TABLE ROOM_DEVICE(
        room BIGINT NOT NULL REFERENCES ROOM(id),
        device BIGINT NOT NULL REFERENCES DEVICE(id),
        qr_hash TEXT
    );
END;