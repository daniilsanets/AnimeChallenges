--liquibase formatted sql

--changeset daniilsanets:009-1
--comment Created table to store refresh token
CREATE TABLE refresh_token(
    uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMPTZ NOT NULL,
    user_uid UUID
);
--rollback DROP TABLE refresh_token;

--changeset daniilsanets:009-2
-- fk to users table
ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_uid)
            REFERENCES users(uid)
                ON DELETE CASCADE;
--rollback ALTER TABLe refresh_token DROP CONSTRAINT;