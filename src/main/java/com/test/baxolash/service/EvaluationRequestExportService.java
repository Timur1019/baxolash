package com.test.baxolash.service;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;

public interface EvaluationRequestExportService {

    byte[] exportAllToExcel(EvaluationRequestStatus statusFilter);

    /**
     * Экспорт одной заявки в Word (docx) — сводный отчёт по клиенту.
     */
    byte[] exportSingleToWord(String requestId);
}

