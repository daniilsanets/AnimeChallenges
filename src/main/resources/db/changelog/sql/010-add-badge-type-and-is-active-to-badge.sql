--liquibase formatted sql

--changeset daniilsanets:010-1
--comment Create badge_types enum type
CREATE TYPE badge_types AS ENUM ('ACHIEVEMENT','QUEST_REWARD');
--rollback DROP TYPE badge_types;

--changeset daniilsanets:010-2
--comment I decide to add badge_type because we'll have common achievement, quest reward
ALTER TABLE badge
    ADD COLUMN badge_type badge_types NOT NULL
        DEFAULT 'ACHIEVEMENT';
--rollback ALTER TABLE badge DROP COLUMN badge_type;

--changeset daniilsanets:010-3
--comment added a lifetime for badges
ALTER TABLE badge
    ADD COLUMN is_active BOOLEAN NOT NULL
        DEFAULT true;
--rollback ALTER TABLE badge DROP COLUMN is_active;