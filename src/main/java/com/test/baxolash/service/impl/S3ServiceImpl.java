package com.test.baxolash.service.impl;

import com.test.baxolash.service.S3Service;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Загрузка файлов в S3/R2. Создаётся только при наличии S3_ENDPOINT.
 */
@Service
@ConditionalOnProperty(name = "S3_ENDPOINT")
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${S3_BUCKET:}")
    private String bucket;

    @Value("${S3_PUBLIC_URL:}")
    private String publicUrl;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("S3_BUCKET is not set");
        }
        String originalName = file.getOriginalFilename();
        String safeName = originalName != null && !originalName.isBlank()
                ? originalName.replaceAll("[\\\\/:*?\"<>|]", "_").trim()
                : "file";
        String key = "uploads/" + UUID.randomUUID() + "_" + safeName;
        try {
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .metadata(Map.of("original-name", originalName != null ? originalName : safeName))
                    .build();
            s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            LogUtil.info("File uploaded to S3: {}", key);
            if (publicUrl != null && !publicUrl.isBlank()) {
                return publicUrl.endsWith("/") ? publicUrl + key : publicUrl + "/" + key;
            }
            return key;
        } catch (IOException e) {
            LogUtil.error("Failed to upload file to S3: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить файл", e);
        }
    }
}
