package com.test.baxolash.service.impl;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.EvaluationRequestFilterParams;
import com.test.baxolash.dto.EvaluationRequestUpdateDto;
import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import com.test.baxolash.entity.User;
import com.test.baxolash.service.EvaluationRequestAccessValidator;
import com.test.baxolash.service.EvaluationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

/**
 * Фасад заявок на оценку: объединяет CRUD, документы, отчёты и проверку прав доступа.
 */
@Service
@Primary
@RequiredArgsConstructor
public class EvaluationRequestFacade implements EvaluationRequestService {

    private final EvaluationRequestAccessValidator accessValidator;
    private final EvaluationRequestCrudService crudService;
    private final EvaluationRequestDocumentService documentService;
    private final EvaluationRequestReportService reportService;

    @Override
    @Transactional
    public EvaluationRequestDto create(EvaluationRequestCreateDto dto) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateClientCanCreate(current);
        return crudService.create(dto, current);
    }

    @Override
    @Transactional
    public EvaluationRequestDto createWithDocument(EvaluationRequestCreateDto dto, MultipartFile cadastralDocument,
                                                   MultipartFile techPassportDocument, MultipartFile fixedAssetsDocument) {
        EvaluationRequestDto created = create(dto);
        MultipartFile fileToUpload = resolveDocumentForRequestType(dto, cadastralDocument, techPassportDocument, fixedAssetsDocument);
        if (fileToUpload != null && !fileToUpload.isEmpty()) {
            User current = accessValidator.getCurrentUser();
            accessValidator.validateCanUploadDocument(current, created.getId());
            documentService.uploadDocument(created.getId(), fileToUpload, current);
            return crudService.getById(created.getId());
        }
        return created;
    }

    private static MultipartFile resolveDocumentForRequestType(EvaluationRequestCreateDto dto,
                                                               MultipartFile cadastral, MultipartFile techPassport, MultipartFile fixedAssets) {
        if (dto == null) return cadastral;
        return switch (dto.getRequestType() != null ? dto.getRequestType() : EvaluationRequestType.REAL_ESTATE) {
            case VEHICLE -> techPassport;
            case FIXED_ASSETS -> fixedAssets;
            default -> cadastral;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvaluationRequestDto> getMy(Pageable pageable) {
        return getMy(pageable, null, null, null, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvaluationRequestDto> getMy(Pageable pageable, EvaluationRequestType requestType, EvaluationRequestStatus status,
                                            String regionId, String districtId, String search, Instant dateFrom, Instant dateTo) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateClientOnly(current);
        var params = EvaluationRequestFilterParams.builder()
                .clientUserId(current.getId())
                .requestType(requestType)
                .status(status)
                .regionId(regionId)
                .districtId(districtId)
                .search(search)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();
        return crudService.getMy(params, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvaluationRequestDto> getAll(Pageable pageable, EvaluationRequestStatus statusFilter) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCompanyOrAdmin(current);
        return crudService.getAll(pageable, statusFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationRequestDto getById(String id) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateAccessToRequestId(current, id);
        return crudService.getById(id);
    }

    @Override
    @Transactional
    public EvaluationRequestDto update(String id, EvaluationRequestUpdateDto dto) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanUpdate(current);
        accessValidator.validateAccessToRequestId(current, id);
        return crudService.update(id, dto);
    }

    @Override
    @Transactional
    public void uploadDocument(String requestId, MultipartFile file) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanUploadDocument(current, requestId);
        documentService.uploadDocument(requestId, file, current);
    }

    @Override
    @Transactional
    public void uploadReport(String requestId, MultipartFile file) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanUploadReport(current);
        accessValidator.validateAccessToRequestId(current, requestId);
        reportService.uploadReport(requestId, file);
    }

    @Override
    @Transactional
    public void confirmCompletion(String requestId) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanConfirmCompletion(current);
        accessValidator.validateAccessToRequestId(current, requestId);
        reportService.confirmCompletion(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDownloadResult downloadReport(String requestId) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateAccessToRequestId(current, requestId);
        return reportService.downloadReport(requestId);
    }

    @Override
    @Transactional
    public void deleteReport(String requestId) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanUploadReport(current);
        accessValidator.validateAccessToRequestId(current, requestId);
        reportService.deleteReport(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDownloadResult downloadDocument(String documentId) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateAccessToDocument(current, documentId);
        return documentService.downloadDocument(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationRequestStatus> getStatusesForFilter() {
        return crudService.getStatusesForFilter();
    }
}
