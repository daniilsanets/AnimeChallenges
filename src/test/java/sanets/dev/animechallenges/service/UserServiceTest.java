package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.UserMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

    private void mockSecurityContext(String username) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn(username);
    }

    @Test
    void updateUserProfileByUId_ShouldUpdate_WhenAccessValid() {
        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        doNothing().when(userMapper).updateUserProfileFromDto(dto, currentUser);

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {

            User result = userService.updateUserProfileByUId(currentUser.getUid(), dto);

            assertNotNull(result);
            verify(userRepository).save(currentUser);
            verify(userMapper).updateUserProfileFromDto(dto, currentUser);

            securityUtilsMock.verify(() -> SecurityUtils.validateUserAccess(currentUser.getUsername()));
        }
    }

    @Test
    void updateUserProfileByUId_ShouldThrow_WhenSecurityUtilsThrows() {

        when(userRepository.findByUid(targetUser.getUid())).thenReturn(Optional.of(targetUser));

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(() -> SecurityUtils.validateUserAccess(targetUser.getUsername()))
                    .thenThrow(new InvalidAccessException("Access denied"));

            assertThrows(InvalidAccessException.class,
                    () -> userService.updateUserProfileByUId(targetUser.getUid(), new UpdateUserProfileRequestDto()));

            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void updateAvatar_ShouldUpdateAvatar_WhenAccessValid() {
        UUID avatarUid = UUID.randomUUID();
        Media media = Media.builder().uid(avatarUid).build();

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));
        when(mediaService.getMediaByUid(avatarUid)).thenReturn(media);
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {

            User result = userService.updateAvatar(currentUser.getUid(), avatarUid);

            assertEquals(media, result.getAvatar());
            verify(userRepository).save(currentUser);

            securityUtilsMock.verify(() -> SecurityUtils.validateUserAccess(currentUser.getUsername()));
        }
    }

    @Test
    void getUserProfileByUid_ShouldReturnDto_WhenUserExists() {
        // Обычный тест без статики
        when(userRepository.findByUid(targetUser.getUid())).thenReturn(Optional.of(targetUser));
        UserProfileResponseDto expectedDto = new UserProfileResponseDto();
        when(userMapper.toUserProfileResponceDto(targetUser)).thenReturn(expectedDto);

        UserProfileResponseDto result = userService.getUserProfileByUid(targetUser.getUid());

        assertEquals(expectedDto, result);
    }

    @Test
    void getUserByUid_ShouldThrow_WhenUserNotFound() {
        UUID unknownUid = UUID.randomUUID();
        when(userRepository.findByUid(unknownUid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUid(unknownUid));
    }

    @Test
    void getCurrentUserUid_ShouldReturnUid_WhenUserExists() {
        mockSecurityContext("danechka");
        when(userRepository.findByUsername("danechka")).thenReturn(Optional.of(currentUser));

        UUID result = userService.getCurrentUserUid();

        assertEquals(currentUser.getUid(), result);
    }
}