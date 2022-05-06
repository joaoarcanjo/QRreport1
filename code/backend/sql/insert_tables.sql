/*
 * Default
 */

BEGIN;
    INSERT INTO COMPANY (name, state) VALUES
        ('ISEL', 'active'),   -- with 2 buildings
        ('ISCAL', 'active'),  -- with 1 building
        ('ESTSEL', 'active'), -- with 0 buildings
        ('ESD', 'inactive');

    INSERT INTO PERSON (id, name, phone, email, password, state) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315','Diogo Novo', '961111111', 'diogo@isel.com', 'diogopass', 'active'),         --user
        ('3ef6f248-2ef1-4dba-ad73-efc0cfc668e3','João Arcanjo', '962222222', 'joao@isel.com', 'joaopass', 'active'),         --user
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057','Pedro Miguens', '963333333', 'pedro@isel.com', 'pedropass', 'active'),      --manager
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b','Elon Musk', '964444444', 'elon@isel.com', 'elonpass', 'active'),            --manager
        ('c2b393be-d720-4494-874d-43765f5116cb','Jeff Bezos', '965555555', 'jeff@isel.com', 'jeffpass', 'active'),           --employee
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b','Bill Gates', '966666666', 'bill@isel.com', 'billpass', 'active'),           --employee
        ('0a8b83ec-7675-4467-91e5-33e933441eee','Tim Berners-Lee', '977777777', 'tim@isel.com', 'timpass', 'active'),        --admin / manager
        ('bb692591-1c74-40ce-99c0-c9b185fd78a9','James Gosling', '968888888', 'james@isel.com', 'jamespass', 'active'),      --guest
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d','Steve Jobs', '969999999', 'steve@isel.com', 'stevepass', 'inactive');       --guest

    INSERT INTO BUILDING (name, floors, state, company, manager) VALUES
        ('A', 4, 'active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'), -- Pedro Miguens, ISEL
        ('F', 6, 'active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'), -- Pedro Miguens, ISEL
        ('1', 7, 'active', 2, '9c06c8f3-ceda-48c5-99a7-29903a921a5b'); -- Elon Musk, ISCAL

    INSERT INTO ROOM (name, floor, state, building, qr_hash) VALUES
        ('Biblioteca', 1, 'active', 1, 'E09C80C42FDA55F9D992E59CA6B3307D'),
        ('ByChef', 1, 'active', 1, '82136B4240D6CE4EA7D03E51469A393B'),
        ('Sala de reuniões', 2, 'active', 2, '604619CB901441B21D1255BD52DABA43'),
        ('Sala de estudos', -1, 'inactive', 2, '6E8728A625986BCBF13C1E6772BC14C8'),
        ('Casa de banho', 7, 'active', 3, '39C8E9953FE8EA40FF1C59876E0E2F28');

    INSERT INTO CATEGORY (name, state) VALUES
        ('canalization', 'active'),
        ('electricity', 'active'),
        ('software', 'active'),
        ('network', 'active'),
        ('cLeaning', 'active'),
        ('woodworker', 'active');

    INSERT INTO ROLE (name) VALUES
        ('guest'),
        ('user'),
        ('employee'),
        ('manager'),
        ('admin');

    INSERT INTO PERSON_SKILL (person, category) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1), -- Jeff Bezos / canalization
        ('c2b393be-d720-4494-874d-43765f5116cb', 2), -- Jeff Bezos / electricity
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 3), -- Bill Gates / software
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 4), -- Bill Gates / network
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 6); -- Bill Gates / woodworker

    INSERT INTO PERSON_ROLE (person, role) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 2), -- Diogo Novo / user
        ('3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 2), -- João Arcanjo / user
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 4), -- Pedro Miguens /manager
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b', 4), -- Elon Musk / manager
        ('c2b393be-d720-4494-874d-43765f5116cb', 3), -- Jeff Bezos / employee
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 3), -- Bill Gates / employee
        ('0a8b83ec-7675-4467-91e5-33e933441eee', 5), -- Tim Berners-Lee / admin
        ('0a8b83ec-7675-4467-91e5-33e933441eee', 4), -- Tim Berners-Lee / manager
        ('bb692591-1c74-40ce-99c0-c9b185fd78a9', 1), -- James Gosling / guest
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d', 1); -- Steve Jobs / guest inactive

    INSERT INTO USER_STATE (name) VALUES --TODO: change states names
        ('state1'),
        ('state2'),
        ('state3'),
        ('state4'),
        ('state5'),
        ('state6');

    INSERT INTO EMPLOYEE_STATE (name, user_state) VALUES
        ('On execution', 1),
        ('Waiting for help', 2),
        ('Waiting for material', 3),
        ('Almost concluded', 4),
        ('To assign', 5),
        ('Concluded', 6);

    --ticket conter o room_device :)
    INSERT INTO TICKET (subject, description, room, reporter, employee_state, category) VALUES
        ('Fuga de água', 'Descrição de fuga de água', 2, '4b341de0-65c0-4526-8898-24de463fc315', 1, 1),
        ('Torneira avariada', 'Descrição de torneira avariada', 5, 'bb692591-1c74-40ce-99c0-c9b185fd78a9', 2, 1),
        ('Mesa partida', 'Descrição de mesa partida', 1, '4b341de0-65c0-4526-8898-24de463fc315', 4, 6),
        ('Wifi 5G não encontrado', 'Descrição de  wifi 5G não encontrado', 4, '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 3, 5);

    --tickets concluded
    INSERT INTO TICKET (subject, description, close_timestamp, room, reporter, employee_state, category) VALUES
        ('Corrimão danificado', 'Descrição de corrimão danificado', '2222-05-06 09:59:34.964477+00', 1, '4b341de0-65c0-4526-8898-24de463fc315', 6, 6),
        ('Infiltração na parede', 'Descrição de infiltração na parede', '2222-05-06 09:59:34.964477+00', 5, '3ef6f248-2ef1-4dba-ad73-efc0cfc668e3', 6, 5);

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
        (1, 'Comentário ao trabalho realizado em corrimão danificado', '4b341de0-65c0-4526-8898-24de463fc315', 5),
        (2, 'Comentário ao trabalho realizado em corrimão danificado', '4b341de0-65c0-4526-8898-24de463fc315', 5);

    INSERT INTO PERSON_COMPANY (person, company, state) VALUES
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 1, 'active'), -- Pedro Miguens, ISEL
        ('9c06c8f3-ceda-48c5-99a7-29903a921a5b', 2, 'active'), -- Elon Musk, ISCAL
        ('c2b393be-d720-4494-874d-43765f5116cb', 1, 'active'), -- Jeff Bezos, ISEL
        ('c2b393be-d720-4494-874d-43765f5116cb', 2, 'active'), -- Jeff Bezos, ISCAL
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 1, 'active'), -- Bill Gates, ISEL
        ('e85c73aa-7869-4861-a1cc-ca30d7c8499b', 2, 'active'); -- Bill Gates, ISCAL

    INSERT INTO DEVICE (name, state) VALUES
        ('Conjunto de tomadas', 'active'),
        ('Cubiculo casa de banho', 'active'),
        ('Bacia de lavatório', 'active'),
        ('Sanita', 'active'),
        ('Fogão', 'active'),
        ('Arcondicionado', 'active'),
        ('Torneira', 'active'),
        ('Estante de livros', 'active');

    INSERT INTO ANOMALY (id, device, anomaly) VALUES
        (1, 1, 'Não funciona'),
        (1, 1, 'Cheira a queimado'),
        (1, 1, 'A realizar curto circuito'),
        (1, 2, 'Sanita partida');

    INSERT INTO ROOM_DEVICE (room, device, qr_hash) VALUES
        (1, 1, 'C99634E23FEBDA60D8529D27A9A9DAAC'),
        (1, 8, '036E33B1E6452691159608DA77A2AA30'),
        (2, 3, 'F171615AB5C9B77CFBD8693332307C5F'),
        (2, 5, 'EC139CB12714BEABD1E4F6075C00BB34'),
        (2, 7, 'DAF1977AEE27CF4145BA11E42E529BD2'),
        (3, 1, '6402BE1FE34D8C0C35F4F5F1FB6236BE'),
        (4, 1, 'E9277D386E6A2999DD8FD7A67D67B9AC'),
        (4, 6, '82AFC3D802392509EBF4EB1C6C7752F7'),
        (5, 2, 'BF1808F907F87899868B29C201567B8A'),
        (5, 3, 'A3C36C0D9CAAC9A25E28F1F4A948C80C'),
        (5, 7, '6A1F680E0AEF0F04729F3A88B088502F');
END;