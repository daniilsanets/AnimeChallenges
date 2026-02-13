package sanets.dev.animechallenges.exception;

public class ErrorMessages {
    private ErrorMessages(){}

    public static final String USER_NOT_FOUND_MSG = "User not found in database by id";
    public static final String INVALID_ACCESS_MSG = "Invalid access!";

    public static final String WRONG_PASSWORD_MSG = "Wrong password!";
    public static final String USERNAME_ALREADY_EXISTS_MSG = "Username already exists in DB!";
    public static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists in DB!";

    public static final String BADGE_NOT_FOUND_MSG = "Badge not found";
    public static final String BADGE_NOT_SAVED_MSG = "Failed to save badge";

    public static final String MEDIA_NOT_FOUND_MSG = "Media not found";
    public static final String MEDIA_NOT_UPLOADED_TO_SERVER_MSG = "Media not uploaded to server storage";
    public static final String MEDIA_NOT_DELETED_FROM_SERVER_MSG = "Media not deleted from server storage";
    public static final String DIRECTORY_NOT_CREATED_MSG = "Could not initialize storage location";

    public static final String QUEST_NOT_FOUND_MSG = "Quest not found in  database by id";
    public static final String QUEST_NOT_AVAILABLE_MSG = "Quest not available in DB!";

    public static final String REFRESH_TOKEN_NOT_FOUND_MSG = "Refresh token not found in DB!";

    public static final String QUEST_PARTICIPATION_NOT_FOUND_MSG = "Quest participation not found in DB!";

    public static final String ALREADY_PARTICIPATING_MSG = "You are already in :)";

    public static final String SUBMISSION_NOT_FOUND_MSG = "Submission not found in database by id";
    public static final String SUBMISSION_NOT_AVAILABLE_MSG = "Submission not available";
    public static final String SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG = "Maximum media limit exceeded. Allowed: ";

    public static final String INVALID_MEDIA_CASTING_MSG = "Invalid media type casting";
}
