package sanets.dev.animechallenges.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;

import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;

public class SecurityUtils {

    private SecurityUtils() {}

    public static void validateUserAccessByUsername(String ownerUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin"));

        if (isAdmin) {
            return;
        }

        String currentUsername = authentication.getName();
        if (!currentUsername.equals(ownerUsername)) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin"));
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
