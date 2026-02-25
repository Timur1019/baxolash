package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDto {
    private String id;
    private String regionId;
    private String nameUz;
    private String nameRu;
    private Integer sortOrder;
}
