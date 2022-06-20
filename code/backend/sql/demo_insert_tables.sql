BEGIN;
    INSERT INTO CATEGORY(name, state) VALUES
        ('water', 'Active'),
        ('electricity', 'Active');

    INSERT INTO ROLE(name) VALUES
        ('guest'),
        ('user'),
        ('employee'),
        ('manager'),
        ('admin');

    INSERT INTO COMPANY(name, state) VALUES
        ('ISEL', 'Active'); -- with 2 buildings

    INSERT INTO PERSON(id, name, phone, email, password, state) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315','Diogo Novo', '961111111', 'diogo@qrreport.com', 'diogopass', 'active'),--admin
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057','Pedro Miguens', '963333333', 'pedro@isel.com', 'pedropass', 'active'), --manager
        ('c2b393be-d720-4494-874d-43765f5116cb','Zé Manuel', '965555555', 'zeze@fixings.com', 'zepass', 'active'),      -- employee
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d','João Arcanjo', null, 'joni@isel.com', 'joaopass', 'active');           --guest

    INSERT INTO PERSON_ROLE(person, role) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 5), -- Diogo Novo / admin
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 4), -- Pedro Miguens / manager
        ('c2b393be-d720-4494-874d-43765f5116cb', 3), -- Zé Manuel / employee
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d', 1); -- João Arcanjo / guest

    INSERT INTO PERSON_SKILL (person, category) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1); -- Zé Manuel / canalization

    INSERT INTO PERSON_COMPANY (person, company, state) VALUES
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 1, 'active'); -- Pedro Miguens, ISEL

    INSERT INTO BUILDING(name, floors, state, company, manager) VALUES
        ('A', 4, 'Active', 1, '4b341de0-65c0-4526-8898-24de463fc315'),  -- Diogo Novo, ISEL
        ('F', 6, 'Active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057');  -- Pedro Miguens, ISEL

    INSERT INTO ROOM(name, floor, state, building) VALUES
        ('1 - Bathroom', 1, 'Active', 1),
        ('2', 1, 'Active', 1),
        ('1', 1, 'Active', 2);

    INSERT INTO DEVICE(name, state, category) VALUES
        ('Toilet1', 'Active', 1),
        ('Lights', 'Active', 2),
        ('Faucet', 'Active', 1);

    INSERT INTO ANOMALY(id, device, anomaly) VALUES
        (1, 1, 'The flush doesn''t work'),
        (2, 1, 'The water is overflowing'),
        (3, 1, 'The toilet is clogged'),
        (4, 1, 'The water is always running');

    INSERT INTO ROOM_DEVICE (room, device, qr_hash) VALUES
        (1, 1, '5abd4089b7921fd6af09d1cc1cbe5220'); -- (ISEL) 1 - Bathroom, Toilet1

    INSERT INTO USER_STATE (name) VALUES
        ('Waiting analysis'),
        ('Refused'),
        ('Not started'),
        ('Fixing'),
        ('Completed'),
        ('Archived');

    INSERT INTO EMPLOYEE_STATE (name, user_state) VALUES
        ('To assign', 1),
        ('Refused', 2),
        ('Not started', 3),
        ('Fixing', 4),
        ('Waiting for material', 4),
        ('Completed', 5),
        ('Archived', 6);

    INSERT INTO EMPLOYEE_STATE_TRANS (first_employee_state, second_employee_state) VALUES
        (1, 2),  -- To assign -> Refused
        (1, 3),  -- To assign -> Not started
        (3, 4),  -- Not started -> Fixing
        (4, 6),  -- Fixing -> Completed
        (6, 7);  -- Completed -> Archived

    INSERT INTO TICKET (subject, description, room, device, reporter, employee_state) VALUES
        ('Fuga de água', 'A sanita está a deixar sair água por baixo', 1, 1, '1f6c1014-b029-4a75-b78c-ba09c8ea474d', 4);

    INSERT INTO FIXING_BY (person, ticket) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1); -- Zé Manuel | Fuga de água

    INSERT INTO COMMENT (id, comment, person, ticket) VALUES
        (1, 'Esta sanita não tem arranjo, vou precisar de uma nova.', 'c2b393be-d720-4494-874d-43765f5116cb', 1),
        (2, 'Tente fazer o possível para estancar a fuga.', '4b341de0-65c0-4526-8898-24de463fc315', 1);
COMMIT;
