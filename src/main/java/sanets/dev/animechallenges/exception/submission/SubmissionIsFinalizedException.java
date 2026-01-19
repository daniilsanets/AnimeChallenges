package sanets.dev.animechallenges.exception.submission;

public class SubmissionIsFinalized extends RuntimeException {
    public SubmissionIsFinalized(String message) {
        super(message);
    }
}
