package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.user.AdminUserProfileResponseDto;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.UserRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.USER_NOT_FOUND_MSG;
import static sanets.dev.animechallenges.security.SecurityUtils.validateUserAccessByUsername;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    /**
     * Updated user profile
     * @param userProfileRequestDto
     * @return <code>UserProfileResponseDto</code>
     */
    public UserProfileResponseDto updateUserProfileByUId(UpdateUserProfileRequestDto userProfileRequestDto){
        User userToUpdate = getUserByUid(SecurityUtils.getCurrentUserUid());

        userMapper.updateUserProfileFromDto(userProfileRequestDto, userToUpdate);

        if (userProfileRequestDto.getAvatarUid() != null) {
            Media avatar = mediaService.getMediaByUid(userProfileRequestDto.getAvatarUid());
            userToUpdate.setAvatar(avatar);
        }

        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toUserProfileResponseDto(updatedUser);
    }

    public UserProfileResponseDto getUserProfileByUid(UUID userUid) {
        User user = getUserByUid(userUid);
        return userMapper.toUserProfileResponseDto(user);
    }

    /// Only for admins
    public AdminUserProfileResponseDto getExtendedUserProfileByUid(UUID userUid) {
        return userMapper.toAdminUserProfileResponseDto(getUserByUid(userUid));
    }

    public void deleteUserByUid(UUID userUid) {

        if(!userRepository.existsById(userUid)){
            throw new UserNotFoundException(USER_NOT_FOUND_MSG);
        }

        userRepository.delete(getUserByUid(userUid));
    }

    public User getUserByUid(UUID userUid) {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));
    }
}
