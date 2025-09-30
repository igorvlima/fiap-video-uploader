package com.example.fiapvideouploader.adapter.out.persistence.s3;

import com.example.fiapvideouploader.domain.exception.VideoStorageException;
import com.example.fiapvideouploader.domain.model.VideoMetadata;
import com.example.fiapvideouploader.domain.port.out.VideoStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3VideoStorageAdapter implements VideoStoragePort {

    private final S3Client s3Client;
    private final S3Properties properties;

    @Override
    public String store(MultipartFile file, VideoMetadata metadata) {
        String s3Key = buildS3Key(metadata.getGeneratedName());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(s3Key)
                    .contentType(metadata.getContentType())
                    .metadata(buildMetadata(metadata))
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("Upload S3 concluído. Bucket: {}, Key: {}",
                    properties.getBucketName(), s3Key);

            return s3Key;

        } catch (Exception e) {
            log.error("Erro no upload para S3", e);
            throw new VideoStorageException("Falha no upload para S3", e);
        }
    }

    private String buildS3Key(String fileName) {
        String folder = properties.getBucketFolder();
        if (folder != null && !folder.isEmpty()) {
            return folder.endsWith("/") ? folder + fileName : folder + "/" + fileName;
        }
        return fileName;
    }

    private Map<String, String> buildMetadata(VideoMetadata metadata) {
        return Map.of(
                "original-name", metadata.getOriginalName(),
                "video-id", metadata.getVideoId().toString(),
                "upload-timestamp", metadata.getTimestamp().toString(),
                "file-size", String.valueOf(metadata.getFileSize())
        );
    }
}