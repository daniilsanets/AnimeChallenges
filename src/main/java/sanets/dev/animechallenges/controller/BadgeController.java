package sanets.dev.animechallenges.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.badge.BadgeFilterDto;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.dto.badge.BadgeResponseDto;
import sanets.dev.animechallenges.service.BadgeService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/badges")
public class BadgeController {

    private BadgeService badgeService;

    //create badge
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBadge(
            @RequestPart @Valid BadgeRequestDto badgeRequestDto,
            @RequestPart MultipartFile file
    ) {
        badgeService.createBadge(badgeRequestDto, file);
    }

    //read
    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public void getBadge(@PathVariable("uid") UUID badgeUid){
        badgeService.getBadgeByUid(badgeUid);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<BadgeResponseDto> getBadges(
            @Valid BadgeFilterDto badgeFilterDto,
            @PageableDefault(size = 20) Pageable pageable
    ){
        return badgeService.getBadgesWithFilter(badgeFilterDto, pageable);
    }

    
}
