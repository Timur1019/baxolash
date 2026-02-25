package com.test.baxolash.service;

import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.EvaluationRequestDocument;
import com.test.baxolash.entity.EvaluationRequestFixedAssetItem;
import com.test.baxolash.entity.User;
import com.test.baxolash.mapper.EvaluationRequestDocumentMapper;
import com.test.baxolash.mapper.EvaluationRequestMapper;
import com.test.baxolash.mapper.FixedAssetItemMapper;
import com.test.baxolash.repository.EvaluationRequestDocumentRepository;
import com.test.baxolash.repository.EvaluationRequestFixedAssetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обогащение EvaluationRequestDto дополнительными данными (клиент, документы, позиции основных средств).
 */
@Component
@RequiredArgsConstructor
public class EvaluationRequestDtoEnricher {

    private final EvaluationRequestMapper mapper;
    private final EvaluationRequestDocumentMapper documentMapper;
    private final FixedAssetItemMapper fixedAssetItemMapper;
    private final EvaluationRequestDocumentRepository documentRepository;
    private final EvaluationRequestFixedAssetItemRepository fixedAssetItemRepository;

    public EvaluationRequestDto enrich(EvaluationRequest entity) {
        if (entity == null) return null;
        EvaluationRequestDto dto = mapper.toDto(entity);
        enrichWithClientInfo(dto, entity.getClientUser());
        enrichWithDocuments(dto, entity.getId());
        enrichWithFixedAssetItems(dto, entity.getId());
        return dto;
    }

    private void enrichWithClientInfo(EvaluationRequestDto dto, User client) {
        if (client != null) {
            dto.setClientFullName(client.getFullName() != null ? client.getFullName() : "");
            dto.setClientEmail(client.getEmail() != null ? client.getEmail() : "");
        }
    }

    private void enrichWithDocuments(EvaluationRequestDto dto, String entityId) {
        List<EvaluationRequestDocument> docs = entityId != null
                ? documentRepository.findByEvaluationRequestIdOrderByCreatedAtAsc(entityId)
                : List.of();
        dto.setDocuments(docs.stream().map(documentMapper::toDto).toList());
    }

    private void enrichWithFixedAssetItems(EvaluationRequestDto dto, String entityId) {
        List<EvaluationRequestFixedAssetItem> items = entityId != null
                ? fixedAssetItemRepository.findByEvaluationRequestIdOrderByCreatedAtAsc(entityId)
                : List.of();
        dto.setFixedAssetItems(fixedAssetItemMapper.toDtoList(items));
    }
}
