--liquibase formatted sql

--changeset svasenkov:TICKET-01_create_autotests_cloud_schema stripComments:false
CREATE SCHEMA IF NOT EXISTS autotests_cloud AUTHORIZATION demo_user;


--rollback drop schema autotests_cloud