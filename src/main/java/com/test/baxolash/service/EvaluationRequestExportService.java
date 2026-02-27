package com.test.baxolash.service;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;

public interface EvaluationRequestExportService {

    byte[] exportAllToExcel(EvaluationRequestStatus statusFilter);

    /**
     * Экспорт одной заявки в Word (docx) — сводный отчёт по клиенту.
     */
    byte[] exportSingleToWord(String requestId);

    /**
     * Экспорт одной заявки в PDF — для просмотра по QR-коду (публичная ссылка).
     */
    byte[] exportSingleToPdf(String requestId);
}

