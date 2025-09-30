package com.example.fiapvideouploader.adapter.out.persistence.s3;

import com.example.fiapvideouploader.domain.exception.VideoStorageException;
import com.example.fiapvideouploader.domain.model.VideoId;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3VideoStorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties s3Properties;

    private S3VideoStorageAdapter s3VideoStorageAdapter;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String BUCKET_FOLDER = "videos";
    private static final String FILE_NAME = "test-video.mp4";
    private static final String CONTENT_TYPE = "video/mp4";
    private static final long FILE_SIZE = 1024L;
    private static final UUID VIDEO_ID = UUID.randomUUID();
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        s3VideoStorageAdapter = new S3VideoStorageAdapter(s3Client, s3Properties);
        when(s3Properties.getBucketName()).thenReturn(BUCKET_NAME);
        when(s3Properties.getBucketFolder()).thenReturn(BUCKET_FOLDER);
    }

    @Test
    void store_shouldUploadFileToS3AndReturnKey() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(FILE_NAME)
                .generatedName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .contentType(CONTENT_TYPE)
                .timestamp(TIMESTAMP)
                .build();

        // When
        String result = s3VideoStorageAdapter.store(file, metadata);

        // Then
        assertThat(result).isEqualTo(BUCKET_FOLDER + "/" + FILE_NAME);

        // Verify S3Client was called with correct parameters
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());
        
        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(BUCKET_FOLDER + "/" + FILE_NAME);
        assertThat(capturedRequest.contentType()).isEqualTo(CONTENT_TYPE);
        
        Map<String, String> capturedMetadata = capturedRequest.metadata();
        assertThat(capturedMetadata).containsEntry("original-name", FILE_NAME);
        assertThat(capturedMetadata).containsEntry("video-id", VIDEO_ID.toString());
        assertThat(capturedMetadata).containsEntry("upload-timestamp", TIMESTAMP.toString());
        assertThat(capturedMetadata).containsEntry("file-size", String.valueOf(FILE_SIZE));
        
        RequestBody capturedBody = bodyCaptor.getValue();
        assertThat(capturedBody.contentLength()).isEqualTo(file.getSize());
    }

    @Test
    void store_shouldHandleFolderWithTrailingSlash() throws IOException {
        // Given
        when(s3Properties.getBucketFolder()).thenReturn(BUCKET_FOLDER + "/");
        
        MultipartFile file = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(FILE_NAME)
                .generatedName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .contentType(CONTENT_TYPE)
                .timestamp(TIMESTAMP)
                .build();

        // When
        String result = s3VideoStorageAdapter.store(file, metadata);

        // Then
        assertThat(result).isEqualTo(BUCKET_FOLDER + "/" + FILE_NAME);
        
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        
        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.key()).isEqualTo(BUCKET_FOLDER + "/" + FILE_NAME);
    }

    @Test
    void store_shouldHandleEmptyFolder() throws IOException {
        // Given
        when(s3Properties.getBucketFolder()).thenReturn("");
        
        MultipartFile file = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(FILE_NAME)
                .generatedName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .contentType(CONTENT_TYPE)
                .timestamp(TIMESTAMP)
                .build();

        // When
        String result = s3VideoStorageAdapter.store(file, metadata);

        // Then
        assertThat(result).isEqualTo(FILE_NAME);
        
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        
        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.key()).isEqualTo(FILE_NAME);
    }

    @Test
    void store_shouldThrowVideoStorageException_whenS3ClientThrowsException() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(FILE_NAME, FILE_NAME, CONTENT_TYPE, "test content".getBytes());
        
        VideoMetadata metadata = VideoMetadata.builder()
                .videoId(new VideoId(VIDEO_ID))
                .originalName(FILE_NAME)
                .generatedName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .contentType(CONTENT_TYPE)
                .timestamp(TIMESTAMP)
                .build();
        
        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // When/Then
        assertThatThrownBy(() -> s3VideoStorageAdapter.store(file, metadata))
                .isInstanceOf(VideoStorageException.class)
                .hasMessageContaining("Falha no upload para S3")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}