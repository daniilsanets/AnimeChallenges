package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sanets.dev.animechallenges.dto.auth.LoginResponseDto;
import sanets.dev.animechallenges.dto.auth.SignUpRequestDto;
import sanets.dev.animechallenges.dto.auth.SignUpResponseDto;
import sanets.dev.animechallenges.exception.auth.UserAlreadyExistsException;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.exception.auth.WrongPasswordException;
import sanets.dev.animechallenges.mapper.AuthMapper;
import sanets.dev.animechallenges.model.security.RefreshToken;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.model.user.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;
import sanets.dev.animechallenges.service.security.AuthService;
import sanets.dev.animechallenges.service.security.JwtService;
import sanets.dev.animechallenges.service.security.RefreshTokenService;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

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
    void login_shouldThrowWrongPasswordException_whenPasswordIsIncorrect(){
        String inputUsername = "testUser";
        String inputPassword = "wrongPassword";
        String correctHashedPassword = "correctHashedPassword";

        User foundUser = User.builder()
                .username(inputUsername)
                .passwordHash(correctHashedPassword)
                .build();

        when(userRepository.findByUsername(inputUsername)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(inputPassword,correctHashedPassword)).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> authService.login(inputUsername, inputPassword));
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
                .role(UserRole.ROLE_USER)
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
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword123";
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("test-refresh-token");

        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newUser");
        signUpRequestDto.setEmail("new@example.com");
        signUpRequestDto.setPassword(rawPassword);

        User userFromMapper = User.builder()
                .username("newUser")
                .email("new@example.com")
                .passwordHash(hashedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);


        when(authMapper.signupDtoToUser(
                eq(signUpRequestDto),
                eq(hashedPassword),
                eq(UserRole.ROLE_USER),
                any(OffsetDateTime.class)
        )).thenReturn(userFromMapper);

        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(mockRefreshToken);

        authService.signup(signUpRequestDto);

        verify(userRepository).save(userFromMapper);
    }

    @Test
    void signup_shouldReturnTokens_whenUserSignUp(){
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newUser");
        signUpRequestDto.setEmail("someEmail@mail.com");
        signUpRequestDto.setPassword("password123");

        String hashedPassword = "hashedPassword123";
        String expectedAccessToken = "some-random-super-crypto-token";
        String expectedRefreshToken = "some-random-super-crypto-refresh-token";

        User mockUser = new User();
        mockUser.setUsername("newUser");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken(expectedRefreshToken);

        when(authMapper.signupDtoToUser(
                any(SignUpRequestDto.class),
                eq(hashedPassword),
                any(UserRole.class),
                any(OffsetDateTime.class)
        )).thenReturn(mockUser);

        when(passwordEncoder.encode("password123")).thenReturn(hashedPassword);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(refreshTokenService.createRefreshToken(mockUser)).thenReturn(mockRefreshToken);
        when(jwtService.generateToken(mockUser)).thenReturn(expectedAccessToken);

        SignUpResponseDto actual = authService.signup(signUpRequestDto);

        assertNotNull(actual);
        assertEquals(expectedAccessToken, actual.getAccessToken());
        assertEquals(expectedRefreshToken, actual.getRefreshToken());

        verify(passwordEncoder).encode("password123");
        verify(authMapper).signupDtoToUser(any(), eq(hashedPassword), any(), any());
        verify(userRepository).save(mockUser);
        verify(jwtService).generateToken(mockUser);
        verify(refreshTokenService).createRefreshToken(mockUser);
    }

    @Test
    void signup_shouldThrowUserAlreadyExistsException_whenDBHasThisNickname(){
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newUser");

        when(userRepository.existsByUsername(signUpRequestDto.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(signUpRequestDto));
    }

    @Test
    void signup_shouldThrowUserAlreadyExistsException_whenDBHasThisEmail(){
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newUser");
        signUpRequestDto.setEmail("someEmail@mail.com");

        when(userRepository.existsByUsername(signUpRequestDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequestDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(signUpRequestDto));
    }
}
