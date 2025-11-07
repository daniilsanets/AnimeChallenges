--liquibase formatted sql

--changeset daniilsanets:004-1
--comment Create user_badge table to link users and badges
CREATE TABLE user_badge (
                            user_uid UUID,
                            badge_uid UUID NOT NULL,
                            awarded_at TIMESTAMPTZ DEFAULT now(),
                            PRIMARY KEY (user_uid, badge_uid)
);
--rollback DROP TABLE user_badge;

--changeset daniilsanets:004-2
--comment Add foreign key to user table
ALTER TABLE user_badge
    ADD CONSTRAINT fk_user_badge_user_uid
        FOREIGN KEY (user_uid)
            REFERENCES users(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE user_badge DROP CONSTRAINT fk_user_badge_user_uid;

--changeset daniilsanets:004-3
--comment Add foreign key to badge table
ALTER TABLE user_badge
    ADD CONSTRAINT fk_user_badge_badge_uid
        FOREIGN KEY (badge_uid)
            REFERENCES badge(uid)
            ON DELETE CASCADE;
--rollback ALTER TABLE user_badge DROP CONSTRAINT fk_user_badge_badge_uid;
