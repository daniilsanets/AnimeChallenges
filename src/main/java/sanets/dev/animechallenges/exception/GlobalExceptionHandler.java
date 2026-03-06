package sanets.dev.animechallenges.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sanets.dev.animechallenges.exception.auth.TokenRefreshException;
import sanets.dev.animechallenges.exception.auth.UserAlreadyExistsException;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.auth.WrongPasswordException;
import sanets.dev.animechallenges.exception.badge.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.exception.media.MediaNotDeletedException;
import sanets.dev.animechallenges.exception.media.MediaNotFoundException;
import sanets.dev.animechallenges.exception.media.MediaNotUploadedException;
import sanets.dev.animechallenges.exception.participation.AlreadyParticipatingException;
import sanets.dev.animechallenges.exception.participation.ParticipationNotFoundException;
import sanets.dev.animechallenges.exception.quest.QuestNotAvailable;
import sanets.dev.animechallenges.exception.quest.QuestNotCreatedException;
import sanets.dev.animechallenges.exception.quest.QuestNotDeletedException;
import sanets.dev.animechallenges.exception.quest.QuestNotFoundException;
import sanets.dev.animechallenges.exception.quest.QuestNotUpdatedException;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.exception.submission.SubmissionMediaLimitExceededException;
import sanets.dev.animechallenges.exception.submission.SubmissionNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiError> handleBadCredentials(WrongPasswordException ex,
                                                         HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler({UserNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleUserNotFound(RuntimeException ex,
                                                       HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiError> handleTokenRefresh(TokenRefreshException ex,
                                                       HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<ApiError> handleInvalidAccess(InvalidAccessException ex,
                                                        HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(BadgeNotFoundException.class)
    public ResponseEntity<ApiError> handleBadgeNotFound(BadgeNotFoundException ex,
                                                        HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<ApiError> handleMediaNotFound(MediaNotFoundException ex,
                                                        HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MediaNotUploadedException.class)
    public ResponseEntity<ApiError> handleMediaNotUploaded(MediaNotUploadedException ex,
                                                           HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(MediaNotDeletedException.class)
    public ResponseEntity<ApiError> handleMediaNotDeleted(MediaNotDeletedException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(QuestNotFoundException.class)
    public ResponseEntity<ApiError> handleQuestNotFound(QuestNotFoundException ex,
                                                        HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(QuestNotCreatedException.class)
    public ResponseEntity<ApiError> handleQuestNotCreated(QuestNotCreatedException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(QuestNotDeletedException.class)
    public ResponseEntity<ApiError> handleQuestNotDeleted(QuestNotDeletedException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(QuestNotUpdatedException.class)
    public ResponseEntity<ApiError> handleQuestNotUpdated(QuestNotUpdatedException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(QuestNotAvailable.class)
    public ResponseEntity<ApiError> handleQuestNotAvailable(QuestNotAvailable ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ParticipationNotFoundException.class)
    public ResponseEntity<ApiError> handleParticipationNotFound(ParticipationNotFoundException ex,
                                                                HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(AlreadyParticipatingException.class)
    public ResponseEntity<ApiError> handleAlreadyParticipating(AlreadyParticipatingException ex,
                                                               HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(SubmissionNotFoundException.class)
    public ResponseEntity<ApiError> handleSubmissionNotFound(SubmissionNotFoundException ex,
                                                             HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(SubmissionIsFinalizedException.class)
    public ResponseEntity<ApiError> handleSubmissionIsFinalized(SubmissionIsFinalizedException ex,
                                                                HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(SubmissionMediaLimitExceededException.class)
    public ResponseEntity<ApiError> handleSubmissionMediaLimitExceeded(SubmissionMediaLimitExceededException ex,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation error");
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "'";
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex,
                                              HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }
}