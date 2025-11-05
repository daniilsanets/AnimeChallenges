--liquibase formatted sql

--changeset daniilsanets:005-1
--comment Create quests_difficulty enum type
CREATE TYPE quests_difficulty AS ENUM ('EASY', 'MEDIUM', 'HARD', 'EXPERT', 'MASTER');
--rollback DROP TYPE quests_difficulty;

--changeset daniilsanets:005-2
--comment Create quest table
CREATE TABLE quest (
                       uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       title VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       difficulty quests_difficulty NOT NULL,
                       reward_points INT NOT NULL DEFAULT 0,
                       max_attempts INT NULL,
                       creator_uid UUID,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMPTZ DEFAULT NOW(),
                       updated_at TIMESTAMPTZ DEFAULT NOW()
);
--rollback DROP TABLE quest;

--changeset daniilsanets:005-3
--comment Add foreign key to user table (creator_uid)
ALTER TABLE quest
    ADD CONSTRAINT fk_quest_creator_uid
        FOREIGN KEY (creator_uid)
            REFERENCES users(uid)
            ON DELETE SET NULL;
--rollback ALTER TABLE quest DROP CONSTRAINT fk_quest_creator_uid;

--changeset daniilsanets:005-4
--comment Add range check on reward_point
ALTER TABLE quest
    ADD CONSTRAINT range_check_reward_points
        CHECK ( reward_points >= 0 AND reward_points <= 10);
--rollback ALTER TABLE quest DROP CONSTRAINT range_check_reward_points;

--changeset daniilsanets:005-5
--comment Add validation on max_attempts
ALTER TABLE quest
    ADD CONSTRAINT range_check_max_attempts
        CHECK ( max_attempts >= 0 AND max_attempts <= 5 );
--rollback ALTER TABLE quest DROP CONSTRAINT range_check_max_attempts;