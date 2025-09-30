package com.example.fiapvideouploader.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class VideoUploadErrorResponse {
    private final boolean success;
    private final String message;
    private final LocalDateTime timestamp;
}