package sanets.dev.animechallenges.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String user, String message) {
        super(message + " " + user);
    }
}
