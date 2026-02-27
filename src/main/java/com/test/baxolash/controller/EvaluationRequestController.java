package com.test.baxolash.controller;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.EvaluationRequestUpdateDto;
import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import com.test.baxolash.service.EvaluationRequestService;
import com.test.baxolash.service.EvaluationRequestExportService;
import com.test.baxolash.service.ReportTokenService;
import com.test.baxolash.util.MediaTypeUtil;
import com.test.baxolash.util.QrCodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation-requests")
@RequiredArgsConstructor
@Tag(name = "Заявки на оценку", description = "Клиент: свои заявки, документы, отчёт. Сотрудник компании: все заявки, статус, отчёт, стоимость.")
public class EvaluationRequestController {

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationRequestExportService exportService;
    private final ReportTokenService reportTokenService;

    @Value("${app.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @PostMapping
    @Operation(summary = "Создать заявку (клиент — банк)")
    public ResponseEntity<EvaluationRequestDto> create(@Valid @RequestBody EvaluationRequestCreateDto dto) {
        EvaluationRequestDto created = evaluationRequestService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping(value = "/with-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать заявку с документом: недвижимость, автотранспорт или основные средства")
    public ResponseEntity<EvaluationRequestDto> createWithDocument(
            @RequestPart("dto") @Valid EvaluationRequestCreateDto dto,
            @RequestPart(value = "cadastralDocument", required = false) MultipartFile cadastralDocument,
            @RequestPart(value = "techPassportDocument", required = false) MultipartFile techPassportDocument,
            @RequestPart(value = "fixedAssetsDocument", required = false) MultipartFile fixedAssetsDocument) {
        EvaluationRequestDto created = evaluationRequestService.createWithDocument(dto, cadastralDocument, techPassportDocument, fixedAssetsDocument);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/my")
    @Operation(summary = "Мои заявки (клиент) с фильтрами")
    public ResponseEntity<Page<EvaluationRequestDto>> getMy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) EvaluationRequestType requestType,
            @RequestParam(required = false) EvaluationRequestStatus status,
            @RequestParam(required = false) String regionId,
            @RequestParam(required = false) String districtId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        Pageable pageable = PageRequest.of(page, size);
        Instant from = parseDate(dateFrom, true);
        Instant to = parseDate(dateTo, false);
        return ResponseEntity.ok(evaluationRequestService.getMy(pageable, requestType, status, regionId, districtId, search, from, to));
    }

    private static Instant parseDate(String dateStr, boolean startOfDay) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDate d = LocalDate.parse(dateStr);
            return startOfDay ? d.atStartOfDay(ZoneOffset.UTC).toInstant() : d.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @GetMapping
    @Operation(summary = "Все заявки с фильтром (сотрудник оценочной компании)")
    public ResponseEntity<Page<EvaluationRequestDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) EvaluationRequestStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(evaluationRequestService.getAll(pageable, status));
    }

    @GetMapping("/export")
    @Operation(summary = "Экспорт всех заявок в Excel (для сотрудника компании)")
    public ResponseEntity<InputStreamResource> exportAll(
            @RequestParam(required = false) EvaluationRequestStatus status) {
        byte[] bytes = exportService.exportAllToExcel(status);
        InputStreamResource resource = new InputStreamResource(new java.io.ByteArrayInputStream(bytes));
        String fileName = "evaluation-requests.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/{id}/export-word")
    @Operation(summary = "Экспорт одной заявки в Word-отчёт (для сотрудника компании)")
    public ResponseEntity<InputStreamResource> exportSingleToWord(@PathVariable String id) {
        byte[] bytes = exportService.exportSingleToWord(id);
        InputStreamResource resource = new InputStreamResource(new java.io.ByteArrayInputStream(bytes));
        String fileName = "evaluation-request-" + id + ".docx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/statuses")
    @Operation(summary = "Список статусов для фильтра")
    public ResponseEntity<List<EvaluationRequestStatus>> getStatuses() {
        return ResponseEntity.ok(evaluationRequestService.getStatusesForFilter());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Карточка заявки")
    public ResponseEntity<EvaluationRequestDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(evaluationRequestService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редактировать заявку: компания/админ — любую; клиент — только свою")
    public ResponseEntity<EvaluationRequestDto> update(
            @PathVariable String id,
            @Valid @RequestBody EvaluationRequestUpdateDto dto) {
        return ResponseEntity.ok(evaluationRequestService.update(id, dto));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить документ к заявке (клиент — свои; сотрудник — любые)")
    public ResponseEntity<Void> uploadDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        evaluationRequestService.uploadDocument(id, file);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить отчёт об оценке (сотрудник компании)")
    public ResponseEntity<Void> uploadReport(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        evaluationRequestService.uploadReport(id, file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заявку (мягкое удаление). Компания/админ — любую; клиент — только свою")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        evaluationRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/report")
    @Operation(summary = "Удалить отчёт об оценке (сотрудник компании)")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        evaluationRequestService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm-completion")
    @Operation(summary = "Подтвердить завершение оценки (сотрудник компании)")
    public ResponseEntity<Void> confirmCompletion(@PathVariable String id) {
        evaluationRequestService.confirmCompletion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/report/download")
    @Operation(summary = "Скачать отчёт об оценке")
    public ResponseEntity<InputStreamResource> downloadReport(@PathVariable String id) {
        FileDownloadResult result = evaluationRequestService.downloadReport(id);
        String encodedName = URLEncoder.encode(result.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaTypeUtil.resolveMediaType(result.getFileName()))
                .body(new InputStreamResource(result.getInputStream()));
    }

    @GetMapping("/documents/{documentId}/download")
    @Operation(summary = "Скачать документ по id")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable String documentId) {
        FileDownloadResult result = evaluationRequestService.downloadDocument(documentId);
        String encodedName = URLEncoder.encode(result.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaTypeUtil.resolveMediaType(result.getFileName()))
                .body(new InputStreamResource(result.getInputStream()));
    }

    @GetMapping("/{id}/report-public-url")
    @Operation(summary = "URL для QR-кода: при сканировании открывается PDF-отчёт (без авторизации)")
    public ResponseEntity<java.util.Map<String, String>> getReportPublicUrl(@PathVariable String id) {
        evaluationRequestService.getById(id); // проверка существования и прав
        String token = reportTokenService.generateToken(id);
        String url = publicBaseUrl.replaceAll("/$", "") + "/api/public/report?token=" + token;
        return ResponseEntity.ok(java.util.Map.of("url", url));
    }

    @GetMapping("/{id}/report-qr")
    @Operation(summary = "QR-код в виде PNG: отобразить на клиенте/в компании; сканирование открывает PDF-отчёт")
    public ResponseEntity<InputStreamResource> getReportQrImage(@PathVariable String id) {
        evaluationRequestService.getById(id);
        String token = reportTokenService.generateToken(id);
        String url = publicBaseUrl.replaceAll("/$", "") + "/api/public/report?token=" + token;
        byte[] png = QrCodeUtil.generateQrPng(url);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(png.length)
                .body(new InputStreamResource(new ByteArrayInputStream(png)));
    }
}
