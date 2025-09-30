package com.example.fiapvideouploader.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@AllArgsConstructor
public class VideoUploadRequest {
    private final MultipartFile file;
    private final Long customerId;
    private final String customerEmail;
}