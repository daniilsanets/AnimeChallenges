--liquibase formatted sql

--changeset daniilsanets:002-1
--comment Create user_roles enum type
CREATE TYPE user_roles AS ENUM ('GUEST','USER','ADMIN');
--rollback DROP TYPE user_roles;

--changeset daniilsanets:002-2
--comment Create user table
CREATE TABLE "user" (
                        uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        email VARCHAR(320) UNIQUE NOT NULL,
                        username VARCHAR(100) UNIQUE NOT NULL,
                        password_hash VARCHAR(200) NOT NULL,
                        role user_roles NOT NULL,
                        nickname VARCHAR(200),
                        avatar_media_uid UUID,
                        bio TEXT,
                        is_active BOOLEAN DEFAULT TRUE,
                        created_at TIMESTAMPTZ DEFAULT now(),
                        updated_at TIMESTAMPTZ DEFAULT now()
);
--rollback DROP TABLE "user";

--changeset daniilsanets:002-3
--comment Add foreign key to media table for avatar
ALTER TABLE "user"
    ADD CONSTRAINT fk_user_avatar_media_uid
        FOREIGN KEY (avatar_media_uid)
            REFERENCES media(uid)
            ON DELETE SET NULL;
--rollback ALTER TABLE "user" DROP CONSTRAINT fk_user_avatar_media_uid;
