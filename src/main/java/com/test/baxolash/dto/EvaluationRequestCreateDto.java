package com.test.baxolash.dto;

import com.test.baxolash.entity.EvaluationRequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestCreateDto {

    private EvaluationRequestType requestType;

    @Size(max = 5000, message = "Описание не более 5000 символов")
    private String objectDescription;

    @Size(max = 100)
    private String cadastralNumber;

    @Size(max = 100)
    private String appraisalPurpose;

    @Size(max = 30)
    private String ownerPhone;

    @Size(max = 30)
    private String bankEmployeePhone;

    @Size(max = 50)
    private String borrowerInn;

    @Size(max = 500)
    private String appraisedObjectName;

    @Size(max = 255)
    private String borrowerName;

    @Size(max = 36)
    private String regionId;

    @Size(max = 36)
    private String districtId;

    @Size(max = 100)
    private String vehicleType;

    @Size(max = 100)
    private String techPassportNumber;

    @Size(max = 50)
    private String licensePlate;

    @Size(max = 255)
    private String propertyOwnerName;

    @Size(max = 500)
    private String objectAddress;

    @Valid
    private List<FixedAssetItemCreateDto> fixedAssetItems;
}
