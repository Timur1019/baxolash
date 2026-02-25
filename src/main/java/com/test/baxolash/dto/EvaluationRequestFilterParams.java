package com.test.baxolash.dto;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Параметры фильтрации заявок на оценку.
 */
@Data
@Builder
public class EvaluationRequestFilterParams {

    private String clientUserId;
    private EvaluationRequestType requestType;
    private EvaluationRequestStatus status;
    private String regionId;
    private String districtId;
    private String search;
    private Instant dateFrom;
    private Instant dateTo;
}
