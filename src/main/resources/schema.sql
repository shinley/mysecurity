drop table if exists mooc_users;
create table mooc_users (
    username varchar(50) not null ,
    password varchar(100) not null,
    enabled TINYINT not null default 1,
    primary key(username)
) engine = innodb;

drop table if exists mooc_authorities;
create table mooc_authorities(
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key (username) references mooc_users(username)
) engine=innodb;

create unique index ix_auth_username on mooc_authorities(username, authority)