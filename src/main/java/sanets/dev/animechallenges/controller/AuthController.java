package sanets.dev.animechallenges.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.LoginRequestDto;
import sanets.dev.animechallenges.dto.LoginResponseDto;
import sanets.dev.animechallenges.dto.RefreshRequestDto;
import sanets.dev.animechallenges.dto.SignUpRequestDto;
import sanets.dev.animechallenges.dto.SignUpResponseDto;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(
            @RequestBody SignUpRequestDto signUpRequestDto
    ) {
        log.info("Signup request received by user: {}", signUpRequestDto.getUsername());
        SignUpResponseDto signUpResponseDto = authService.signup(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto
    ){
        log.info("User send data to login", loginRequestDto.getUsernameOrEmail());
        log.debug("Receive loginRequestDto: {}", loginRequestDto.getUsernameOrEmail());
        LoginResponseDto loginResponseDto = authService.login(
                    loginRequestDto.getUsernameOrEmail(),
                    loginRequestDto.getPassword()
            );
        log.debug("Send loginResponseDto to user: {}", loginRequestDto.getUsernameOrEmail());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @RequestBody RefreshRequestDto refreshRequestDto
    ){
        log.debug("Receive refreshRequestDto");
        String newAccessToken = authService.refreshToken(refreshRequestDto.getRefreshToken());
        log.debug("Send new access token");
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

}

