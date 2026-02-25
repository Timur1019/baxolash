package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppraisalPurposeDto {
    private String id;
    private String code;
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private Integer sortOrder;
}
