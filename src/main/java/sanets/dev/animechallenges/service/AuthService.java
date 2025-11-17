package sanets.dev.animechallenges.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
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

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthMapper authMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authMapper = authMapper;
    }

    public SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) {

        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            throw new UserAlreadyExistsException(USERNAME_ALREADY_EXISTS_MSG, signUpRequestDto.getUsername());
        }

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException(EMAIL_ALREADY_EXISTS_MSG,signUpRequestDto.getEmail());
        }
        
        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        UserRole roleToAssign = UserRole.USER;
        OffsetDateTime now = OffsetDateTime.now();

        User user = authMapper.signupDtoToUser(
                signUpRequestDto,
                hashedPassword,
                roleToAssign,
                now
        );

        userRepository.save(user);

        SignUpResponseDto signUpResponseDto = new SignUpResponseDto(
                jwtService.generateToken(user),
                refreshTokenService.createRefreshToken(user).getToken()
        );

        return signUpResponseDto;
    }

    public LoginResponseDto login(String usernameOrEmail, String password) throws UserNotFoundException, BadCredentialsException {

        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG, usernameOrEmail));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new WrongPasswordException(WRONG_PASSWORD_MSG, user.getUsername());
        }

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponseDto(accessToken, refreshToken.getToken());
    }

    public String refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(REFRESH_TOKEN_NOT_FOUND_MSG, requestRefreshToken));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return newAccessToken;
    }

}
