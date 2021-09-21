drop table if exists users;
create table users (
    username varchar(50) not null ,
    password varchar(100) not null,
    enabled TINYINT not null default 1,
    primary key(username)
) engine = innodb;

drop table if exists authorities;
create table authorities(
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key (username) references users(username)
) engine=innodb;

create unique index ix_auth_username on authorities(username, authority)