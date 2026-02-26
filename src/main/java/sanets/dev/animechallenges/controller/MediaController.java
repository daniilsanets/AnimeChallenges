package sanets.dev.animechallenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.media.MediaResponseDto;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.service.MediaService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    //todo:
    // add validation to deny when user upload too much data
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponseDto createMedia(@RequestPart("file") MultipartFile file){
        return mediaService.uploadMedia(file);
    }

    @GetMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public MediaResponseDto getMediaByUid(@PathVariable("uid") UUID mediaUid){
        return mediaService.getMediaDtoByUid(mediaUid);
    }

    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMediaByUid(@PathVariable("uid") UUID submissionUid){
        mediaService.delete(submissionUid);
    }
}
