package com.test.baxolash.service.impl;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.EvaluationRequestFilterParams;
import com.test.baxolash.dto.EvaluationRequestUpdateDto;
import com.test.baxolash.dto.FixedAssetItemCreateDto;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.EvaluationRequestFixedAssetItem;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import com.test.baxolash.entity.User;
import com.test.baxolash.exception.NotFoundException;
import com.test.baxolash.mapper.EvaluationRequestMapper;
import com.test.baxolash.mapper.FixedAssetItemMapper;
import com.test.baxolash.repository.EvaluationRequestRepository;
import com.test.baxolash.repository.RegionRepository;
import com.test.baxolash.repository.DistrictRepository;
import com.test.baxolash.service.EvaluationRequestDtoEnricher;
import com.test.baxolash.service.EvaluationRequestFilterService;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD операции с заявками на оценку (без проверки прав доступа).
 */
@Service
@RequiredArgsConstructor
public class EvaluationRequestCrudService {

    private final EvaluationRequestRepository requestRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final EvaluationRequestMapper mapper;
    private final FixedAssetItemMapper fixedAssetItemMapper;
    private final EvaluationRequestDtoEnricher enricher;
    private final EvaluationRequestFilterService filterService;

    @Transactional
    public EvaluationRequestDto create(EvaluationRequestCreateDto dto, User clientUser) {
        EvaluationRequest entity = mapper.toEntity(dto != null ? dto : new EvaluationRequestCreateDto());
        entity.setClientUser(clientUser);

        if (dto != null && EvaluationRequestType.REAL_ESTATE == (dto.getRequestType() != null ? dto.getRequestType() : EvaluationRequestType.REAL_ESTATE)) {
            if (dto.getRegionId() != null && !dto.getRegionId().isBlank()) {
                entity.setRegion(regionRepository.findById(dto.getRegionId()).orElse(null));
            }
            if (dto.getDistrictId() != null && !dto.getDistrictId().isBlank()) {
                entity.setDistrict(districtRepository.findById(dto.getDistrictId()).orElse(null));
            }
        }

        if (dto != null && EvaluationRequestType.FIXED_ASSETS == (dto.getRequestType() != null ? dto.getRequestType() : EvaluationRequestType.REAL_ESTATE)
                && dto.getFixedAssetItems() != null && !dto.getFixedAssetItems().isEmpty()) {
            for (FixedAssetItemCreateDto itemDto : dto.getFixedAssetItems()) {
                EvaluationRequestFixedAssetItem item = fixedAssetItemMapper.toEntity(itemDto, entity);
                if (item != null) {
                    entity.getFixedAssetItems().add(item);
                }
            }
        }

        entity = requestRepository.save(entity);
        LogUtil.info("Evaluation request created: id={}, client={}", entity.getId(), clientUser.getLogin());
        return enricher.enrich(entity);
    }

    @Transactional(readOnly = true)
    public EvaluationRequestDto getById(String id) {
        EvaluationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        return enricher.enrich(entity);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationRequestDto> getMy(EvaluationRequestFilterParams filterParams, Pageable pageable) {
        return requestRepository.findAll(filterService.buildClientFilterSpec(filterParams), pageable)
                .map(enricher::enrich);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationRequestDto> getAll(Pageable pageable, EvaluationRequestStatus statusFilter) {
        Page<EvaluationRequest> page = statusFilter == null
                ? requestRepository.findAllByOrderByCreatedAtDesc(pageable)
                : requestRepository.findByStatusOrderByCreatedAtDesc(statusFilter, pageable);
        return page.map(enricher::enrich);
    }

    @Transactional
    public EvaluationRequestDto update(String id, EvaluationRequestUpdateDto dto) {
        EvaluationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getObjectDescription() != null) entity.setObjectDescription(dto.getObjectDescription());
        if (dto.getAppraisedObjectName() != null) entity.setAppraisedObjectName(dto.getAppraisedObjectName());
        if (dto.getBorrowerName() != null) entity.setBorrowerName(dto.getBorrowerName());
        if (dto.getLicensePlate() != null) entity.setLicensePlate(dto.getLicensePlate());
        if (dto.getCost() != null) entity.setCost(dto.getCost());
        if (dto.getLatitude() != null) entity.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) entity.setLongitude(dto.getLongitude());
        if (dto.getLocationAddress() != null) entity.setLocationAddress(dto.getLocationAddress());
        if (dto.getRegionId() != null) {
            entity.setRegion(dto.getRegionId().isBlank() ? null : regionRepository.findById(dto.getRegionId()).orElse(null));
        }
        if (dto.getDistrictId() != null) {
            entity.setDistrict(dto.getDistrictId().isBlank() ? null : districtRepository.findById(dto.getDistrictId()).orElse(null));
        }
        entity = requestRepository.save(entity);
        LogUtil.info("Evaluation request updated: id={}", id);
        return enricher.enrich(entity);
    }

    @Transactional(readOnly = true)
    @Cacheable("evaluation-statuses")
    public List<EvaluationRequestStatus> getStatusesForFilter() {
        return List.of(
                EvaluationRequestStatus.NOT_REVIEWED,
                EvaluationRequestStatus.ASSIGNED_TO_APPRAISER,
                EvaluationRequestStatus.IN_APPRAISAL,
                EvaluationRequestStatus.APPROVED,
                EvaluationRequestStatus.CANCELLED,
                EvaluationRequestStatus.NOT_READY,
                EvaluationRequestStatus.IN_IDENTIFICATION
        );
    }
}
