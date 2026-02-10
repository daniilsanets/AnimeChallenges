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
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.model.user.UserRole;
import sanets.dev.animechallenges.security.SecurityUtils;
import sanets.dev.animechallenges.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public UserProfileResponseDto getUserProfile() {
        return userService.getUserProfileByUid(SecurityUtils.getCurrentUserUid());
    }

    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponseDto getUserProfile(@PathVariable UUID uid) {
        return userService.getUserProfileByUid(uid);
    }

    @PatchMapping()
    public UserProfileResponseDto updateProfile(@RequestBody UpdateUserProfileRequestDto updateDto){
        return userService.updateUserProfileByUId(SecurityUtils.getCurrentUserUid(), updateDto);
    }

    ///todo in the future realises
//    @DeleteMapping()
//    @PreAuthorize("hasRole()")
//    public void deleteUserProfile(@PathVariable UUID uid){
//
//    }
}
