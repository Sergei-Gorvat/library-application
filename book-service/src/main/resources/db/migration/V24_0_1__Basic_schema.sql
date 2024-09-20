create schema if not exists catalogue;

create table catalogue.t_book
(
id serial primary key,
c_title varchar(100) not null check (length(trim(c_title)) >= 3),
c_author varchar(100) not null check (length(trim(c_author)) >= 3),
c_publication  smallint not null check(c_publication >= 1800 and c_publication <= 2024)
);


