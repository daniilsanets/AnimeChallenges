package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.UserMapper;
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

    @InjectMocks
    private UserService userService;

    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .uid(UUID.randomUUID())
                .username("danechka")
                .role(UserRole.ROLE_ADMIN)
                .build();

        targetUser = User.builder()
                .uid(UUID.randomUUID())
                .username("Almost danechka")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void updateUserProfileByUId_ShouldUpdate_WhenAccessValid() {
        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();

        UserProfileResponseDto expectedResponse = new UserProfileResponseDto();

        when(userRepository.findByUid(currentUser.getUid())).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        doNothing().when(userMapper).updateUserProfileFromDto(dto, currentUser);

        when(userMapper.toUserProfileResponseDto(currentUser)).thenReturn(expectedResponse);

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {

            UserProfileResponseDto result = userService.updateUserProfileByUId(currentUser.getUid(), dto);

            assertNotNull(result);

            verify(userRepository).save(currentUser);
            verify(userMapper).updateUserProfileFromDto(dto, currentUser);

            verify(userMapper).toUserProfileResponseDto(currentUser);

            securityUtilsMock.verify(() -> SecurityUtils.validateUserAccessByUsername(currentUser.getUsername()));
        }
    }

    @Test
    void updateUserProfileByUId_ShouldThrow_WhenSecurityUtilsThrows() {

        when(userRepository.findByUid(targetUser.getUid())).thenReturn(Optional.of(targetUser));

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(() -> SecurityUtils.validateUserAccessByUsername(targetUser.getUsername()))
                    .thenThrow(new InvalidAccessException("Access denied"));

            assertThrows(InvalidAccessException.class,
                    () -> userService.updateUserProfileByUId(targetUser.getUid(), new UpdateUserProfileRequestDto()));

            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void getUserProfileByUid_ShouldReturnDto_WhenUserExists() {
        when(userRepository.findByUid(targetUser.getUid())).thenReturn(Optional.of(targetUser));
        UserProfileResponseDto expectedDto = new UserProfileResponseDto();
        when(userMapper.toUserProfileResponseDto(targetUser)).thenReturn(expectedDto);

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
    void deleteUserByUid_ShouldDelete_WhenIsAdminAndUserExists() {
        UUID targetUid = targetUser.getUid();

        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {

            securityUtilsMock.when(SecurityUtils::isAdmin).thenReturn(true);

            when(userRepository.existsById(targetUid)).thenReturn(true);
            when(userRepository.findByUid(targetUid)).thenReturn(Optional.of(targetUser));

            userService.deleteUserByUid(targetUid);

            verify(userRepository).delete(targetUser);
            verify(userRepository).existsById(targetUid);
        }
    }

    @Test
    void deleteUserByUid_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        UUID targetUid = UUID.randomUUID();
        currentUser.setRole(UserRole.ROLE_ADMIN);

        when(userRepository.existsById(targetUid)).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserByUid(targetUid));

        verify(userRepository, never()).delete(any());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}