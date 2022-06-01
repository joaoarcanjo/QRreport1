/*
 * Default
 */

BEGIN;
    INSERT INTO COMPANY (name, state) VALUES
        ('ISEL', 'Active'),   -- with 2 buildings
        ('ISCAL', 'Active'),  -- with 1 building
        ('ESTSEL', 'Active'), -- with 0 buildings
        ('ESD', 'Inactive');

    INSERT INTO PERSON (id, name, phone, email, password, state) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315','Diogo Novo', '961111111', 'diogo@isel.com', 'diogopass', 'active'),         --user
        ('3ef6f248-2ef1-4dba-ad73-efc0cfc668e3','João Arcanjo', '962222222', 'joao@isel.com', 'joaopass', 'active'),         --user
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057','Pedro Miguens', '963333333', 'pedro@isel.com', 'pedropass', 'active'),      --manager
        ('996aff17-9d5c-48d4-b178-da7463e85652','Michael Phelps', '963131313', 'michael@isel.com', 'michaelpass', 'active'), --manager
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b','Elon Musk', '964444444', 'elon@isel.com', 'elonpass', 'active'),            --manager
        ('c2b393be-d720-4494-874d-43765f5116cb','Jeff Bezos', '965555555', 'jeff@isel.com', 'jeffpass', 'active'),           --employee
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b','Bill Gates', '966666666', 'bill@isel.com', 'billpass', 'active'),           --employee
        ('e85c73aa-7869-4861-a1cc-ca30d7c84123','Ivo Lucas', '966661111', 'ivo@isel.com', 'ivopass', 'active'),              --employee
        ('0a8b83ec-7675-4467-91e5-33e933441eee','Tim Berners-Lee', '977777777', 'tim@isel.com', 'timpass', 'active'),        --admin
        ('bb692591-1c74-40ce-99c0-c9b185fd78a9','James Gosling', '968888888', 'james@isel.com', 'jamespass', 'active'),      --guest
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d','Steve Jobs', '969999999', 'steve@isel.com', 'stevepass', 'inactive');       --guest

    INSERT INTO BUILDING (name, floors, state, company, manager) VALUES
        ('A', 4, 'Active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'), -- Pedro Miguens, ISEL
        ('F', 6, 'Active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'), -- Pedro Miguens, ISEL
        ('1', 7, 'Active', 2, '9c06c8f3-ceda-48c5-99a7-29903a921a5b'), -- Elon Musk, ISCAL
        ('2', 7, 'Inactive', 2, '9c06c8f3-ceda-48c5-99a7-29903a921a5b'); -- Elon Musk, ISCAL

    INSERT INTO ROOM (name, floor, state, building) VALUES
        ('Biblioteca', 1, 'Active', 1),
        ('ByChef', 1, 'Active', 1),
        ('Sala de reuniões', 2, 'Active', 2),
        ('Sala de estudos', -1, 'Active', 2),
        ('Casa de banho', 7, 'Active', 3);

    INSERT INTO CATEGORY (name, state) VALUES
        ('canalization', 'active'),
        ('electricity', 'active'),
        ('software', 'active'),
        ('network', 'active'),
        ('cleaning', 'active'),
        ('woodworker', 'active'),
        ('acclimatization', 'active');

    INSERT INTO ROLE (name) VALUES
        ('guest'),
        ('user'),
        ('employee'),
        ('manager'),
        ('admin');

    INSERT INTO PERSON_SKILL (person, category) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1), -- Jeff Bezos / canalization
        ('c2b393be-d720-4494-874d-43765f5116cb', 2), -- Jeff Bezos / electricity
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 2), -- Bill Gates / electricity
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 3), -- Bill Gates / software
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 4), -- Bill Gates / network
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 6), -- Bill Gates / woodworker
        ('e85c73aa-7869-4861-a1cc-ca30d7c84123', 1); -- Ivo Lucas / canalization

    INSERT INTO PERSON_ROLE (person, role) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 2), -- Diogo Novo / user
        ('3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 2), -- João Arcanjo / user
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 4), -- Pedro Miguens /manager
        ('996aff17-9d5c-48d4-b178-da7463e85652', 4), -- Michael Phelps /manager
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b', 4), -- Elon Musk / manager
        ('c2b393be-d720-4494-874d-43765f5116cb', 3), -- Jeff Bezos / employee
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 3), -- Bill Gates / employee
        ('0a8b83ec-7675-4467-91e5-33e933441eee', 5), -- Tim Berners-Lee / admin
        ('bb692591-1c74-40ce-99c0-c9b185fd78a9', 1), -- James Gosling / guest
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d', 1); -- Steve Jobs / guest inactive

    INSERT INTO DEVICE (name, state, category) VALUES
        ('Conjunto de tomadas', 'active', 2),
        ('Cubiculo casa de banho', 'active', 5),
        ('Bacia de lavatório', 'active', 1),
        ('Sanita', 'active', 1),
        ('Fogão', 'active', 2),
        ('Arcondicionado', 'active', 7),
        ('Torneira', 'active', 1),
        ('Estante de livros', 'active', 6);

    INSERT INTO ANOMALY (id, device, anomaly) VALUES
        (1, 1, 'Não funciona'),
        (1, 1, 'Cheira a queimado'),
        (1, 1, 'A realizar curto circuito'),
        (1, 2, 'Sanita partida');

    INSERT INTO ROOM_DEVICE (room, device, qr_hash) VALUES
        (1, 1, 'D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90'), -- 1-1-1
        (1, 8, '5177D42E73ACA3CB9ED62311287CB0BD646CE83396777C0CABA781AB594902DF'), -- 1-1-8
        (2, 3, 'BA101947992BA0AAD0D9074451EE3D37A65D881F7DD01E7AD45B6507C471F286'), -- 1-2-3
        (2, 5, 'D3791D0EA764FED312584CC33B162DB371F3D04CDF59990B1FD797130A46736B'), -- 1-2-5
        (2, 7, 'C27B14D96369E55E97B734D23BAC9714498A230E9B5D498B6CDC55F406C5CA13'), -- 1-2-7
        (3, 1, 'D3896B46417E65072B8E4D607CDFF6B405FCB1BB19DDC53B8D6D5EBDADD29D95'), -- 2-3-1
        (4, 1, '8101D29607F88ADDFB0D020C77D00A7C89109A665FFE3D6D27351629445C3F2A'), -- 2-4-1
        (4, 6, '6304CA426E42F2ED67C93105E0EB0881F304A3E74CBFC7D9A0BFA6E4F76B5755'), -- 2-4-6
        (5, 2, '109AFBC5B4D463D2D3A3FD88597368427DFE6AAE2117DADBDB7C4746C762EC44'), -- 3-5-2
        (5, 3, 'B5C639D606ED48D0629FCC11571961EB1AC81283E6372D2BE35EED0845604D26'), -- 3-5-3
        (5, 7, '5FCC82039A1FE62EAAF8948F5C614365FD0B1A2A584F24DF09D5A240E840017F'); -- 3-5-7

    INSERT INTO USER_STATE (name) VALUES --TODO: change states names
        ('On execution'),
        ('Waiting for accept'),
        ('Almost concluded'),
        ('Submitted'),
        ('Refused'),
        ('Completed'),
        ('Not Started');

    INSERT INTO EMPLOYEE_STATE (name, user_state) VALUES
        ('On execution', 1),             --1
        ('Waiting for help', 1),         --2
        ('Waiting for material', 1),     --3
        ('Waiting for new employee', 7), --4
        ('Almost concluded', 3),         --5
        ('To assign', 2),                --6
        ('Refused', 5),                  --7
        ('Concluded', 6),                --8
        ('Archived', 6);                 --9

    INSERT INTO EMPLOYEE_STATE_TRANS (first_employee_state, second_employee_state) VALUES
        (1, 2), -- On execution -> Waiting for help
        (1, 3), -- On execution -> Waiting for material
        --(1, 4), -- On execution -> Waiting for new employee
        (1, 5), -- On execution -> Almost concluded
        (1, 7), -- On execution -> Concluded
        (2, 1), -- Waiting for help -> On execution
        (2, 3), -- Waiting for help -> Waiting for material
        --(2, 4), -- Waiting for help -> Waiting for new employee
        (2, 5), -- Waiting for help -> Almost concluded
        (2, 8), -- Waiting for help -> Concluded
        (3, 1), -- Waiting for material -> On execution
        (3, 2), -- Waiting for material -> Waiting for help
        --(3, 4), -- Waiting for material -> Waiting for new employee
        (3, 5), -- Waiting for material -> Almost concluded
        (3, 6), -- Waiting for material -> Concluded
        (4, 1), -- Waiting for new employee -> On execution
        (4, 2), -- Waiting for new employee -> Waiting for help
        (4, 3), -- Waiting for new employee -> Waiting for material
        (4, 5), -- Waiting for new employee -> Almost concluded
        --(4, 6), -- Waiting for new employee -> Concluded
        (5, 1), -- Almost concluded -> On execution
        (5, 2), -- Almost concluded -> Waiting for help
        (5, 3), -- Almost concluded -> Waiting for material
        --(5, 4), -- Almost concluded -> Waiting for new employee
        (5, 7), -- Almost concluded -> Concluded
        (6, 1), -- To assign -> On execution
        (6, 7), -- To assign -> Refused
        (7, 8), -- Refused -> Archived
        (8, 8); -- Concluded -> Archived

    INSERT INTO TICKET (subject, description, room, device, reporter, employee_state) VALUES
        ('Fuga de água', 'Descrição de fuga de água', 2, 3, '4b341de0-65c0-4526-8898-24de463fc315', 5),
        ('Torneira avariada', 'Descrição de torneira avariada', 5, 7, 'bb692591-1c74-40ce-99c0-c9b185fd78a9', 2),
        ('Mesa partida', 'Descrição de mesa partida', 1, 8, '4b341de0-65c0-4526-8898-24de463fc315', 6),
        ('Tomada cheira a queimado', 'Descrição de tomada cheira a queimado', 4, 1, '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 6);

    INSERT INTO TICKET (subject, description, close_timestamp, room, device, reporter, employee_state) VALUES
        ('Corrimão danificado', 'Descrição de corrimão danificado', '2222-05-06 09:59:34.964477+00', 1, 8, '4b341de0-65c0-4526-8898-24de463fc315', 7),
        ('Infiltração na parede', 'Descrição de infiltração na parede', '2222-05-06 09:59:34.964477+00', 5, 7, '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 9),
        ('Porta partida', 'Descrição de porta partida', '2222-05-06 09:59:34.964477+00', 1, 8, '4b341de0-65c0-4526-8898-24de463fc315', 8);

    INSERT INTO FIXING_BY (person, ticket) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1), -- Jeff Bezos / Fuga de água
        ('c2b393be-d720-4494-874d-43765f5116cb', 2), -- Jeff Bezos / Torneira avariada
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 3), -- Bill Gates / Mesa partida
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 5), -- Bill Gates / Corrimão danificado
        ('c2b393be-d720-4494-874d-43765f5116cb', 6); -- Jeff Bezos / Infiltração na parede

    INSERT INTO RATE (person, ticket, rate) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 5, 4),
        ('3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 6, 5);

    INSERT INTO COMMENT (id, comment, person, ticket) VALUES
        (1, 'Comentário ao trabalho realizado em fuga de água', '4b341de0-65c0-4526-8898-24de463fc315', 1),
        (1, 'Comentário ao trabalho realizado em torneira avariada', '4b341de0-65c0-4526-8898-24de463fc315', 2),
        (2, 'Comentário ao trabalho realizado em fuga de água', '4b341de0-65c0-4526-8898-24de463fc315', 1),
        (2, 'Comentário ao trabalho realizado em torneira avariada', '4b341de0-65c0-4526-8898-24de463fc315', 2),
        (1, 'Comentário ao trabalho realizado em infiltração na parede', '4b341de0-65c0-4526-8898-24de463fc315', 6);

    INSERT INTO PERSON_COMPANY (person, company, state) VALUES
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 1, 'active'), -- Pedro Miguens, ISEL
        ('996aff17-9d5c-48d4-b178-da7463e85652', 1, 'active'), -- Michael Phelps, ISEL
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b', 2, 'active'), -- Elon Musk, ISCAL
        ('c2b393be-d720-4494-874d-43765f5116cb', 1, 'active'), -- Jeff Bezos, ISEL
        ('c2b393be-d720-4494-874d-43765f5116cb', 2, 'active'), -- Jeff Bezos, ISCAL
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 1, 'active'), -- Bill Gates, ISEL
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 2, 'active'); -- Bill Gates, ISCAL
COMMIT;