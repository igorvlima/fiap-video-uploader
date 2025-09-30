package com.example.fiapvideouploader.domain.port.in;

public interface VideoValidatorPort {
    void validate(String fileName, String contentType);
}