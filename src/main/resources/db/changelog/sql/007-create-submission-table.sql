--liquibase formatted sql

--changeset daniilsanets:007-1
--comment Create submission_status enum type
CREATE TYPE submission_status AS ENUM ('PHOTO', 'VIDEO', 'TEXT', 'LINK');
--rollback DROP TYPE submission_status;

--changeset daniilsanets:007-2
--comment Create submission table
CREATE TABLE submission (
                            uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            participation_uid UUID NOT NULL,
                            submission_type submission_status NOT NULL,
                            description TEXT NOT NULL,
                            notes TEXT NOT NULL,
                            submitted_at TIMESTAMPTZ NULL,
                            rejected_at TIMESTAMPTZ NULL,
                            approved_at TIMESTAMPTZ NULL,
                            created_at TIMESTAMPTZ DEFAULT NOW(),
                            updated_at TIMESTAMPTZ DEFAULT NOW()
);
--rollback DROP TABLE submission;

--changeset daniilsanets:007-3
--comment Add foreign key to quest_participation table
ALTER TABLE submission
    ADD CONSTRAINT fk_submission_participation_uid
        FOREIGN KEY (participation_uid)
            REFERENCES quest_participation(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE submission DROP CONSTRAINT fk_submission_participation_uid;
