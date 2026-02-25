package com.test.baxolash.service.impl;

import com.test.baxolash.dto.DistrictDto;
import com.test.baxolash.entity.District;
import com.test.baxolash.repository.DistrictRepository;
import com.test.baxolash.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private static final Logger log = LoggerFactory.getLogger(DistrictServiceImpl.class);

    private final DistrictRepository districtRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    @Cacheable(value = "districts", key = "#regionId")
    public List<DistrictDto> findByRegionId(String regionId) {
        if (regionId == null || regionId.isBlank()) {
            return List.of();
        }
        try {
            return districtRepository.findByRegion_IdAndDeletedAtIsNullOrderBySortOrderAscNameUzAsc(regionId).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to load districts for region {}: {}", regionId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private DistrictDto toDto(District d) {
        if (d == null) return null;
        DistrictDto dto = new DistrictDto();
        dto.setId(d.getId());
        dto.setRegionId(d.getRegion() != null ? d.getRegion().getId() : null);
        dto.setNameUz(d.getNameUz());
        dto.setNameRu(d.getNameRu());
        dto.setSortOrder(d.getSortOrder());
        return dto;
    }
}
