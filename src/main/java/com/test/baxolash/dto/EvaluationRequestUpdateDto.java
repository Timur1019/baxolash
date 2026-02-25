package com.test.baxolash.dto;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestUpdateDto {

    private EvaluationRequestStatus status;

    @Size(max = 5000)
    private String objectDescription;

    @Size(max = 500)
    private String appraisedObjectName;

    @Size(max = 255)
    private String borrowerName;

    @Size(max = 50)
    private String licensePlate;

    private BigDecimal cost;

    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;

    @Size(max = 500)
    private String locationAddress;

    private String regionId;
    private String districtId;
}
