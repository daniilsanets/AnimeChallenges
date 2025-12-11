package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponceDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final String USER_NOT_FOUND_MSG = "User not found in database by id";
    private static final String INVALID_ACCESS_MSG = "Invalid access!";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    public User updateUserProfileByUId(UUID userToUpdateUid, UpdateUserProfileRequestDto userProfileRequestDto){
        validateUserAccess(userToUpdateUid);

        User userToUpdate = getUserByUidOrThrow(userToUpdateUid);
        userMapper.updateUserProfileFromDto(userProfileRequestDto, userToUpdate);

        return userRepository.save(userToUpdate);
    }

    public User updateAvatar(UUID userToUpdateUid, UUID avatarUid){
        validateUserAccess(userToUpdateUid);

        User userToUpdate = getUserByUidOrThrow(userToUpdateUid);
        Media avatar = mediaService.getMediaByUidOrThrow(avatarUid);

        userToUpdate.setAvatar(avatar);

        return userRepository.save(userToUpdate);
    }

    public UserProfileResponceDto getUserProfileByUid(UUID userUid) {
        User user = getUserByUidOrThrow(userUid);
        return userMapper.toUserProfileResponceDto(user);
    }

    public User getUserByUidOrThrow(UUID userUid) {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
    }

    public UUID getCurrentUserUid(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
        return currentUser.getUid();
    }

    //user mustn't make changes if it is not a creator or admin
    public void validateUserAccess(UUID creatorUid) {
        UUID currentUserUid = getCurrentUserUid();

        if (!currentUserUid.equals(creatorUid)) {
            User currentUser = getUserByUidOrThrow(currentUserUid);

            if (!currentUser.getRole().equals(UserRole.ADMIN)) {
                throw new InvalidAccessException(INVALID_ACCESS_MSG);
            }
        }
    }

    //delete user profile, or maybe ban I dunno I need it or don't
}
