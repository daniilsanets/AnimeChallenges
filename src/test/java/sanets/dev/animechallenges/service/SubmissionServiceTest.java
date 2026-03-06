package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.submission.Submission;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private SubmissionMediaRepository submissionMediaRepository;
    @Mock
    private QuestParticipationService questParticipationService;
    @Mock
    private SubmissionMapper submissionMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SubmissionMediaService submissionMediaService;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private SubmissionService submissionService;

    private QuestParticipation questParticipation;
    private User user;
    private Submission submission;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("danechka");

        questParticipation = QuestParticipation.builder()
                .uid(UUID.randomUUID())
                .performer(user)
                .build();

        submission = Submission.builder()
                .uid(UUID.randomUUID())
                .submissionStatus(SubmissionStatus.PENDING)
                .questParticipation(questParticipation)
                .build();

    }

    @Test
    void createSubmission_shouldCreateSubmissionAndReturnDto() {
        UUID participationUid = questParticipation.getUid();

        CreateSubmissionRequestDto dto = new CreateSubmissionRequestDto();
        dto.setParticipationUid(participationUid);
        dto.setMultipartFiles(List.of());
        dto.setDescription("Cool description");
        dto.setNotes("Cool notes");

        SubmissionResponseDto responseDto = new SubmissionResponseDto();
        responseDto.setUid(submission.getUid());

        when(questParticipationService.getQuestParticipationByUid(participationUid))
                .thenReturn(questParticipation);

        when(submissionMapper.toSubmission(dto, questParticipation))
                .thenReturn(submission);

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(responseDto);

        SubmissionResponseDto result = submissionService.createSubmission(dto);

        assertNotNull(result);
        assertEquals(submission.getUid(), result.getUid());

        verify(submissionRepository).save(submission);
        verify(submissionMapper).toSubmission(dto, questParticipation);
        verify(submissionMapper).toSubmissionResponseDto(submission);
    }

    @Test
    void createSubmission_withMedia_shouldSaveSubmissionAndMedia() {
        UUID participationUid = questParticipation.getUid();

        MockMultipartFile file1 = new MockMultipartFile(
                "file", "file1.png", "image/png", "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "file2.jpg", "image/jpeg", "content2".getBytes()
        );

        CreateSubmissionRequestDto dto = new CreateSubmissionRequestDto();
        dto.setParticipationUid(participationUid);
        dto.setDescription("desc");
        dto.setNotes("notes");
        dto.setMultipartFiles(List.of(file1, file2));

        SubmissionResponseDto responseDto = new SubmissionResponseDto();
        responseDto.setUid(submission.getUid());

        Media media1 = new Media();
        Media media2 = new Media();

        when(questParticipationService.getQuestParticipationByUid(participationUid))
                .thenReturn(questParticipation);
        when(submissionMapper.toSubmission(dto, questParticipation))
                .thenReturn(submission);
        when(submissionRepository.save(submission))
                .thenReturn(submission);
        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(responseDto);

        when(mediaService.upload(file1)).thenReturn(media1);
        when(mediaService.upload(file2)).thenReturn(media2);

        SubmissionResponseDto result = submissionService.createSubmission(dto);

        assertNotNull(result);
        assertEquals(submission.getUid(), result.getUid());

        verify(submissionRepository).save(submission);
        verify(submissionMediaService).attachToSubmission(
                eq(submission),
                argThat(list -> list.size() == 2 && list.containsAll(List.of(media1, media2)))
        );
    }

    @Test
    void rejectSubmission_shouldSetRejectAndReturnDto_whenItsSuccessfully(){
        submission.setSubmissionStatus(SubmissionStatus.PENDING);

        when(submissionRepository.findByUid(submission.getUid()))
                .thenReturn(Optional.of(submission));

        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(new SubmissionResponseDto());

        submissionService.rejectSubmissionByUid(submission.getUid());

        assertEquals(SubmissionStatus.REJECTED, submission.getSubmissionStatus());
        assertNotNull(submission.getRejectedAt());

        verify(submissionRepository).saveAndFlush(submission);
        verify(submissionMapper).toSubmissionResponseDto(submission);
    }

    @Test
    void rejectSubmission_shouldThrowSubmissionIsFinalizedException_whenSubmissionStatusIsFinalized(){
        submission.setSubmissionStatus(SubmissionStatus.APPROVED);

        when(submissionRepository.findByUid(submission.getUid()))
                .thenReturn(Optional.of(submission));

        assertThrows(
                SubmissionIsFinalizedException.class,
                () -> submissionService.rejectSubmissionByUid(submission.getUid())
        );

        verify(submissionRepository, never()).saveAndFlush(any());
    }

    @Test
    void approvedSubmission_shouldSetApprovedAndReturnDto_whenItsSuccessfully() {
        submission.setSubmissionStatus(SubmissionStatus.PENDING);

        when(submissionRepository.findByUid(submission.getUid()))
                .thenReturn(Optional.of(submission));
        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(new SubmissionResponseDto());

        submissionService.approveSubmissionByUid(submission.getUid());

        assertEquals(SubmissionStatus.APPROVED, submission.getSubmissionStatus());
        assertNotNull(submission.getApprovedAt());

        verify(submissionRepository).saveAndFlush(submission);
        verify(submissionMapper).toSubmissionResponseDto(submission);
    }

    @Test
    void approvedSubmission_shouldThrowSubmissionIsFinalizedException_whenSubmissionStatusIsFinalized(){
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);

        when(submissionRepository.findByUid(submission.getUid()))
                .thenReturn(Optional.of(submission));

        assertThrows(
                SubmissionIsFinalizedException.class,
                () -> submissionService.approveSubmissionByUid(submission.getUid())
        );

        verify(submissionRepository, never()).saveAndFlush(any());
    }
}
