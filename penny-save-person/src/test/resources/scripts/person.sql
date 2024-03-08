-- PERSON
insert into person(id_person, cd_external, ds_email, nm_person, dt_birth, in_status, ds_password, dt_created, dt_updated)
values (1001, 'd83dae5a0c194ccb8ee4', 'uncleduck@disney.com', 'Uncle Scrooge Mc Duck', '1947-12-01', 'A', '$2a$12$upe7Cs55.WbJJOOEhFP7SeQp1I560vjfl2C30aTEOIOqtgdEys6H6', '2024-01-01', '2024-01-01');

insert into person(id_person, cd_external, ds_email, nm_person, dt_birth, in_status, ds_password, dt_created, dt_updated)
values (1002, 'e94ebf6b1d205ddc9ff5', 'tony@stark.com', 'Tony Stark', '1963-03-01', 'P', '$2a$12$upe7Cs55.WbJJOOEhFP7SeQp1I560vjfl2C30aTEOIOqtgdEys6H6', '2024-01-01', '2024-01-01');

insert into person(id_person, cd_external, ds_email, nm_person, dt_birth, in_status, ds_password, dt_created, dt_updated)
values (1003, 'f05fcg7c2e316eed0gg6', 'montgomery@burns.com', 'Montgomery Burns', '1989-12-17', 'A', '$2a$12$upe7Cs55.WbJJOOEhFP7SeQp1I560vjfl2C30aTEOIOqtgdEys6H6', '2024-01-01', '2024-01-01');

-- EMAIL CONFIRMATION
insert into email_confirmation(id_email_confirmation, cd_token, dt_created, dt_limit_confirmation, in_email_confirmed, in_person_action, id_person)
values (1001, '8d793ceef277487eb84d7d544842ef2a', now(), now(), 'N', 'C', 1001);

insert into email_confirmation(id_email_confirmation, cd_token, dt_created, dt_limit_confirmation, in_email_confirmed, in_person_action, id_person)
values (1002, '9e804dffg388598fc95e8e655953fg3b', now(), now(), 'Y', 'C', 1003);