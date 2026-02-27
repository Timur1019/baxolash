package com.test.baxolash.service.impl;

import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.EvaluationRequestDocument;
import com.test.baxolash.entity.User;
import com.test.baxolash.exception.NotFoundException;
import com.test.baxolash.repository.EvaluationRequestDocumentRepository;
import com.test.baxolash.repository.EvaluationRequestRepository;
import com.test.baxolash.service.FileStorageService;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Работа с документами заявок на оценку (без проверки прав доступа).
 */
@Service
@RequiredArgsConstructor
public class EvaluationRequestDocumentService {

    private final EvaluationRequestRepository requestRepository;
    private final EvaluationRequestDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void uploadDocument(String requestId, MultipartFile file, User uploadedBy) {
        var request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        String safeName = sanitizeFileName(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + safeName;
        String fileUrl = fileStorageService.upload(requestId, "documents", file, storedName);

        EvaluationRequestDocument doc = new EvaluationRequestDocument();
        doc.setEvaluationRequest(request);
        doc.setFileName(safeName);
        doc.setFileUrl(fileUrl);
        doc.setUploadedBy(uploadedBy);
        documentRepository.save(doc);
        LogUtil.info("Document uploaded for request {}: {}", requestId, safeName);
    }

    @Transactional(readOnly = true)
    public FileDownloadResult downloadDocument(String documentId) {
        EvaluationRequestDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Документ не найден"));
        if (!fileStorageService.exists(doc.getFileUrl())) {
            throw new NotFoundException("Файл не найден");
        }
        try {
            var stream = fileStorageService.openStream(doc.getFileUrl());
            return new FileDownloadResult(stream, doc.getFileName());
        } catch (Exception e) {
            LogUtil.error("Download document failed: {}", e.getMessage());
            throw new com.test.baxolash.exception.BusinessException("Не удалось скачать документ");
        }
    }

    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) return "document";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
}
