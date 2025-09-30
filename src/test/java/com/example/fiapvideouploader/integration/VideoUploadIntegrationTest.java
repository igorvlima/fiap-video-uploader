package com.example.fiapvideouploader.integration;

import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoDataMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoProcessorMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.messages.VideoStatusMessage;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoDataProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoStatusProducer;
import com.example.fiapvideouploader.adapter.out.messaging.kafka.producer.VideoToProcessProducer;
import com.example.fiapvideouploader.adapter.out.persistence.s3.S3Properties;
import com.example.fiapvideouploader.domain.model.VideoStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private VideoToProcessProducer videoToProcessProducer;

    @MockitoBean
    private VideoStatusProducer videoStatusProducer;

    @MockitoBean
    private VideoDataProducer videoDataProducer;

    @Mock
    private S3Properties s3Properties;

    @Test
    void uploadVideo_shouldProcessVideoSuccessfully() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "video",
                "test-video.mp4",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test content".getBytes()
        );

        Long customerId = 123L;
        String customerEmail = "test@example.com";

        // Mock S3 storage to return a key
        when(s3Properties.getBucketName()).thenReturn("test-bucket");
        when(s3Properties.getBucketFolder()).thenReturn("videos");

        // When/Then
        mockMvc.perform(multipart("/api/v1/videos/upload")
                        .file(file)
                        .param("customerId", customerId.toString())
                        .param("customerEmail", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoName").value("test-video.mp4"))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.customerEmail").value(customerEmail))
                .andExpect(jsonPath("$.success").value(true));

        // Verify S3 client was called
        verify(s3Client).putObject((PutObjectRequest) any(), (RequestBody) any());

        // Verify Kafka producers were called
        verify(videoToProcessProducer).send(any(VideoProcessorMessage.class));
        verify(videoStatusProducer).send(any(VideoStatusMessage.class));
        verify(videoDataProducer).send(any(VideoDataMessage.class));

        // Verify status message contains UPLOADED status
        ArgumentCaptor<VideoStatusMessage> statusCaptor = ArgumentCaptor.forClass(VideoStatusMessage.class);
        verify(videoStatusProducer).send(statusCaptor.capture());
        VideoStatusMessage statusMessage = statusCaptor.getValue();
        assertThat(statusMessage.getVideoStatus()).isEqualTo(VideoStatus.UPLOADED.toString());
    }

    @Test
    void uploadVideo_shouldReturnError_whenFileFormatIsInvalid() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "video",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // When/Then
        mockMvc.perform(multipart("/api/v1/videos/upload")
                        .file(file))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Falha no upload do vídeo"));
    }
}