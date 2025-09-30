package com.example.fiapvideouploader.adapter.in.web;

import com.example.fiapvideouploader.domain.exception.VideoUploadException;
import com.example.fiapvideouploader.domain.exception.VideoValidationException;
import com.example.fiapvideouploader.domain.model.VideoId;
import com.example.fiapvideouploader.domain.port.in.UploadVideoCommand;
import com.example.fiapvideouploader.domain.port.in.UploadVideoResult;
import com.example.fiapvideouploader.domain.port.in.UploadVideoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VideoUploadControllerTest {

    @Mock
    private UploadVideoUseCase uploadVideoUseCase;

    @InjectMocks
    private VideoUploadController videoUploadController;

    @Test
    void uploadVideo_shouldReturnSuccessResponse_whenUploadIsSuccessful() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "video",
                "test-video.mp4",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test content".getBytes()
        );

        Long customerId = 123L;
        String customerEmail = "test@example.com";
        UUID videoId = UUID.randomUUID();
        String storageKey = "videos/test-key";
        LocalDateTime now = LocalDateTime.now();

        UploadVideoResult result = UploadVideoResult.builder()
                .videoId(new VideoId(videoId))
                .videoName("test-video.mp4")
                .customerId(customerId)
                .customerEmail(customerEmail)
                .storageKey(storageKey)
                .uploadTimestamp(now)
                .build();

        when(uploadVideoUseCase.execute(any(UploadVideoCommand.class))).thenReturn(result);

        // Set up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(videoUploadController)
                .setControllerAdvice(new VideoUploadExceptionHandler())
                .build();

        // When/Then
        mockMvc.perform(multipart("/api/v1/videos/upload")
                        .file(file)
                        .param("customerId", customerId.toString())
                        .param("customerEmail", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value(videoId.toString()))
                .andExpect(jsonPath("$.videoName").value("test-video.mp4"))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.customerEmail").value(customerEmail))
                .andExpect(jsonPath("$.storageKey").value(storageKey))
                .andExpect(jsonPath("$.success").value(true));

        // Verify that the use case was called with the correct command
        ArgumentCaptor<UploadVideoCommand> commandCaptor = ArgumentCaptor.forClass(UploadVideoCommand.class);
        verify(uploadVideoUseCase).execute(commandCaptor.capture());
        UploadVideoCommand capturedCommand = commandCaptor.getValue();
        
        assertThat(capturedCommand.getFileName()).isEqualTo("test-video.mp4");
        assertThat(capturedCommand.getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertThat(capturedCommand.getFileSize()).isEqualTo(file.getSize());
        assertThat(capturedCommand.getCustomerId()).isEqualTo(customerId);
        assertThat(capturedCommand.getCustomerEmail()).isEqualTo(customerEmail);
    }

    @Test
    void uploadVideo_shouldReturnErrorResponse_whenValidationFails() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "video",
                "test-video.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
        );

        when(uploadVideoUseCase.execute(any(UploadVideoCommand.class)))
                .thenThrow(new VideoValidationException("Formato de arquivo inválido"));

        // Set up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(videoUploadController)
                .setControllerAdvice(new VideoUploadExceptionHandler())
                .build();

        // When/Then
        mockMvc.perform(multipart("/api/v1/videos/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Formato de arquivo inválido"));
    }

    @Test
    void uploadVideo_shouldReturnErrorResponse_whenUploadFails() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "video",
                "test-video.mp4",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test content".getBytes()
        );

        when(uploadVideoUseCase.execute(any(UploadVideoCommand.class)))
                .thenThrow(new VideoUploadException("Falha no upload do vídeo"));

        // Set up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(videoUploadController)
                .setControllerAdvice(new VideoUploadExceptionHandler())
                .build();

        // When/Then
        mockMvc.perform(multipart("/api/v1/videos/upload")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Falha no upload do vídeo"));
    }
}