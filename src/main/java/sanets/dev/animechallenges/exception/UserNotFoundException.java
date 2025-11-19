package sanets.dev.animechallenges.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
      super(message);
  }
}
