package com.example.fiapvideouploader.adapter.out.validation;

import com.example.fiapvideouploader.domain.exception.VideoValidationException;
import com.example.fiapvideouploader.domain.port.in.VideoValidatorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@Slf4j
public class VideoValidatorAdapter implements VideoValidatorPort {

    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".avi", ".mov", ".mkv", ".wmv", ".flv", ".webm"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "video/mp4", "video/avi", "video/quicktime", "video/x-msvideo"
    );

    @Override
    public void validate(String fileName, String contentType) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new VideoValidationException("Nome do arquivo inválido");
        }

        validateFileName(fileName);
        validateContentType(contentType);
    }

    private void validateFileName(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        boolean hasValidExtension = ALLOWED_VIDEO_EXTENSIONS.stream()
                .anyMatch(lowerCaseFilename::endsWith);

        if (!hasValidExtension) {
            throw new VideoValidationException("Formato de arquivo não suportado: " + filename);
        }
    }

    private void validateContentType(String contentType) {
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("Content-Type não reconhecido: {}", contentType);
        }
    }
}