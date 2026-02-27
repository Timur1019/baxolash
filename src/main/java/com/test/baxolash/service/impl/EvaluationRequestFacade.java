package com.test.baxolash.service.impl;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.EvaluationRequestFilterParams;
import com.test.baxolash.dto.EvaluationRequestUpdateDto;
import com.test.baxolash.entity.EvaluationRequestType;
import com.test.baxolash.entity.enums.UserRole;
import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
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
    public EvaluationRequestDto createWithDocument(
            EvaluationRequestCreateDto dto,
            MultipartFile cadastralDocument,
            MultipartFile techPassportDocument,
            MultipartFile fixedAssetsDocument) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateClientCanCreate(current);
        EvaluationRequestDto created = crudService.create(dto, current);
        MultipartFile fileToUpload = resolveDocumentForRequestType(
                dto,
                cadastralDocument,
                techPassportDocument,
                fixedAssetsDocument
        );
        if (fileToUpload != null && !fileToUpload.isEmpty()) {
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
        accessValidator.validateCanUpdate(current, id);
        EvaluationRequestDto existing = crudService.getById(id);
        EvaluationRequestType requestType = existing.getRequestType() != null
                ? existing.getRequestType() : EvaluationRequestType.REAL_ESTATE;
        EvaluationRequestUpdateDto filtered = filterUpdateDtoByRole(current, dto, requestType);
        return crudService.update(id, filtered);
    }

    /**
     * Клиент редактирует только поля, которые заполняет при создании (по типу заявки).
     * Сотрудник компании — только поля, которые заполняет при работе (статус, стоимость, объект, заёмщик и т.д.).
     */
    private EvaluationRequestUpdateDto filterUpdateDtoByRole(User current, EvaluationRequestUpdateDto dto,
                                                             EvaluationRequestType requestType) {
        if (dto == null) return null;
        if (current.getRole() == UserRole.CLIENT_EMPLOYEE) {
            EvaluationRequestUpdateDto filtered = new EvaluationRequestUpdateDto();
            if (requestType == EvaluationRequestType.REAL_ESTATE) {
                filtered.setCadastralNumber(dto.getCadastralNumber());
                filtered.setAppraisalPurpose(dto.getAppraisalPurpose());
                filtered.setOwnerPhone(dto.getOwnerPhone());
                filtered.setBankEmployeePhone(dto.getBankEmployeePhone());
                filtered.setBorrowerInn(dto.getBorrowerInn());
                filtered.setRegionId(dto.getRegionId());
                filtered.setDistrictId(dto.getDistrictId());
            } else if (requestType == EvaluationRequestType.VEHICLE) {
                filtered.setVehicleType(dto.getVehicleType());
                filtered.setTechPassportNumber(dto.getTechPassportNumber());
                filtered.setLicensePlate(dto.getLicensePlate());
                filtered.setBorrowerInn(dto.getBorrowerInn());
                filtered.setAppraisalPurpose(dto.getAppraisalPurpose());
                filtered.setOwnerPhone(dto.getOwnerPhone());
                filtered.setBankEmployeePhone(dto.getBankEmployeePhone());
            } else if (requestType == EvaluationRequestType.FIXED_ASSETS) {
                filtered.setPropertyOwnerName(dto.getPropertyOwnerName());
                filtered.setObjectAddress(dto.getObjectAddress());
                filtered.setOwnerPhone(dto.getOwnerPhone());
                filtered.setBankEmployeePhone(dto.getBankEmployeePhone());
                filtered.setAppraisalPurpose(dto.getAppraisalPurpose());
                filtered.setBorrowerInn(dto.getBorrowerInn());
            }
            return filtered;
        }
        if (current.getRole() == UserRole.ADMIN || current.getRole() == UserRole.COMPANY_EMPLOYEE) {
            EvaluationRequestUpdateDto filtered = new EvaluationRequestUpdateDto();
            filtered.setStatus(dto.getStatus());
            filtered.setObjectDescription(dto.getObjectDescription());
            filtered.setAppraisedObjectName(dto.getAppraisedObjectName());
            filtered.setBorrowerName(dto.getBorrowerName());
            filtered.setLicensePlate(dto.getLicensePlate());
            filtered.setCost(dto.getCost());
            filtered.setLatitude(dto.getLatitude());
            filtered.setLongitude(dto.getLongitude());
            filtered.setLocationAddress(dto.getLocationAddress());
            filtered.setRegionId(dto.getRegionId());
            filtered.setDistrictId(dto.getDistrictId());
            return filtered;
        }
        return dto;
    }

    @Override
    @Transactional
    public void delete(String id) {
        User current = accessValidator.getCurrentUser();
        accessValidator.validateCanDeleteWithRequestId(current, id);
        crudService.delete(id);
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
