insert into mooc_users(id, username, name, mobile, password_hash, email, enabled, account_non_expired, account_non_locked, credentials_non_expired) values(1, 'user', 'zhang san', '17346511540', '{bcrypt}$2a$10$czDXnYgcuHPqdYwewS1tAOVn0WZdUU6kx7WV.Lh.3WxPiEDaUwRTG','cxl17258@qq.com',1, 1,1,1);
insert into mooc_users(id, username, name, mobile, password_hash, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)values (2, 'old_user', 'Li si','17346511541', '{SHA-1}{V+M6G5s38TVSgzNseMHGDrduLjN06mJ3btCmcMDC8b4=}600f76a9d83a495b426e4507bd5decaca0b826e4','cxl@qq.com', 1,1,1,1);

insert into mooc_roles(id, role_name) values(1, 'ROLE_USER'),(2, 'ROLE_ADMIN');
insert into mooc_users_roles(user_id, role_id) values(1,1),(1,2),(2,1);
