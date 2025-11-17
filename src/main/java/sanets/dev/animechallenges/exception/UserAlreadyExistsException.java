package sanets.dev.animechallenges.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message, String username) {
        super(message + " " + username);
    }
}
