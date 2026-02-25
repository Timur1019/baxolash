package com.test.baxolash.service.impl;

import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.service.EvaluationRequestExportService;
import com.test.baxolash.util.EvaluationRequestExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationRequestExportServiceImpl implements EvaluationRequestExportService {

    private final EvaluationRequestCrudService crudService;

    @Override
    public byte[] exportAllToExcel(EvaluationRequestStatus statusFilter) {
        Pageable pageable = Pageable.unpaged();
        return EvaluationRequestExportUtil.buildExcelForRequests(
                crudService.getAll(pageable, statusFilter).getContent()
        );
    }

    @Override
    public byte[] exportSingleToWord(String requestId) {
        EvaluationRequestDto dto = crudService.getById(requestId);
        if (dto == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }
        return EvaluationRequestExportUtil.buildWordForRequest(dto);
    }
}

