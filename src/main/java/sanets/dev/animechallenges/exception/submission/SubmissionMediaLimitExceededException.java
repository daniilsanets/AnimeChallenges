package sanets.dev.animechallenges.exception.submission;

public class SubmissionMediaLimitExceededException extends RuntimeException {
  public SubmissionMediaLimitExceededException(String message) {
    super(message);
  }
}
