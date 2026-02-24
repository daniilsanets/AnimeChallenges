--liquibase formatted sql

--changeset daniilsanets:014-1
--comment insert new type into enum
ALTER TYPE quests_status ADD VALUE 'PENDING' BEFORE 'STARTED';
