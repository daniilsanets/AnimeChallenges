--liquibase formatted sql

--changeset daniilsanets:001-1
--comment Enable pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--rollback DROP EXTENSION pgcrypto;

--changeset daniilsanets:001-2
--comment Create media table
CREATE TABLE media (
                       uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       storage_key TEXT NOT NULL,
                       url TEXT NOT NULL,
                       mime VARCHAR(100) NOT NULL,
                       size BIGINT NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT now(),
                       updated_at TIMESTAMPTZ DEFAULT now()
);
--rollback DROP TABLE media;

--changeset daniilsanets:001-3
--comment Add check constraint for size
ALTER TABLE media
    ADD CONSTRAINT chk_media_size_positive CHECK (size > 0);
--rollback ALTER TABLE media DROP CONSTRAINT chk_media_size_positive;
