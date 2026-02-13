package sanets.dev.animechallenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.badge.BadgeFilterDto;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.dto.badge.BadgeResponseDto;
import sanets.dev.animechallenges.model.badge.Badge;
import sanets.dev.animechallenges.service.BadgeService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/badges")
public class BadgeController {

    private BadgeService badgeService;

    @GetMapping
    public Page<BadgeResponseDto> getBadges(BadgeFilterDto filter, Pageable pageable) {
        return badgeService.getBadgesWithFilter(filter, pageable);
    }

    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Badge getBadgeByUid(@PathVariable UUID uid) {
        return badgeService.getBadgeByUid(uid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBadge(@RequestPart("badge") BadgeRequestDto badgeRequestDto,
                            @RequestPart("file") MultipartFile file) {
        badgeService.createBadge(badgeRequestDto, file);
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBadge(@PathVariable UUID uid) {
        badgeService.deleteBadge(uid);
    }

    @GetMapping("/user/{userId}")
    public List<BadgeResponseDto> getUserBadges(@PathVariable UUID userId) {
        return badgeService.getUserBadgesByUserUid(userId);
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public boolean assignBadgeToUser(@RequestParam UUID userUid, @RequestParam UUID badgeUid) {
        Badge badge = badgeService.getBadgeByUid(badgeUid);
        return badgeService.saveBadgeToUser(userUid, badge);
    }
}
