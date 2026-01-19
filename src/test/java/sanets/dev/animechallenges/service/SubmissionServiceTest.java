package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.exception.submission.SubmissionMediaLimitExceededException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.Submission;
import sanets.dev.animechallenges.model.SubmissionStatus;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
                .build();

    }

    @Test
    void createSubmission_shouldCreateSubmissionAndReturnDto() {
        UUID participationUid = questParticipation.getUid();

        CreateSubmissionRequestDto dto = new CreateSubmissionRequestDto();
        dto.setParticipationUid(participationUid);
        dto.setMedia(List.of());
        dto.setDescription("Cool description");
        dto.setNotes("Cool notes");

        SubmissionResponseDto responseDto = new SubmissionResponseDto();
        responseDto.setUid(submission.getUid());

        when(questParticipationService.getQuestParticipationByUid(participationUid))
                .thenReturn(questParticipation);

        when(submissionMapper.toSubmission(dto, questParticipation))
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

        Media media1 = new Media();
        media1.setMimeType("image/png");

        Media media2 = new Media();
        media2.setMimeType("image/jpeg");

        CreateSubmissionRequestDto dto = new CreateSubmissionRequestDto();
        dto.setParticipationUid(participationUid);
        dto.setDescription("desc");
        dto.setNotes("notes");
        dto.setMedia(List.of(media1, media2));

        SubmissionResponseDto responseDto = new SubmissionResponseDto();
        responseDto.setUid(submission.getUid());

        when(questParticipationService.getQuestParticipationByUid(participationUid))
                .thenReturn(questParticipation);

        when(submissionMapper.toSubmission(dto, questParticipation))
                .thenReturn(submission);

        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(responseDto);

        when(submissionMediaRepository.countBySubmissionUid(submission.getUid()))
                .thenReturn(0L);

        ReflectionTestUtils.setField(
                submissionService,
                "MAX_MEDIA_COULD_BE_ADDED",
                5
        );

        SubmissionResponseDto result = submissionService.createSubmission(dto);

        assertNotNull(result);

        verify(submissionRepository).save(submission);
        verify(submissionMediaRepository).countBySubmissionUid(submission.getUid());

        verify(submissionMediaRepository, times(2))
                .save(any());
    }

    @Test
    void createSubmission_withMedia_shouldThrowSubmissionMediaLimitExceededException() {
        UUID participationUid = questParticipation.getUid();

        List<Media> mediaList = new ArrayList<>();

        IntStream.range(0, 3).forEach(i -> {
            mediaList.add(new Media());
        });

        CreateSubmissionRequestDto dto = new CreateSubmissionRequestDto();
        dto.setParticipationUid(participationUid);
        dto.setDescription("desc");
        dto.setNotes("notes");
        dto.setMedia(mediaList);

        when(questParticipationService.getQuestParticipationByUid(participationUid))
                .thenReturn(questParticipation);

        when(submissionMapper.toSubmission(dto, questParticipation))
                .thenReturn(submission);

        when(submissionMediaRepository.countBySubmissionUid(submission.getUid()))
                .thenReturn(4L);

        ReflectionTestUtils.setField(
                submissionService,
                "MAX_MEDIA_COULD_BE_ADDED",
                5
        );

        assertThrows(SubmissionMediaLimitExceededException.class, () -> submissionService.createSubmission(dto));
        verify(submissionRepository).save(submission);

        verify(submissionMediaRepository, never()).save(any());
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
    void approvedSubmission_shouldSetApprovedAndReturnDto_whenItsSuccessfully(){
        submission.setSubmissionStatus(SubmissionStatus.PENDING);

        when(submissionRepository.findByUid(submission.getUid()))
                .thenReturn(Optional.of(submission));

        when(submissionMapper.toSubmissionResponseDto(submission))
                .thenReturn(new SubmissionResponseDto());

        submissionService.approveSubmissionByUid(submission.getUid());

        assertEquals(SubmissionStatus.APPROVED, submission.getSubmissionStatus());
        assertNotNull(submission.getApprovedAt());

        verify(submissionRepository).saveAndFlush(submission);
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
