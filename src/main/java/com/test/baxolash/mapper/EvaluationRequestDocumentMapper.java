package com.test.baxolash.mapper;

import com.test.baxolash.dto.DocumentItemDto;
import com.test.baxolash.entity.EvaluationRequestDocument;
import com.test.baxolash.entity.enums.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EvaluationRequestDocumentMapper extends BaseMapper<EvaluationRequestDocument, DocumentItemDto> {

    @Override
    default DocumentItemDto toDto(EvaluationRequestDocument entity) {
        if (entity == null) return null;
        var uploadedBy = entity.getUploadedBy();
        DocumentItemDto dto = new DocumentItemDto();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFileUrl(entity.getFileUrl());
        dto.setUploadedByFullName(uploadedBy != null ? uploadedBy.getFullName() : null);
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setFromClient(uploadedBy != null && uploadedBy.getRole() == UserRole.CLIENT_EMPLOYEE);
        return dto;
    }
}
