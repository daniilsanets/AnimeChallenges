--liquibase formatted sql

--changeset daniilsanets:003-1
--comment Create badge table
CREATE TABLE badge (
                       uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       code VARCHAR(100) UNIQUE NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       image_media_uid UUID NOT NULL,
                       rule JSONB NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT now(),
                       updated_at TIMESTAMPTZ DEFAULT now()
);
--rollback DROP TABLE badge;

--changeset daniilsanets:003-2
--comment Add foreign key to media table for badge image
ALTER TABLE badge
    ADD CONSTRAINT fk_badge_image_media_uid
        FOREIGN KEY (image_media_uid)
            REFERENCES media(uid)
            ON DELETE RESTRICT;
--rollback ALTER TABLE badge DROP CONSTRAINT fk_badge_image_media_uid;
