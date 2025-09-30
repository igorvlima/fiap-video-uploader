package com.example.fiapvideouploader.application.service;

import com.example.fiapvideouploader.application.mapper.Mapper;
import com.example.fiapvideouploader.domain.exception.VideoUploadException;
import com.example.fiapvideouploader.domain.exception.VideoValidationException;
import com.example.fiapvideouploader.domain.model.VideoId;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import com.example.fiapvideouploader.domain.model.VideoStatus;
import com.example.fiapvideouploader.domain.port.in.UploadVideoCommand;
import com.example.fiapvideouploader.domain.port.in.UploadVideoResult;
import com.example.fiapvideouploader.domain.port.in.VideoValidatorPort;
import com.example.fiapvideouploader.domain.port.out.VideoEventPublisherPort;
import com.example.fiapvideouploader.domain.port.out.VideoStoragePort;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoUploadServiceTest {

    @Mock
    private VideoValidatorPort validatorPort;

    @Mock
    private VideoStoragePort storagePort;

    @Mock
    private VideoEventPublisherPort eventPublisherPort;

    private VideoUploadService videoUploadService;

    private static final String FILE_NAME = "test-video.mp4";
    private static final String CONTENT_TYPE = "video/mp4";
    private static final long FILE_SIZE = 1024L;
    private static final Long CUSTOMER_ID = 123L;
    private static final String CUSTOMER_EMAIL = "test@example.com";
    private static final String STORAGE_KEY = "videos/test-storage-key";
    private static final UUID FIXED_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final VideoId FIXED_VIDEO_ID = new VideoId(FIXED_UUID);

    @BeforeEach
    void setUp() {
        videoUploadService = new VideoUploadService(validatorPort, storagePort, eventPublisherPort);
    }

    @Test
    void execute_shouldUploadVideoSuccessfully() {
        // Given
        MultipartFile fileContent = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(fileContent)
                .fileName(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .fileSize(FILE_SIZE)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // Mock VideoId.generate() to return a fixed UUID for testing
        try (MockedStatic<VideoId> mockedVideoId = mockStatic(VideoId.class)) {
            mockedVideoId.when(VideoId::generate).thenReturn(FIXED_VIDEO_ID);
            
            // Mock dependencies
            when(storagePort.store(any(MultipartFile.class), any(VideoMetadata.class))).thenReturn(STORAGE_KEY);
            
            // When
            UploadVideoResult result = videoUploadService.execute(command);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getVideoId()).isEqualTo(FIXED_VIDEO_ID);
            assertThat(result.getVideoName()).isEqualTo(FILE_NAME);
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
            assertThat(result.getStorageKey()).isEqualTo(STORAGE_KEY);
            assertThat(result.getUploadTimestamp()).isNotNull();
            
            // Verify interactions
            verify(validatorPort).validate(FILE_NAME, CONTENT_TYPE);
            verify(storagePort).store(eq(fileContent), any(VideoMetadata.class));
            verify(eventPublisherPort).publishVideoToProcess(any(VideoProcessorMessage.class));
            verify(eventPublisherPort).publishVideoData(any(VideoDataMessage.class));
            verify(eventPublisherPort).publishVideoStatus(any(VideoStatusMessage.class));
        }
    }

    @Test
    void execute_shouldThrowException_whenValidationFails() {
        // Given
        MultipartFile fileContent = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(fileContent)
                .fileName(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .fileSize(FILE_SIZE)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // Mock VideoId.generate() to return a fixed UUID for testing
        try (MockedStatic<VideoId> mockedVideoId = mockStatic(VideoId.class)) {
            mockedVideoId.when(VideoId::generate).thenReturn(FIXED_VIDEO_ID);
            
            // Mock validation to fail
            doThrow(new VideoValidationException("Invalid video format")).when(validatorPort).validate(FILE_NAME, CONTENT_TYPE);
            
            // When/Then
            assertThatThrownBy(() -> videoUploadService.execute(command))
                    .isInstanceOf(VideoUploadException.class)
                    .hasMessageContaining("Falha no upload do vídeo")
                    .hasCauseInstanceOf(VideoValidationException.class);
            
            // Verify interactions
            verify(validatorPort).validate(FILE_NAME, CONTENT_TYPE);
            verify(storagePort, never()).store(any(), any());
            verify(eventPublisherPort).publishVideoStatus(any(VideoStatusMessage.class));
            
            // Verify error status is published
            ArgumentCaptor<VideoStatusMessage> statusCaptor = ArgumentCaptor.forClass(VideoStatusMessage.class);
            verify(eventPublisherPort).publishVideoStatus(statusCaptor.capture());
            VideoStatusMessage statusMessage = statusCaptor.getValue();
            assertThat(statusMessage.getVideoStatus()).isEqualTo(VideoStatus.ERROR.toString());
        }
    }

    @Test
    void generateMetadata_shouldCreateCorrectMetadata() {
        // Given
        MultipartFile fileContent = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(fileContent)
                .fileName(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .fileSize(FILE_SIZE)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // When
        // We need to use reflection to access the private method
        VideoMetadata metadata = invokeGenerateMetadata(videoUploadService, command, FIXED_VIDEO_ID);

        // Then
        assertThat(metadata).isNotNull();
        assertThat(metadata.getVideoId()).isEqualTo(FIXED_VIDEO_ID);
        assertThat(metadata.getOriginalName()).isEqualTo(FILE_NAME);
        assertThat(metadata.getGeneratedName()).isNotNull();
        assertThat(metadata.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(metadata.getCustomerEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(metadata.getTimestamp()).isNotNull();
        assertThat(metadata.getFileSize()).isEqualTo(FILE_SIZE);
        assertThat(metadata.getContentType()).isEqualTo(CONTENT_TYPE);
    }

    @Test
    void sanitizeFileName_shouldHandleNullFilename() {
        // Given
        MultipartFile fileContent = new MockMultipartFile("file", null, CONTENT_TYPE, "test content".getBytes());
        
        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(fileContent)
                .fileName(null)
                .contentType(CONTENT_TYPE)
                .fileSize(FILE_SIZE)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // When
        // We need to use reflection to access the private method
        VideoMetadata metadata = invokeGenerateMetadata(videoUploadService, command, FIXED_VIDEO_ID);

        // Then
        assertThat(metadata).isNotNull();
        assertThat(metadata.getGeneratedName()).contains("unknown");
    }

    @Test
    void sanitizeFileName_shouldReplaceInvalidCharacters() {
        // Given
        String fileNameWithInvalidChars = "test video with spaces & special chars!.mp4";
        MultipartFile fileContent = new MockMultipartFile("file", fileNameWithInvalidChars, CONTENT_TYPE, "test content".getBytes());
        
        UploadVideoCommand command = UploadVideoCommand.builder()
                .fileContent(fileContent)
                .fileName(fileNameWithInvalidChars)
                .contentType(CONTENT_TYPE)
                .fileSize(FILE_SIZE)
                .customerId(CUSTOMER_ID)
                .customerEmail(CUSTOMER_EMAIL)
                .build();

        // When
        // We need to use reflection to access the private method
        VideoMetadata metadata = invokeGenerateMetadata(videoUploadService, command, FIXED_VIDEO_ID);

        // Then
        assertThat(metadata).isNotNull();
        assertThat(metadata.getGeneratedName()).doesNotContain(" ", "&", "!", "special chars");
        assertThat(metadata.getGeneratedName()).contains("test_video_with_spaces___special_chars_.mp4");
    }

    // Helper method to invoke private generateMetadata method using reflection
    private VideoMetadata invokeGenerateMetadata(VideoUploadService service, UploadVideoCommand command, VideoId videoId) {
        try {
            java.lang.reflect.Method method = VideoUploadService.class.getDeclaredMethod("generateMetadata", UploadVideoCommand.class, VideoId.class);
            method.setAccessible(true);
            return (VideoMetadata) method.invoke(service, command, videoId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke generateMetadata method", e);
        }
    }
}