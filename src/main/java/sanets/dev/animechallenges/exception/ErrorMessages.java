package sanets.dev.animechallenges.exception;

public class ErrorMessages {
    private ErrorMessages(){}

    public static final String USER_NOT_FOUND_MSG = "User not found in database by id";
    public static final String INVALID_ACCESS_MSG = "Invalid access!";

    public static final String WRONG_PASSWORD_MSG = "Wrong password!";
    public static final String USERNAME_ALREADY_EXISTS_MSG = "Username already exists in DB!";
    public static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists in DB!";

    public static final String MEDIA_NOT_FOUND_MSG = "Media not found";
    public static final String MEDIA_NOT_UPLOADED_TO_SERVER_MSG = "Media not uploaded to server storage";
    public static final String MEDIA_NOT_DELETED_FROM_SERVER_MSG = "Media not deleted from server storage";
    public static final String DIRECTORY_NOT_CREATED_MSG = "Could not initialize storage location";

    public static final String QUEST_NOT_FOUND_MSG = "Quest not found in  database by id";


    public static final String REFRESH_TOKEN_NOT_FOUND_MSG = "Refresh token not found in DB!";
}
