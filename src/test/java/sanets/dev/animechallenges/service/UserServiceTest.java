package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponceDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .uid(UUID.randomUUID())
                .username("danechka")
                .role(UserRole.USER)
                .build();

        targetUser = User.builder()
                .uid(UUID.randomUUID())
                .username("Almost danechka")
                .role(UserRole.USER)
                .build();
    }

    private void mockSecurityContext(User user) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(user.getUsername());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }


    @Test
    void validateUserAccess_ShouldPass_WhenUserIsCreator() {
        mockSecurityContext(currentUser);

        assertDoesNotThrow(() -> userService.validateUserAccess(currentUser.getUid()));
    }

    @Test
    void validateUserAccess_ShouldPass_WhenUserIsAdmin() {
        User adminUser = User.builder()
                .uid(UUID.randomUUID())
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        mockSecurityContext(adminUser);

        when(userRepository.findByUid(adminUser.getUid())).thenReturn(Optional.of(adminUser));

        assertDoesNotThrow(() -> userService.validateUserAccess(targetUser.getUid()));
    }

    @Test
    void validateUserAccess_ShouldThrow_WhenUserIsNotCreatorAndNotAdmin() {
        mockSecurityContext(currentUser);

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));

        assertThrows(InvalidAccessException.class,
                () -> userService.validateUserAccess(targetUser.getUid()));
    }

    @Test
    void updateUserProfileByUId_ShouldUpdate_WhenAccessValid() {
        mockSecurityContext(currentUser);

        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        doNothing().when(userMapper).updateUserProfileFromDto(dto, currentUser);

        User result = userService.updateUserProfileByUId(currentUser.getUid(), dto);

        assertNotNull(result);
        verify(userRepository).save(currentUser);
        verify(userMapper).updateUserProfileFromDto(dto, currentUser);
    }

    @Test
    void updateAvatar_ShouldUpdateAvatar_WhenAccessValid() {
        mockSecurityContext(currentUser);

        UUID avatarUid = UUID.randomUUID();
        Media media = Media.builder()
                .uid(avatarUid)
                .build();

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));
        when(mediaService.getMediaByUidOrThrow(avatarUid)).thenReturn(media);
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        User result = userService.updateAvatar(currentUser.getUid(), avatarUid);

        assertEquals(media, result.getAvatar());
        verify(userRepository).save(currentUser);
    }

    @Test
    void getUserProfileByUid_ShouldReturnDto_WhenUserExists() {
        when(userRepository.findByUid(targetUser.getUid())).thenReturn(Optional.of(targetUser));
        UserProfileResponceDto expectedDto = new UserProfileResponceDto();
        when(userMapper.toUserProfileResponceDto(targetUser)).thenReturn(expectedDto);

        UserProfileResponceDto result = userService.getUserProfileByUid(targetUser.getUid());

        assertEquals(expectedDto, result);
    }

    @Test
    void getUserByUidOrThrow_ShouldThrow_WhenUserNotFound() {
        UUID unknownUid = UUID.randomUUID();

        when(userRepository.findByUid(unknownUid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUidOrThrow(unknownUid));
    }

    @Test
    void getCurrentUserUid_ShouldThrow_WhenAuthUserNotInDb() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("Not Danechka");

        when(userRepository.findByUsername("Not Danechka")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUserUid());
    }
}