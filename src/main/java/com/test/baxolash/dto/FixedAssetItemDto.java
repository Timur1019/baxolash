package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetItemDto {

    private String id;
    private String assetType;
    private String name;
    private BigDecimal quantity;
    private String unitOfMeasurement;
}
