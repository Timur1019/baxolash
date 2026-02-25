package com.test.baxolash.service.impl;

import com.test.baxolash.dto.AppraisalPurposeDto;
import com.test.baxolash.entity.AppraisalPurpose;
import com.test.baxolash.repository.AppraisalPurposeRepository;
import com.test.baxolash.service.AppraisalPurposeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppraisalPurposeServiceImpl implements AppraisalPurposeService {

    private static final Logger log = LoggerFactory.getLogger(AppraisalPurposeServiceImpl.class);

    private final AppraisalPurposeRepository repository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<AppraisalPurposeDto> findAll() {
        try {
            return repository.findByDeletedAtIsNullOrderBySortOrderAscNameUzAsc().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to load appraisal purposes (e.g. table not created yet): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private AppraisalPurposeDto toDto(AppraisalPurpose e) {
        if (e == null) return null;
        AppraisalPurposeDto dto = new AppraisalPurposeDto();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setNameUz(e.getNameUz());
        dto.setNameRu(e.getNameRu());
        dto.setNameEn(e.getNameEn());
        dto.setSortOrder(e.getSortOrder());
        return dto;
    }
}
