package sanets.dev.animechallenges.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message, String username) {
      super(message + " " + username);
  }
}
