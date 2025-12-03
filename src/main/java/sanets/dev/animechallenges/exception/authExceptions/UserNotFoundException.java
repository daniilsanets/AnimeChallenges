package sanets.dev.animechallenges.exception.authExceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
      super(message);
  }
}
