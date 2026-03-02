package sanets.dev.animechallenges.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;

public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * This method checks for user ability to change the data state
     * by its ownership of source or its role.
     *
     * @param ownerUsername get from invocation point
     * @throws InvalidAccessException when source isn't allowed
     */
    public static void validateUserAccessByUsername(String ownerUsername) {
        if (isAdmin()) {
            return;
        }

        String currentUsername = getCurrentUsername();
        if (!currentUsername.equals(ownerUsername)) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }
    }

    /**
     * This method checks for user ability to change the data state
     * by its ownership of source or its role.
     *
     * @param userUid get from invocation point
     * @throws InvalidAccessException when source isn't allowed
     */
    public static void validateUserAccessByUserUid(UUID userUid) {
        if (isAdmin()) {
            return;
        }

        if (!getCurrentUserUid().equals(userUid)) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static UUID getCurrentUserUid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (authentication != null) {
            return userPrincipal.getUid();
        }
        throw new InvalidAccessException(INVALID_ACCESS_MSG);
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (authentication != null) {
            return userPrincipal.getUsername();
        }
        throw new InvalidAccessException(INVALID_ACCESS_MSG);
    }
}
