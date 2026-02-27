package com.test.baxolash.service.impl;

import com.test.baxolash.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
@Profile("local")
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.storage.local-path:./uploads}")
    private String uploadDir;

    @Override
    public String upload(String requestId,
                         String subDir,
                         MultipartFile file,
                         String savedName) {

        try {
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();

            Path dirPath = basePath
                    .resolve(requestId)
                    .resolve(subDir);

            Files.createDirectories(dirPath);

            Path filePath = dirPath.resolve(savedName);

            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла локально", e);
        }
    }

    @Override
    public InputStream openStream(String fileUrlOrKey) throws IOException {
        return Files.newInputStream(Paths.get(fileUrlOrKey));
    }

    @Override
    public void delete(String fileUrlOrKey) {
        try {
            Files.deleteIfExists(Paths.get(fileUrlOrKey));
        } catch (IOException ignored) {}
    }

    @Override
    public boolean exists(String fileUrlOrKey) {
        return Files.exists(Paths.get(fileUrlOrKey));
    }
}