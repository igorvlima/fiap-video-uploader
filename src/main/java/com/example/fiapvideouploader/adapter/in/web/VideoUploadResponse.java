package com.example.fiapvideouploader.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class VideoUploadResponse {
    private final UUID videoId;
    private final String videoName;
    private final Long customerId;
    private final String customerEmail;
    private final String storageKey;
    private final LocalDateTime uploadTimestamp;
    private final boolean success;
}