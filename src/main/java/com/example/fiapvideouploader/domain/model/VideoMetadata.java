package com.example.fiapvideouploader.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class VideoMetadata {
    private final VideoId videoId;
    private final String originalName;
    private final String generatedName;
    private final Long customerId;
    private final String customerEmail;
    private final LocalDateTime timestamp;
    private final long fileSize;
    private final String contentType;
}