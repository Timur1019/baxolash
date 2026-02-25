package com.test.baxolash.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Единственное файловое хранилище — Cloudflare R2 (S3 compatible).
 * Файлы не сохраняются локально; в БД хранятся только URL.
 */
public interface FileStorageService {

    /**
     * Загрузить файл в R2 и вернуть публичный URL для хранения в БД.
     *
     * @param requestId id заявки
     * @param subDir    подкаталог (например "documents" или "report")
     * @param file      загружаемый файл
     * @param savedName имя файла для сохранения
     * @return публичный URL файла (при заданном S3_PUBLIC_URL) или ключ объекта
     */
    String upload(String requestId, String subDir, MultipartFile file, String savedName);

    /**
     * Открыть поток для чтения по URL или по ключу S3.
     */
    InputStream openStream(String fileUrlOrKey) throws IOException;

    /**
     * Проверить, существует ли объект в R2 по URL или ключу.
     */
    boolean exists(String fileUrlOrKey);

    /**
     * Удалить файл из R2 по публичному URL или по ключу.
     */
    void delete(String fileUrlOrKey);
}
