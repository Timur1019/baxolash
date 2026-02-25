package com.test.baxolash.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<ENTITY, DTO> {

    DTO toDto(ENTITY entity);

    default List<DTO> toDtoList(List<ENTITY> entities) {
        if (entities == null) {
            return null;
        }
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

