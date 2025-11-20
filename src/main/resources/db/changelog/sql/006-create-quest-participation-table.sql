--liquibase formatted sql

--changeset daniilsanets:006-1
--comment Create quests_status enum type
CREATE TYPE quests_status AS ENUM ('STARTED', 'SUBMITTED', 'APPROVED','REJECTED','CANCELLED');
--rollback DROP TYPE quests_status;

--changeset daniilsanets:006-2
--comment Create quest_participation table
CREATE TABLE quest_participation (
                                     uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     user_uid UUID NOT NULL,
                                     quest_uid UUID NOT NULL,
                                     quest_status quests_status NOT NULL,
                                     started_at TIMESTAMPTZ NOT NULL,
                                     score INT NOT NULL,
                                     created_at TIMESTAMPTZ DEFAULT NOW(),
                                     updated_at TIMESTAMPTZ DEFAULT NOW()
);
--rollback DROP TABLE quest_participation;

--changeset daniilsanets:006-3
--comment Add foreign key to user table
ALTER TABLE quest_participation
    ADD CONSTRAINT fk_quest_participation_user_uid
        FOREIGN KEY (user_uid)
            REFERENCES users(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE quest_participation DROP CONSTRAINT fk_quest_participation_user_uid;

--changeset daniilsanets:006-4
--comment Add foreign key to quest table
ALTER TABLE quest_participation
    ADD CONSTRAINT fk_quest_participation_quest_uid
        FOREIGN KEY (quest_uid)
            REFERENCES quest(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE quest_participation DROP CONSTRAINT fk_quest_participation_quest_uid;

--changeset daniilsanets:006-5
--comment Add positive check on score
ALTER TABLE quest_participation
    ADD CONSTRAINT positive_score_check
        CHECK ( score > 0 );
--rollback ALTER TABLE quest_participation DROP CONSTRAINT positive_score_check;