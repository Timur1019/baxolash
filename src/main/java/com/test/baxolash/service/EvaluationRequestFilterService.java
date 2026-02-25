package com.test.baxolash.service;

import com.test.baxolash.dto.EvaluationRequestFilterParams;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.repository.EvaluationRequestSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationRequestFilterService {

    public Specification<EvaluationRequest> buildClientFilterSpec(EvaluationRequestFilterParams params) {
        if (params == null || params.getClientUserId() == null) {
            throw new IllegalArgumentException("clientUserId обязателен для фильтра клиента");
        }
        return EvaluationRequestSpecs.forClientWithFilters(
                params.getClientUserId(),
                params.getRequestType(),
                params.getStatus(),
                params.getRegionId(),
                params.getDistrictId(),
                params.getSearch(),
                params.getDateFrom(),
                params.getDateTo()
        );
    }
}
