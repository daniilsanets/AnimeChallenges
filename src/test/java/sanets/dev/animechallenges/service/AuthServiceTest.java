package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import sanets.dev.animechallenges.dto.LoginResponseDto;
import sanets.dev.animechallenges.dto.SignUpRequestDto;
import sanets.dev.animechallenges.exception.UserNotFoundException;
import sanets.dev.animechallenges.mapper.AuthMapper;
import sanets.dev.animechallenges.model.RefreshToken;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    private AuthMapper authMapper;

    @Mock
    RefreshTokenService refreshTokenService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    AuthService authService;

    @Test
    void login_shouldThrowBadCredentials_whenPasswordIsIncorrect(){
        String inputUsername = "testUser";
        String inputPassword = "wrongPassword";
        String correctHashedPassword = "correctHashedPassword";

        User foundUser = User.builder()
                .username(inputUsername)
                .passwordHash(correctHashedPassword)
                .build();

        when(userRepository.findByUsername(inputUsername)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(inputPassword,correctHashedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(inputUsername, inputPassword));
    }

    @Test
    void login_shouldThrowUserNotFound_whenUserDoesNotExist(){
        String inputUsernameOrEmail = "wrongUsernameOrEmail";
        String inputPassword = "password";

        when(userRepository.findByUsername(inputUsernameOrEmail)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(inputUsernameOrEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(inputUsernameOrEmail, inputPassword));
    }

    @Test
    void login_shouldReturnDto_whenUserIsValid() {
        String inputUsernameOrEmail = "testUser";
        String inputPassword = "password";
        String correctHashedPassword = "correctHashedPassword";
        String expectedAccessToken = "fake-access-token-123";
        String expectedRefreshTokenString = UUID.randomUUID().toString();

        User user = User.builder()
                .username(inputUsernameOrEmail)
                .passwordHash(correctHashedPassword)
                .role(UserRole.USER)
                .build();

        RefreshToken mockRefreshToken = RefreshToken.builder()
                .token(expectedRefreshTokenString)
                .user(user)
                .build();

        when(userRepository.findByUsername(inputUsernameOrEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(inputPassword, correctHashedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(expectedAccessToken);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(mockRefreshToken);

        LoginResponseDto actualResponse = authService.login(inputUsernameOrEmail, inputPassword);

        assertEquals(expectedAccessToken, actualResponse.getAccessToken());
        assertEquals(expectedRefreshTokenString, actualResponse.getRefreshToken());
    }

    @Test
    void signup_shouldCallMapperAndSave_whenRequestIsValid() {
        // --- 1. Подготовка (Arrange) ---
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword123";

        // 1. Создаем DTO, который "придет" в сервис
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newUser");
        signUpRequestDto.setEmail("new@example.com");
        signUpRequestDto.setPassword(rawPassword);

        // 2. Создаем "фальшивого" User, которого "якобы" вернет маппер
        User userFromMapper = User.builder()
                .username("newUser")
                .email("new@example.com")
                .passwordHash(hashedPassword)
                .role(UserRole.USER)
                .build();

        // 3. Настраиваем моки
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);


        when(authMapper.signupDtoToUser(
                eq(signUpRequestDto),
                eq(hashedPassword),
                eq(UserRole.USER),
                any(OffsetDateTime.class)
        )).thenReturn(userFromMapper);

        authService.signup(signUpRequestDto);

        verify(userRepository).save(userFromMapper);
    }


}
