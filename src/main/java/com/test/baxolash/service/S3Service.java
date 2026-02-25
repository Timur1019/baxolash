package com.test.baxolash.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Загрузка файлов в S3-совместимое хранилище (Cloudflare R2).
 * Возвращает URL загруженного файла (если задан S3_PUBLIC_URL).
 */
public interface S3Service {

    /**
     * Загрузить файл в bucket и вернуть URL для доступа.
     *
     * @param file файл для загрузки
     * @return URL файла (например https://bucket.example.com/key) или ключ объекта, если публичный URL не настроен
     */
    String upload(MultipartFile file);
}
