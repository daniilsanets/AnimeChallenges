package sanets.dev.animechallenges.controller;

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
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody() SignUpRequestDto signUpRequestDto
    ) {
        authService.signup(signUpRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("You are with us :)");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody() LoginRequestDto loginRequestDto
    ){
        LoginResponseDto loginResponseDto;

                loginResponseDto = authService.login(
                    loginRequestDto.getUsernameOrEmail(),
                    loginRequestDto.getPassword()
            );

        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody() RefreshRequestDto refreshRequestDto
    ){

        String newAccessToken = authService.refreshToken(refreshRequestDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

}

