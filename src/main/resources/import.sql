insert into role (name) values ("ROLE_USER")
insert into role (name) values ("ROLE_ADMIN")

insert into user (username, password) values ('admin' ,'$2a$12$5Ctx9MycUY4Z7IJjAVw5j.lMwPogj2hbigz0MXyxX5N708B7K3zei')
insert into user (username, password) values ('user' ,'$2a$12$5Ctx9MycUY4Z7IJjAVw5j.lMwPogj2hbigz0MXyxX5N708B7K3zei')

insert into user_role (user_id, role_id) values (1, 1)
insert into user_role (user_id, role_id) values (1, 2)
insert into user_role (user_id, role_id) values (2, 1)

insert into client (first_name, last_name, phone_number, email, birth_date) values ('Pedro Henrique', 'Vieira', '+55 86 981021180', 'pedrohenrick.dev@gmail.com', '2002-03-27 12:00:00')
