package com.test.baxolash.dto;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDto {

    private String id;
    private String clientUserId;
    private String clientFullName;
    private String clientEmail;
    private EvaluationRequestStatus status;
    private String objectDescription;
    private BigDecimal cost;
    private String reportFileName;
    private Boolean hasReportFile;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;
    /** Список документов (имя файла + можно ли скачать) */
    private List<DocumentItemDto> documents;

    private String cadastralNumber;
    private String appraisalPurpose;
    private String ownerPhone;
    private String bankEmployeePhone;
    private String borrowerInn;
    private String appraisedObjectName;
    private String borrowerName;
    private String regionId;
    private String regionNameUz;
    private String districtId;
    private String districtNameUz;

    private EvaluationRequestType requestType;
    private String vehicleType;
    private String techPassportNumber;
    private String licensePlate;

    private String propertyOwnerName;
    private String objectAddress;

    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private String locationAddress;

    private List<FixedAssetItemDto> fixedAssetItems;
}
