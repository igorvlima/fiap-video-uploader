package com.example.fiapvideouploader.domain.port.in;

import com.example.fiapvideouploader.domain.model.VideoId;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UploadVideoResult {
    private final VideoId videoId;
    private final String videoName;
    private final Long customerId;
    private final String customerEmail;
    private final String storageKey;
    private final LocalDateTime uploadTimestamp;
}