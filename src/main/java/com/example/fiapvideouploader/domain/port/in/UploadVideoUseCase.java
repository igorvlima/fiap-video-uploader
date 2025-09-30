package com.example.fiapvideouploader.domain.port.in;

public interface UploadVideoUseCase {
    UploadVideoResult execute(UploadVideoCommand command);
}