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
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    AuthService authService;

    @Captor
    ArgumentCaptor<User> userCaptor;

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

        assertThrows(UsernameNotFoundException.class, () -> authService.login(inputUsernameOrEmail, inputPassword));
    }

    @Test
    void login_shouldReturnToken_whenUserIsValid(){
        String inputUsernameOrEmail = "usernameOrEmail";
        String inputPassword = "password";
        String correctHashedPassword = "correctHashedPassword";
        String expectedToken = "fake-jwt-token-string";

        User user = User.builder()
                .username(inputUsernameOrEmail)
                .passwordHash(correctHashedPassword)
                .build();

        when(userRepository.findByUsername(inputUsernameOrEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(inputPassword,correctHashedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(expectedToken);

        String actual = authService.login(inputUsernameOrEmail, inputPassword);

        assertEquals(expectedToken, actual);
    }

    @Test
    void signup_shouldSaveUser_whenUserIsValid() {
        String username = "newUser";
        String email = "new@example.com";
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword123";

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        authService.signup(username, rawPassword, email, UserRole.USER);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals(username, capturedUser.getUsername());
        assertEquals(email, capturedUser.getEmail());
        assertEquals(hashedPassword, capturedUser.getPasswordHash());
    }

}
