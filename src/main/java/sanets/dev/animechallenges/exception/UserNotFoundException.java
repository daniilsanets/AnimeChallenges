package sanets.dev.animechallenges.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String username) {
      super(String.format("User not found with provided credentials. Username Or Email [%s]", username));
  }
}
