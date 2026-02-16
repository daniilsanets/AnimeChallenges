package sanets.dev.animechallenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.user.AdminUserProfileResponseDto;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.service.UserService;

import java.util.UUID;

import static sanets.dev.animechallenges.security.SecurityUtils.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponseDto getCurrentUserProfile() {
        return userService.getUserProfileByUid(getCurrentUserUid());
    }

    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponseDto getUserProfileByUid(@PathVariable UUID uid) {
        return userService.getUserProfileByUid(uid);
    }

    @GetMapping("/admin/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserProfileResponseDto getAdminUserProfileByUid(@PathVariable UUID uid) {
        return userService.getExtendedUserProfileByUid(uid);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponseDto updateProfile(@RequestBody UpdateUserProfileRequestDto updateDto){
        return userService.updateUserProfileByUId(updateDto);
    }

    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID uid){
        userService.deleteUserByUid(uid);
    }
}
