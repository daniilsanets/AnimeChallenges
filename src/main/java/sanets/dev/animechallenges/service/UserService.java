package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.USER_NOT_FOUND_MSG;
import static sanets.dev.animechallenges.security.SecurityUtils.isAdmin;
import static sanets.dev.animechallenges.security.SecurityUtils.validateUserAccessByUsername;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    public UserProfileResponseDto updateUserProfileByUId(UUID userToUpdateUid, UpdateUserProfileRequestDto userProfileRequestDto){
        User userToUpdate = getUserByUid(userToUpdateUid);

        validateUserAccessByUsername(userToUpdate.getUsername());

        userMapper.updateUserProfileFromDto(userProfileRequestDto, userToUpdate, mediaService);

        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toUserProfileResponseDto(updatedUser);
    }

    public UserProfileResponseDto getUserProfileByUid(UUID userUid) {
        User user = getUserByUid(userUid);
        return userMapper.toUserProfileResponseDto(user);
    }

    public void deleteUserByUid(UUID userUid) {

        if (!isAdmin()) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }

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
