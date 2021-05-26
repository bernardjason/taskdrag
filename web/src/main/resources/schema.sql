DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS authorities;

create table users (
    username varchar(50) not null primary key,
    password varchar(120) not null,
    enabled boolean not null
);

create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    foreign key (username) references users (username)
);
/*
CREATE TABLE task_states (id bigint not null, colour varchar, display_order bigint not null, status varchar, primary key (id));
insert into task_states values(1,'#ffb3b3',1,'created');
insert into task_states values(2,'#ccffcc',2,'inprogress');
insert into task_states values(3,'#ff8888',3,'waiting');
insert into task_states values(4,'#ffff99',r,'done');
*/

insert into users(username, password, enabled)values('bernard','{noop}jason',true);
insert into authorities(username,authority)values('bernard','ROLE_USER');

/*
https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format
*/
insert into users(username, password, enabled)values('catherine','{noop}password',true);
insert into authorities(username,authority)values('catherine','ROLE_USER');





