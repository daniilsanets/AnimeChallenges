package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.USER_NOT_FOUND_MSG;
import static sanets.dev.animechallenges.security.SecurityUtils.validateUserAccess;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    public User updateUserProfileByUId(UUID userToUpdateUid, UpdateUserProfileRequestDto userProfileRequestDto){
        User userToUpdate = getUserByUid(userToUpdateUid);

        validateUserAccess(userToUpdate.getUsername());

        userMapper.updateUserProfileFromDto(userProfileRequestDto, userToUpdate);

        return userRepository.save(userToUpdate);
    }

    public User updateAvatar(UUID userToUpdateUid, UUID avatarUid){
        User userToUpdate = getUserByUid(userToUpdateUid);

        validateUserAccess(userToUpdate.getUsername());

        Media avatar = mediaService.getMediaByUid(avatarUid);

        userToUpdate.setAvatar(avatar);

        return userRepository.save(userToUpdate);
    }

    public UserProfileResponseDto getUserProfileByUid(UUID userUid) {
        User user = getUserByUid(userUid);
        return userMapper.toUserProfileResponceDto(user);
    }

    public User getUserByUid(UUID userUid) {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
    }

    public UUID getCurrentUserUid(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
        return currentUser.getUid();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
    }
    //delete user profile, or maybe ban I dunno I need it or don't
}
