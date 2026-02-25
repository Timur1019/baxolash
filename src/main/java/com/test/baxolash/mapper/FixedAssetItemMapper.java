package com.test.baxolash.mapper;

import com.test.baxolash.dto.FixedAssetItemCreateDto;
import com.test.baxolash.dto.FixedAssetItemDto;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.EvaluationRequestFixedAssetItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FixedAssetItemMapper {

    public FixedAssetItemDto toDto(EvaluationRequestFixedAssetItem entity) {
        if (entity == null) return null;
        return FixedAssetItemDto.builder()
                .id(entity.getId())
                .assetType(entity.getAssetType())
                .name(entity.getName())
                .quantity(entity.getQuantity())
                .unitOfMeasurement(entity.getUnitOfMeasurement())
                .build();
    }

    public EvaluationRequestFixedAssetItem toEntity(FixedAssetItemCreateDto dto, EvaluationRequest request) {
        if (dto == null || request == null) return null;
        EvaluationRequestFixedAssetItem entity = new EvaluationRequestFixedAssetItem();
        entity.setEvaluationRequest(request);
        entity.setAssetType(dto.getAssetType());
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitOfMeasurement(dto.getUnitOfMeasurement());
        return entity;
    }

    public List<FixedAssetItemDto> toDtoList(List<EvaluationRequestFixedAssetItem> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
