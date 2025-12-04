package sanets.dev.animechallenges.exception.auth;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
      super(message);
  }
}
