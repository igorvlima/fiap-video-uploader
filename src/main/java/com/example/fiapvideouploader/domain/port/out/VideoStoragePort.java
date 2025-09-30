package com.example.fiapvideouploader.domain.port.out;

import com.example.fiapvideouploader.domain.model.VideoMetadata;
import org.springframework.web.multipart.MultipartFile;

public interface VideoStoragePort {
    String store(MultipartFile file, VideoMetadata metadata);
}