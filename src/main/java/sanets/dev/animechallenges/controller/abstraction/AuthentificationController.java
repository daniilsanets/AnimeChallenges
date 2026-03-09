package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import sanets.dev.animechallenges.dto.auth.LoginRequestDto;
import sanets.dev.animechallenges.dto.auth.LoginResponseDto;
import sanets.dev.animechallenges.dto.auth.RefreshRequestDto;
import sanets.dev.animechallenges.dto.auth.SignUpRequestDto;
import sanets.dev.animechallenges.dto.auth.SignUpResponseDto;

@Tag(name = "Authentication", description = "User authentication and token management")
@RequestMapping("/api/v1/auth")
public interface AuthentificationController {
    @Operation(
            summary = "User registration",
            description = "Registrer a new user and return auth-tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                            schema = @Schema(implementation = SignUpResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            )
    })
    @PostMapping("/signup")
    ResponseEntity<SignUpResponseDto> signup(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    );

    @Operation(
            summary = "User login",
            description = "Authenticates user using username or email and password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully passed auth",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid Credentials"
            )
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto
    );

    @Operation(
            summary = "Refresh access token",
            description = "Provide ability to refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token was successfully refreshed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    @PostMapping("/refresh")
    ResponseEntity<String> refresh(
            @RequestBody @Valid RefreshRequestDto refreshRequestDto
    );
}
