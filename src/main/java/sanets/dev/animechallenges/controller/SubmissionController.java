package sanets.dev.animechallenges.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.service.SubmissionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping(value = "/" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDto createSubmission(@Valid @ModelAttribute CreateSubmissionRequestDto dto) {
        return submissionService.createSubmission(dto);
    }

    @PatchMapping("/admin/{uid}/approve")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public SubmissionResponseDto approveSubmission(@PathVariable UUID uid){
        return submissionService.approveSubmissionByUid(uid);
    }

    @PatchMapping("/admin/{uid}/reject")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public SubmissionResponseDto rejectSubmission(@PathVariable UUID uid){
        return submissionService.rejectSubmissionByUid(uid);
    }

    @GetMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public SubmissionResponseDto getSubmission(@PathVariable UUID uid){
        return submissionService.getSubmissionResponseDto(uid);
    }
}
