package com.test.baxolash.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetItemCreateDto {

    @NotBlank
    @Size(max = 50)
    private String assetType;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    @DecimalMin("0")
    private BigDecimal quantity;

    @Size(max = 50)
    private String unitOfMeasurement;
}
