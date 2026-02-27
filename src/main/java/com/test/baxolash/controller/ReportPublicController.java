package com.test.baxolash.controller;

import com.test.baxolash.service.EvaluationRequestExportService;
import com.test.baxolash.service.ReportTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

/**
 * Публичный доступ к PDF-отчёту по токену (сканирование QR-кода без авторизации).
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Публичный отчёт", description = "Просмотр PDF по ссылке из QR-кода")
public class ReportPublicController {

    private final ReportTokenService reportTokenService;
    private final EvaluationRequestExportService exportService;

    @GetMapping("/report")
    @Operation(summary = "Получить PDF-отчёт по токену (для QR-кода)")
    public ResponseEntity<InputStreamResource> getReportPdf(@RequestParam("token") String token) {
        String requestId = reportTokenService.validateAndGetRequestId(token);
        if (requestId == null) {
            return ResponseEntity.badRequest().build();
        }
        byte[] pdf = exportService.exportSingleToPdf(requestId);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdf));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"report-" + requestId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }
}
