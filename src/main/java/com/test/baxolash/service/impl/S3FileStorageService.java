package com.test.baxolash.service.impl;

import com.test.baxolash.service.FileStorageService;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Единственное хранилище файлов — Cloudflare R2 (S3 compatible).
 * Все файлы загружаются в R2; возвращается публичный URL для хранения в БД.
 * Создаётся только при наличии S3_ENDPOINT (и S3Client).
 */
@Service
@ConditionalOnBean(S3Client.class)
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${S3_BUCKET:}")
    private String bucket;

    @Value("${S3_PUBLIC_URL:}")
    private String publicUrl;

    @Override
    public String upload(String requestId, String subDir, MultipartFile file, String savedName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("S3_BUCKET is not set");
        }
        String key = "evaluation-requests/" + requestId + "/" + subDir + "/" + savedName;
        try {
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .metadata(Map.of("original-name", file.getOriginalFilename() != null ? file.getOriginalFilename() : savedName))
                    .build();
            s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            LogUtil.info("File uploaded to R2: {}", key);
            if (publicUrl != null && !publicUrl.isBlank()) {
                return publicUrl.endsWith("/") ? publicUrl + key : publicUrl + "/" + key;
            }
            return key;
        } catch (IOException e) {
            LogUtil.error("Failed to upload file to R2: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить файл", e);
        }
    }

    @Override
    public InputStream openStream(String fileUrlOrKey) throws IOException {
        String key = keyFromUrlOrKey(fileUrlOrKey);
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("URL или ключ не задан");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("S3_BUCKET is not set");
        }
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            LogUtil.error("Failed to open R2 stream for {}: {}", key, e.getMessage());
            throw new IOException("Не удалось открыть файл: " + key, e);
        }
    }

    @Override
    public boolean exists(String fileUrlOrKey) {
        String key = keyFromUrlOrKey(fileUrlOrKey);
        if (key == null || key.isBlank() || bucket == null || bucket.isBlank()) {
            return false;
        }
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            LogUtil.error("R2 headObject failed for {}: {}", key, e.getMessage());
            return false;
        }
    }

    @Override
    public void delete(String fileUrlOrKey) {
        String key = keyFromUrlOrKey(fileUrlOrKey);
        if (key == null || key.isBlank() || bucket == null || bucket.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            LogUtil.info("File deleted from R2: {}", key);
        } catch (Exception e) {
            LogUtil.error("Failed to delete from R2 {}: {}", key, e.getMessage());
        }
    }

    /** Извлекает S3-ключ из публичного URL (S3_PUBLIC_URL + key) или возвращает значение как ключ. */
    private String keyFromUrlOrKey(String fileUrlOrKey) {
        if (fileUrlOrKey == null || fileUrlOrKey.isBlank()) {
            return null;
        }
        if (publicUrl != null && !publicUrl.isBlank() && fileUrlOrKey.startsWith(publicUrl)) {
            String suffix = fileUrlOrKey.substring(publicUrl.length());
            return suffix.startsWith("/") ? suffix.substring(1) : suffix;
        }
        return fileUrlOrKey;
    }
}
