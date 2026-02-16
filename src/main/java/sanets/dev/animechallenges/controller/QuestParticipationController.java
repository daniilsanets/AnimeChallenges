package sanets.dev.animechallenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.participation.CreateParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
import sanets.dev.animechallenges.service.QuestParticipationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/participations")
@RequiredArgsConstructor
public class QuestParticipationController {
    private final QuestParticipationService questParticipationService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestParticipationResponseDto createParticipation(@RequestBody() CreateParticipationRequestDto createParticipationRequestDto) {
        return questParticipationService.createQuestParticipation(createParticipationRequestDto);
    }

    @GetMapping("/{uid}")
    public QuestParticipationResponseDto getParticipationByUid(@PathVariable UUID uid) {
        return questParticipationService.getQuestParticipationResponseByUid(uid);
    }

    @GetMapping("/admin/users/{userUid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<QuestParticipationResponseDto> getAllParticipations(
            @PathVariable UUID userUid,
            @PageableDefault(20) Pageable pageable) {
        return questParticipationService.getAllQuestParticipationByUserUid(userUid, pageable);
    }

    @PatchMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public QuestParticipationResponseDto updateQuestParticipationByUid(
            @PathVariable UUID uid,
            @RequestBody UpdateQuestParticipationRequestDto dto) {
        return questParticipationService.updateQuestParticipation(uid, dto);
    }

    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void cancelParticipationByUid(@PathVariable UUID uid) {
        questParticipationService.cancelParticipationByUid(uid);
    }

}
