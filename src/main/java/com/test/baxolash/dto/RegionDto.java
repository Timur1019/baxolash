package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {
    private String id;
    private String nameUz;
    private String nameRu;
    private Integer sortOrder;
}
