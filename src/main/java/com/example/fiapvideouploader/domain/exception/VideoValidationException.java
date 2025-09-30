package com.example.fiapvideouploader.domain.exception;

public class VideoValidationException extends RuntimeException {
    public VideoValidationException(String message) {
        super(message);
    }
}