package com.test.baxolash.mapper;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EvaluationRequestMapper extends BaseMapper<EvaluationRequest, EvaluationRequestDto> {

    @Override
    default EvaluationRequestDto toDto(EvaluationRequest entity) {
        if (entity == null) return null;
        EvaluationRequestDto dto = new EvaluationRequestDto();
        dto.setId(entity.getId());
        if (entity.getClientUser() != null) {
            dto.setClientUserId(entity.getClientUser().getId());
            dto.setClientFullName(entity.getClientUser().getFullName());
            dto.setClientEmail(entity.getClientUser().getEmail());
        }
        dto.setStatus(entity.getStatus());
        dto.setObjectDescription(entity.getObjectDescription());
        dto.setCost(entity.getCost());
        dto.setReportFileName(entity.getReportFileName());
        dto.setHasReportFile(entity.getReportFileUrl() != null && !entity.getReportFileUrl().isEmpty());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCadastralNumber(entity.getCadastralNumber());
        dto.setAppraisalPurpose(entity.getAppraisalPurpose());
        dto.setOwnerPhone(entity.getOwnerPhone());
        dto.setBankEmployeePhone(entity.getBankEmployeePhone());
        dto.setBorrowerInn(entity.getBorrowerInn());
        dto.setAppraisedObjectName(entity.getAppraisedObjectName());
        dto.setBorrowerName(entity.getBorrowerName());
        if (entity.getRegion() != null) {
            dto.setRegionId(entity.getRegion().getId());
            dto.setRegionNameUz(entity.getRegion().getNameUz());
        }
        if (entity.getDistrict() != null) {
            dto.setDistrictId(entity.getDistrict().getId());
            dto.setDistrictNameUz(entity.getDistrict().getNameUz());
        }
        dto.setRequestType(entity.getRequestType() != null ? entity.getRequestType() : EvaluationRequestType.REAL_ESTATE);
        dto.setVehicleType(entity.getVehicleType());
        dto.setTechPassportNumber(entity.getTechPassportNumber());
        dto.setLicensePlate(entity.getLicensePlate());
        dto.setPropertyOwnerName(entity.getPropertyOwnerName());
        dto.setObjectAddress(entity.getObjectAddress());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setLocationAddress(entity.getLocationAddress());
        return dto;
    }

    default EvaluationRequest toEntity(EvaluationRequestCreateDto dto) {
        if (dto == null) return null;
        EvaluationRequest entity = new EvaluationRequest();
        entity.setRequestType(dto.getRequestType() != null ? dto.getRequestType() : EvaluationRequestType.REAL_ESTATE);
        entity.setObjectDescription(dto.getObjectDescription());
        entity.setStatus(EvaluationRequestStatus.NOT_REVIEWED);
        entity.setCadastralNumber(dto.getCadastralNumber());
        entity.setAppraisalPurpose(dto.getAppraisalPurpose());
        entity.setOwnerPhone(dto.getOwnerPhone());
        entity.setBankEmployeePhone(dto.getBankEmployeePhone());
        entity.setBorrowerInn(dto.getBorrowerInn());
        entity.setAppraisedObjectName(dto.getAppraisedObjectName());
        entity.setBorrowerName(dto.getBorrowerName());
        entity.setVehicleType(dto.getVehicleType());
        entity.setTechPassportNumber(dto.getTechPassportNumber());
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setPropertyOwnerName(dto.getPropertyOwnerName());
        entity.setObjectAddress(dto.getObjectAddress());
        return entity;
    }
}
