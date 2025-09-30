package com.example.fiapvideouploader.adapter.in.web;

import com.example.fiapvideouploader.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class VideoUploadExceptionHandler {

    @ExceptionHandler(VideoValidationException.class)
    public ResponseEntity<VideoUploadErrorResponse> handleValidationException(VideoValidationException e) {
        log.error("Erro de validação: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(VideoUploadErrorResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(VideoStorageException.class)
    public ResponseEntity<VideoUploadErrorResponse> handleStorageException(VideoStorageException e) {
        log.error("Erro no armazenamento: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoUploadErrorResponse.builder()
                        .success(false)
                        .message("Erro interno no serviço de armazenamento")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(VideoUploadException.class)
    public ResponseEntity<VideoUploadErrorResponse> handleUploadException(VideoUploadException e) {
        log.error("Erro no upload: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(VideoUploadErrorResponse.builder()
                        .success(false)
                        .message("Falha no upload do vídeo")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}