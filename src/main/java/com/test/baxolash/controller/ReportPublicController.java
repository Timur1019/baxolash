package com.test.baxolash.controller;

import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.service.EvaluationRequestExportService;
import com.test.baxolash.service.ReportTokenService;
import com.test.baxolash.util.EvaluationRequestReportHtmlUtil;
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

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Публичный доступ к отчёту по токену (сканирование QR-кода).
 * Браузер получает HTML (удобно на телефоне), иначе — PDF.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Публичный отчёт", description = "Просмотр отчёта по ссылке из QR-кода")
public class ReportPublicController {

    private final ReportTokenService reportTokenService;
    private final EvaluationRequestExportService exportService;

    @GetMapping("/report")
    @Operation(summary = "Отчёт по токену: HTML в браузере или PDF")
    public ResponseEntity<?> getReport(
            @RequestParam("token") String token,
            @RequestParam(value = "download", required = false) Boolean download,
            HttpServletRequest request) {
        String requestId = reportTokenService.validateAndGetRequestId(token);
        if (requestId == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean forceDownload = Boolean.TRUE.equals(download);
        String accept = request.getHeader("Accept");
        boolean wantsHtml = accept != null && accept.contains("text/html");

        if (forceDownload || !wantsHtml) {
            byte[] pdf = exportService.exportSingleToPdf(requestId);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdf));
            String disposition = forceDownload ? "attachment" : "inline";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"report-" + requestId + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdf.length)
                    .body(resource);
        }

        EvaluationRequestDto dto = exportService.getDtoForPublicReport(requestId);
        String qs = request.getQueryString();
        String pdfDownloadUrl = request.getRequestURL() + "?" + (qs != null && !qs.isEmpty() ? qs + "&" : "token=" + token + "&") + "download=1";
        String html = EvaluationRequestReportHtmlUtil.buildHtml(dto, pdfDownloadUrl);
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .contentLength(bytes.length)
                .body(bytes);
    }
}
