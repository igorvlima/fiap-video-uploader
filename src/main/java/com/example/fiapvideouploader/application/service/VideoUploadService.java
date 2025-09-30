package com.example.fiapvideouploader.application.service;

import com.example.fiapvideouploader.application.mapper.Mapper;
import com.example.fiapvideouploader.domain.exception.VideoUploadException;
import com.example.fiapvideouploader.domain.model.*;
import com.example.fiapvideouploader.domain.port.in.*;
import com.example.fiapvideouploader.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoUploadService implements UploadVideoUseCase {

    private final VideoValidatorPort validatorPort;
    private final VideoStoragePort storagePort;
    private final VideoEventPublisherPort eventPublisherPort;

    @Override
    public UploadVideoResult execute(UploadVideoCommand command) {
        log.info("Iniciando upload de vídeo: {}", command.getFileName());

        VideoId videoId = VideoId.generate();

        try {
            validatorPort.validate(command.getFileName(), command.getContentType());

            VideoMetadata metadata = generateMetadata(command, videoId);

            String storageKey = storagePort.store(command.getFileContent(), metadata);

            eventPublisherPort.publishVideoToProcess(Mapper.buildVideoProcessorMessage(metadata.getVideoId().getValue(), metadata.getCustomerEmail(), metadata.getOriginalName(), storageKey));
            eventPublisherPort.publishVideoData(Mapper.buildVideoDataMessage(metadata));
            eventPublisherPort.publishVideoStatus(Mapper.buildVideoStatusMessage(metadata, VideoStatus.UPLOADED.toString()));

            log.info("Upload concluído com sucesso. VideoId: {}", videoId);

            return UploadVideoResult.builder()
                    .videoId(videoId)
                    .videoName(metadata.getOriginalName())
                    .customerId(command.getCustomerId())
                    .customerEmail(command.getCustomerEmail())
                    .storageKey(storageKey)
                    .uploadTimestamp(metadata.getTimestamp())
                    .build();

        } catch (Exception e) {
            log.error("Erro durante o upload", e);
            eventPublisherPort.publishVideoStatus(Mapper.buildVideoStatusMessage(command, videoId.getValue(), VideoStatus.ERROR.toString()));
            throw new VideoUploadException("Falha no upload do vídeo", e);
        }
    }

    private VideoMetadata generateMetadata(UploadVideoCommand command, VideoId videoId) {
        LocalDateTime timestamp = LocalDateTime.now();
        String generatedName = generateFileName(videoId, command.getFileName(), timestamp);

        return VideoMetadata.builder()
                .videoId(videoId)
                .originalName(command.getFileName())
                .generatedName(generatedName)
                .customerId(command.getCustomerId())
                .customerEmail(command.getCustomerEmail())
                .timestamp(timestamp)
                .fileSize(command.getFileSize())
                .contentType(command.getContentType())
                .build();
    }

    private String generateFileName(VideoId videoId, String originalName, LocalDateTime timestamp) {
        String timestampStr = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String sanitizedName = sanitizeFileName(originalName);
        return String.format("%s_%s_%s", videoId, sanitizedName, timestampStr);
    }

    private String sanitizeFileName(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}