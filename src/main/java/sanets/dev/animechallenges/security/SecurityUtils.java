package sanets.dev.animechallenges.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;

import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;

public class SecurityUtils {

    private SecurityUtils() {}

    public static void validateUserAccess(String ownerUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return;
        }

        String currentUsername = authentication.getName();
        if (!currentUsername.equals(ownerUsername)) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }
    }
}
