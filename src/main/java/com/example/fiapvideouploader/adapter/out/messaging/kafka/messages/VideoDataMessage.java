package com.example.fiapvideouploader.adapter.out.messaging.kafka.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class VideoDataMessage {
    private final UUID videoId;
    private final String originalName;
    private final String generatedName;
    private final String s3Key;
    private final Long customerId;
    private final String customerEmail;
}