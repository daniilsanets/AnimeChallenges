--liquibase formatted sql

--changeset daniilsanets:013-1 splitStatements:false endDelimiter:$$
--comment add check function to submission_media table
CREATE OR REPLACE FUNCTION check_submission_media_limit()
    RETURNS TRIGGER AS $$
BEGIN
    PERFORM 1 FROM submission WHERE uid = NEW.submission_uid FOR UPDATE;

    IF (
           SELECT COUNT(*)
           FROM submission_media
           WHERE submission_uid = NEW.submission_uid
       ) >= 5 THEN
        RAISE EXCEPTION 'Submission cannot have more than 5 media files';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION check_submission_media_limit();

--changeset daniilsanets:013-2
--comment add check trigger to submission_media table
CREATE TRIGGER check_submission_media_limit_trigger
BEFORE INSERT ON submission_media
FOR EACH ROW
EXECUTE FUNCTION check_submission_media_limit();
--rollback DROP TRIGGER check_submission_media_limit_trigger ON submission_media;