package com.example.fiapvideouploader.adapter.out.messaging.kafka.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class VideoProcessorMessage {
    private String videoId;
    private String s3Key;
    private String videoName;
    private String customerEmail;
}
