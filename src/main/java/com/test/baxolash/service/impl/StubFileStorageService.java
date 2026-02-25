package com.test.baxolash.service.impl;

import com.test.baxolash.service.FileStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Заглушка хранилища при отсутствии S3/R2 (S3_ENDPOINT не задан).
 * Позволяет запускать приложение без R2; загрузка/скачивание выбросят исключение.
 */
@Service
@ConditionalOnMissingBean(FileStorageService.class)
public class StubFileStorageService implements FileStorageService {

    private static final String MESSAGE = "S3/R2 не настроен. Задайте переменные: S3_ENDPOINT, S3_ACCESS_KEY, S3_SECRET_KEY, S3_BUCKET (и при необходимости S3_PUBLIC_URL).";

    @Override
    public String upload(String requestId, String subDir, MultipartFile file, String savedName) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public InputStream openStream(String fileUrlOrKey) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public boolean exists(String fileUrlOrKey) {
        return false;
    }

    @Override
    public void delete(String fileUrlOrKey) {
        // no-op
    }
}
