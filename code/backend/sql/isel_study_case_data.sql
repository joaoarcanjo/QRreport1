BEGIN;
    INSERT INTO CATEGORY(name,state) VALUES
        ('water', 'active'),
        ('electricity', 'active');
/*        ('locksmith', 'active');*/

    INSERT INTO ROLE(name) VALUES
        ('guest'),
        ('user'),
        ('employee'),
        ('manager'),
        ('admin');

    INSERT INTO COMPANY(name) VALUES
        ('ISEL'); -- 3 buildings

    INSERT INTO PERSON(id, name, phone, email, password, state, active_role) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315','Diogo Novo', '961111111', 'diogo@qrreport.com', '$2a$10$4IeU1oTfxXRQFBVaUrSy9.xqxPLkT.dOFGVa9.VwmNF6WLDlQa04y', 'active', 5),--admin --diogopass
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d','João Arcanjo', '961215511', 'joni@isel.com', '$2a$10$FnfoD5NC8GRsZKBEH3pC5.Li3SYUOG1EyqAyiiSLJnnd2YHInWc..', 'active', 5),          --admin --joaopass
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057','Pedro Miguens', '963333333', 'pedro@isel.com', '$2a$10$xWfdwoxJBzp8J5M44GQ0veUBvJG3yacGdPGPXFgKKhoRaNhCMN6lG', 'active', 4), --manager --pedropass
        ('c2b393be-d720-4494-874d-43765f5116cb','Zé Manuel', '965555555', 'zeze@fixings.com', '$2a$10$DlrjEOpJUig4AqVV2yN2R.fSnSOmZwquBQuZi1cHLZStQMhpQjlmC', 'active', 3),   -- employee --zepass
--         ('c4bbd96f-a637-49c2-81ac-837308bfb7be','Idalvina Lopes', '935451444', 'idal@fixings.com', '$2a$10$DlrjEOpJUig4AqVV2yN2R.fSnSOmZwquBQuZi1cHLZStQMhpQjlmC', 'active', 3),   -- employee --zepass
        ('b9063a7e-7ba4-42d3-99f4-1b00e00db55d','Esmeralda Diamante', null, 'esme@alunos.isel.com', '$2a$10$mNL.45WNpF1W64J.RYKLYelmvmxIfAA7iTiOPQtfhEu7t4W62MKRy', 'active', 2); --user --danielapass

    INSERT INTO PERSON_ROLE(person, role) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 5), -- Diogo Novo / admin
        ('4b341de0-65c0-4526-8898-24de463fc315', 4), -- Diogo Novo / manager
        ('1f6c1014-b029-4a75-b78c-ba09c8ea474d', 5), -- João Arcanjo / admin
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 4), -- Pedro Miguens / manager
        ('c2b393be-d720-4494-874d-43765f5116cb', 3), -- Zé Manuel / employee
--         ('c4bbd96f-a637-49c2-81ac-837308bfb7be', 3), -- Idalvina Lopes / employee
        ('b9063a7e-7ba4-42d3-99f4-1b00e00db55d', 2); -- Esmeralda Diamante / user

    INSERT INTO PERSON_SKILL (person, category) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1); -- Zé Manuel / water
--         ('c4bbd96f-a637-49c2-81ac-837308bfb7be', 3); -- Idalvina Lopes / locksmith

    INSERT INTO PERSON_COMPANY (person, company, state) VALUES
        ('4b341de0-65c0-4526-8898-24de463fc315', 1, 'active'), -- Diogo Novo(manager), ISEL
        ('d1ad1c02-9e4f-476e-8840-c56ae8aa7057', 1, 'active'), -- Pedro Miguens(manager), ISEL
        ('c2b393be-d720-4494-874d-43765f5116cb', 1, 'active'); -- Zé Manuel(employee), ISEL
--         ('c4bbd96f-a637-49c2-81ac-837308bfb7be', 1, 'active'); -- Idalvina Lopes(employee), ISEL

    INSERT INTO BUILDING(name, floors, state, company, manager) VALUES
        ('A', 6, 'active', 1, '4b341de0-65c0-4526-8898-24de463fc315'), -- Diogo Novo, ISEL
        ('C', 6, 'active', 1, 'd1ad1c02-9e4f-476e-8840-c56ae8aa7057'); -- Pedro Miguens, ISEL

    INSERT INTO ROOM(name, floor, state, building) VALUES
        ('0.13', 1, 'active', 1),
        ('0.15', 1, 'active', 1),
        ('1.2(1)', 1, 'active', 2);

    INSERT INTO DEVICE(name, state, category) VALUES
        ('Lavatório', 'active', 1),
        ('Sanita', 'active', 1),
        ('Urinol', 'active', 1),
        ('Poliban', 'active', 1),
        ('Torneira de lavagem', 'active', 1);
--         ('Porta de cubíbulo', 'active', 1);

    INSERT INTO ANOMALY(device, anomaly) VALUES
        (1, 'Fora de serviço'),
        (1, 'Fuga de água à entrada do equipamento'),
        (1, 'Fuga de água à saída do equipamento'),
        (1, 'Torneira avariada (sem água)/danificada'),
        (1, 'Torneira com temporizador mal regulado/avariado'),
        (2, 'Fora de serviço'),
        (2, 'Entupimento'),
        (2, 'Autoclismo danificado (sem botão)'),
        (2, 'Fuga de água à entrada do equipamento'),
        (2, 'Fuga de água à saída do equipamento'),
        (2, 'Sem água'),
        (3, 'Fora de serviço'),
        (3, 'Fuga de água à entrada do equipamento'),
        (3, 'Fuga de água à saída do equipamento'),
        (3, 'Sem água'),
        (3, 'Fluxómetro avariado (sem água)'),
        (3, 'Fluxómetro com temporizador mal regulado/avariado'),
        (4, 'Fora de serviço'),
        (4, 'Fuga de água à entrada do equipamento'),
        (4, 'Fuga de água à saída do equipamento'),
        (4, 'Sem água'),
        (5, 'Fuga de água');
--         (6, 'Danificado/avariado/inexistente');

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
        (3, 4),  -- Not started -> Fixing
        (4, 6),  -- Fixing -> Completed
        (6, 4),  -- Fixing -> Completed
        (6, 7);  -- Completed -> Archived

/*    INSERT INTO TICKET (subject, description, room, device, reporter, employee_state) VALUES
        ('Fuga de água', 'A sanita está a deixar sair água por baixo', 1, 1, 'b9063a7e-7ba4-42d3-99f4-1b00e00db55d', 1),
        ('Torneira avariada (sem água)/danificada', 'Torneira avariada (sem água)/danificada', 1, 1, 'b9063a7e-7ba4-42d3-99f4-1b00e00db55d', 1);*/

    /*INSERT INTO FIXING_BY (person, ticket) VALUES
        ('c2b393be-d720-4494-874d-43765f5116cb', 1);*/ -- Zé Manuel | Fuga de água

    /*INSERT INTO COMMENT (comment, person, ticket) VALUES
        ('Esta sanita não tem arranjo, vou precisar de uma nova.', 'c2b393be-d720-4494-874d-43765f5116cb', 1),
        ('Tente fazer o possível para estancar a fuga.', '4b341de0-65c0-4526-8898-24de463fc315', 1);*/
COMMIT;