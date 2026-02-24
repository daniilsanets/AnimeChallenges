package sanets.dev.animechallenges.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.dto.quest.QuestFilterDto;
import sanets.dev.animechallenges.dto.quest.QuestResponseDto;
import sanets.dev.animechallenges.dto.quest.UpdateQuestRequestDto;
import sanets.dev.animechallenges.service.QuestService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuest(@RequestBody @Valid CreateQuestRequestDto dto) {
        questService.createQuest(dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<QuestResponseDto> getQuests(
            QuestFilterDto filterDto,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return questService.getQuestsWithFilter(filterDto, pageable);
    }

    @PatchMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public void updateQuest(
            @PathVariable UUID uid,
            @RequestBody @Valid UpdateQuestRequestDto dto
    ) {
        questService.updateQuest(uid, dto);
    }

    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuest(@PathVariable UUID uid) {
        questService.deleteQuest(uid);
    }
}
