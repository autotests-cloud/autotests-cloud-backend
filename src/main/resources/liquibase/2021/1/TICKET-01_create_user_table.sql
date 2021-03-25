--liquibase formatted sql

--changeset svasenkov:TICKET-01_create_user_table stripComments:false
create table autotests_cloud.user
(
id            bigint       not null
constraint pk_user
primary key
);


--rollback drop table autotests_cloud.user