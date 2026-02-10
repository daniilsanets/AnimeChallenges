package sanets.dev.animechallenges.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.auth.LoginRequestDto;
import sanets.dev.animechallenges.dto.auth.LoginResponseDto;
import sanets.dev.animechallenges.dto.auth.RefreshRequestDto;
import sanets.dev.animechallenges.dto.auth.SignUpRequestDto;
import sanets.dev.animechallenges.dto.auth.SignUpResponseDto;
import sanets.dev.animechallenges.service.security.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        SignUpResponseDto signUpResponseDto = authService.signup(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto
    ){
        LoginResponseDto loginResponseDto = authService.login(
                    loginRequestDto.getUsernameOrEmail(),
                    loginRequestDto.getPassword()
            );
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @RequestBody @Valid RefreshRequestDto refreshRequestDto
    ){
        String newAccessToken = authService.refreshToken(refreshRequestDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

}

