--liquibase formatted sql

--changeset daniilsanets:008-1
--comment Create submission_media table
CREATE TABLE submission_media (
                                  uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  submission_uid UUID NOT NULL,
                                  media_uid UUID NOT NULL,
                                  created_at TIMESTAMPTZ DEFAULT NOW()
);
--rollback DROP TABLE submission_media;

--changeset daniilsanets:008-2
--comment Add foreign key to submission table
ALTER TABLE submission_media
    ADD CONSTRAINT fk_submission_media_submission_uid
        FOREIGN KEY (submission_uid)
            REFERENCES submission(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE submission_media DROP CONSTRAINT fk_submission_media_submission_uid;

--changeset daniilsanets:008-3
--comment Add foreign key to media table
ALTER TABLE submission_media
    ADD CONSTRAINT fk_submission_media_media_uid
        FOREIGN KEY (media_uid)
            REFERENCES media(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE submission_media DROP CONSTRAINT fk_submission_media_media_uid;
