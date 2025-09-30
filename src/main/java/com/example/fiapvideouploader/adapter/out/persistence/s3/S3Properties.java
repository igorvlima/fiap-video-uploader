package com.example.fiapvideouploader.adapter.out.persistence.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class S3Properties {
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucketFolder;
}