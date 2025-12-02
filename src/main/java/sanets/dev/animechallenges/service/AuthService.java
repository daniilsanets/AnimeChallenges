package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.LoginResponseDto;
import sanets.dev.animechallenges.dto.SignUpRequestDto;
import sanets.dev.animechallenges.dto.SignUpResponseDto;
import sanets.dev.animechallenges.exception.TokenRefreshException;
import sanets.dev.animechallenges.exception.UserAlreadyExistsException;
import sanets.dev.animechallenges.exception.UserNotFoundException;
import sanets.dev.animechallenges.exception.WrongPasswordException;
import sanets.dev.animechallenges.mapper.AuthMapper;
import sanets.dev.animechallenges.model.RefreshToken;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_TOKEN_NOT_FOUND_MSG = "Refresh token not found in DB!";
    private static final String USER_NOT_FOUND_MSG = "User not found in DB!";
    private static final String WRONG_PASSWORD_MSG = "Wrong password!";
    private static final String USERNAME_ALREADY_EXISTS_MSG = "Username already exists in DB!";
    private static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists in DB!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper authMapper;

    public SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) {
        log.debug("Trying to check unique username in signup");
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            String message = USERNAME_ALREADY_EXISTS_MSG + " " +  signUpRequestDto.getUsername();
            log.error("The user entered not unique username: {}", message);
            throw new UserAlreadyExistsException(message);
        }

        log.debug("Trying to check unique user email in signup");
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            String message = EMAIL_ALREADY_EXISTS_MSG + " " +  signUpRequestDto.getEmail();
            log.error("The user entered not unique email: {}", message);
            throw new UserAlreadyExistsException(message);
        }
        
        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        UserRole roleToAssign = UserRole.USER;
        OffsetDateTime now = OffsetDateTime.now();

        log.debug("Map signupDto to user: {}", signUpRequestDto.getUsername());
        User user = authMapper.signupDtoToUser(
                signUpRequestDto,
                hashedPassword,
                roleToAssign,
                now
        );

        log.debug("Will save user {} in db", user.getUid());
        userRepository.save(user);
        log.info("User {} saved in db",  user.getUid());

        return  new SignUpResponseDto(
                jwtService.generateToken(user),
                refreshTokenService.createRefreshToken(user).getToken());
    }

    public LoginResponseDto login(String usernameOrEmail, String password) throws UserNotFoundException, BadCredentialsException {
        String message = USER_NOT_FOUND_MSG + " " +  usernameOrEmail;

        log.debug("Looking for user in db");
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException(message));
        log.info("User {} found in db", user.getUid());

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.error("User {} password does not match", user.getUid());
            String messageWrongPassword = WRONG_PASSWORD_MSG + user.getUsername();
            throw new WrongPasswordException(messageWrongPassword);
        }

        log.debug("Create tokens for user {}", user.getUid());
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponseDto(accessToken, refreshToken.getToken());
    }

    public String refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(REFRESH_TOKEN_NOT_FOUND_MSG));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return newAccessToken;
    }

}
