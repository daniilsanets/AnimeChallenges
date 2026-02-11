--liquibase formatted sql

--changeset daniilsanets:012-1
--comment Create media_type enum type
CREATE TYPE media_type AS ENUM ('PHOTO','LINK','VIDEO');
--rollback DROP TYPE media_type;

--changeset daniilsanets:012-2
--comment add media_type to submission_media
ALTER TABLE submission_media
    ADD COLUMN media_type media_type NOT NULL;
--rollback ALTER TABLE submission_media DROP COLUMN media_type;

--changeset daniilsanets:012-3
--comment delete submission_type from submission
ALTER TABLE submission
    DROP COLUMN submission_type;

--changeset daniilsanets:012-4
--comment drop submission_type as enum
DROP TYPE submission_type;

--changeset daniilsanets:012-5
--comment create new enum submission_type
CREATE TYPE submission_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
--rollback DROP TYPE submission_type;

--changeset daniilsanets:012-6
--comment add submission_status column to submission
ALTER TABLE submission
    ADD COLUMN submission_status submission_status NOT NULL DEFAULT 'PENDING';
--rollback ALTER TABLE submission DROP COLUMN submission_status;