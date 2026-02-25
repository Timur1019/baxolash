package com.test.baxolash.service.impl;

import com.test.baxolash.dto.RegionDto;
import com.test.baxolash.entity.Region;
import com.test.baxolash.repository.RegionRepository;
import com.test.baxolash.service.RegionService;
import com.test.baxolash.util.LogUtil;
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
public class RegionServiceImpl implements RegionService {

    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    @Cacheable("regions")
    public List<RegionDto> findAll() {
        try {
            return regionRepository.findByDeletedAtIsNullOrderBySortOrderAscNameUzAsc().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LogUtil.warn("Failed to load regions (e.g. table not created yet): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private RegionDto toDto(Region r) {
        if (r == null) return null;
        RegionDto dto = new RegionDto();
        dto.setId(r.getId());
        dto.setNameUz(r.getNameUz() != null ? r.getNameUz() : "");
        dto.setNameRu(r.getNameRu());
        dto.setSortOrder(r.getSortOrder());
        return dto;
    }
}
