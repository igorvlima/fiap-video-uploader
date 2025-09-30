package com.example.fiapvideouploader.domain.port.in;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UploadVideoCommand {
    private final MultipartFile fileContent;
    private final String fileName;
    private final String contentType;
    private final long fileSize;
    private final Long customerId;
    private final String customerEmail;
}