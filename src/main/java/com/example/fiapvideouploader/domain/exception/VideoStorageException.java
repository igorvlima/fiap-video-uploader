package com.example.fiapvideouploader.domain.exception;

public class VideoStorageException extends RuntimeException {
    public VideoStorageException(String message) {
        super(message);
    }

    public VideoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
