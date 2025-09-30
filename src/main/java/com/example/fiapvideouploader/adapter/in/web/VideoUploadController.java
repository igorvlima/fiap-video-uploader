package com.example.fiapvideouploader.adapter.in.web;

import com.example.fiapvideouploader.domain.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.annotations.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Validated
public class VideoUploadController {

    private final UploadVideoUseCase uploadVideoUseCase;

    @PostMapping("/upload")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @RequestParam("video") @NotNull MultipartFile file,
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "customerEmail", required = false) String customerEmail) throws IOException {

        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(file)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .customerId(customerId)
                .customerEmail(customerEmail)
                .build();

        UploadVideoResult result = uploadVideoUseCase.execute(command);

        VideoUploadResponse response = VideoUploadResponse.builder()
                .videoId(result.getVideoId().getValue())
                .videoName(result.getVideoName())
                .customerId(result.getCustomerId())
                .customerEmail(result.getCustomerEmail())
                .storageKey(result.getStorageKey())
                .uploadTimestamp(result.getUploadTimestamp())
                .success(true)
                .build();

        return ResponseEntity.ok(response);
    }
}