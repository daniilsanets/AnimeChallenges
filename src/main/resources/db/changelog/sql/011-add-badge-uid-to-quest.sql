--liquibase formatted sql

--changeset daniilsanets:011-1
--comment add badge to quest
ALTER TABLE quest
    ADD COLUMN badge_uid UUID NOT NULL;
--rollback ALTER TABLE quest DROP COLUMN badge_uid;

--changeset daniilsanets:011-2
--comment add fk to badge
ALTER TABLE quest
    ADD CONSTRAINT fk_quest_badge_uid
        FOREIGN KEY (badge_uid)
            REFERENCES badge(uid)
            ON DELETE RESTRICT;
--rollback ALTER TABLE quest DROP CONSTRAINT fk_quest_badge_uid;

