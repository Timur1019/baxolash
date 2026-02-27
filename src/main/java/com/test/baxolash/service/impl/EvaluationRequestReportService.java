package com.test.baxolash.service.impl;

import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.exception.BusinessException;
import com.test.baxolash.exception.NotFoundException;
import com.test.baxolash.repository.EvaluationRequestRepository;
import com.test.baxolash.service.FileStorageService;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Работа с отчётами заявок на оценку (без проверки прав доступа).
 */
@Service
@RequiredArgsConstructor
public class EvaluationRequestReportService {

    private final EvaluationRequestRepository requestRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void uploadReport(String requestId, MultipartFile file) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        String safeName = sanitizeFileName(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + safeName;
        String fileUrl = fileStorageService.upload(requestId, "report", file, storedName);

        request.setReportFileName(safeName);
        request.setReportFileUrl(fileUrl);
        request.setStatus(EvaluationRequestStatus.REPORT_READY);
        requestRepository.save(request);
        LogUtil.info("Report uploaded for request {}", requestId);
    }

    @Transactional
    public void confirmCompletion(String requestId) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        request.setStatus(EvaluationRequestStatus.COMPLETED);
        request.setCompletedAt(java.time.Instant.now());
        requestRepository.save(request);
        LogUtil.info("Evaluation request completed: {}", requestId);
    }

    @Transactional(readOnly = true)
    public FileDownloadResult downloadReport(String requestId) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        if (request.getReportFileUrl() == null || !fileStorageService.exists(request.getReportFileUrl())) {
            throw new NotFoundException("Отчёт ещё не загружен");
        }
        try {
            var stream = fileStorageService.openStream(request.getReportFileUrl());
            String fileName = request.getReportFileName() != null ? request.getReportFileName() : "report";
            return new FileDownloadResult(stream, fileName);
        } catch (Exception e) {
            LogUtil.error("Download report failed: {}", e.getMessage());
            throw new BusinessException("Не удалось скачать отчёт");
        }
    }

    @Transactional
    public void deleteReport(String requestId) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        if (request.getReportFileUrl() != null) {
            fileStorageService.delete(request.getReportFileUrl());
            request.setReportFileUrl(null);
            request.setReportFileName(null);
        }
        if (request.getStatus() == EvaluationRequestStatus.REPORT_READY) {
            request.setStatus(EvaluationRequestStatus.IN_PROGRESS);
        }
        requestRepository.save(request);
        LogUtil.info("Report deleted for request {}", requestId);
    }

    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) return "document";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

}
