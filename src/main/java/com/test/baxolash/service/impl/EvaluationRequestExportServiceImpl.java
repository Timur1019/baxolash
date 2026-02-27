package com.test.baxolash.service.impl;

import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.service.EvaluationRequestExportService;
import com.test.baxolash.util.EvaluationRequestExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationRequestExportServiceImpl implements EvaluationRequestExportService {

    private final EvaluationRequestCrudService crudService;

    // ✅ ПРАВИЛЬНО — использовать Stream или батчевую обработку
    @Override
    public byte[] exportAllToExcel(EvaluationRequestStatus statusFilter) {
        int batchSize = 500;
        int page = 0;
        List<EvaluationRequestDto> all = new ArrayList<>();
        Page<EvaluationRequestDto> batch;
        do {
            batch = crudService.getAll(PageRequest.of(page++, batchSize), statusFilter);
            all.addAll(batch.getContent());
        } while (batch.hasNext());
        return EvaluationRequestExportUtil.buildExcelForRequests(all);
    }


    @Override
    public byte[] exportSingleToWord(String requestId) {
        EvaluationRequestDto dto = crudService.getById(requestId);
        if (dto == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        return EvaluationRequestExportUtil.buildWordForRequest(dto);
    }

    @Override
    public byte[] exportSingleToPdf(String requestId) {
        EvaluationRequestDto dto = crudService.getById(requestId);
        if (dto == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        return EvaluationRequestExportUtil.buildPdfForRequest(dto);
    }
}

