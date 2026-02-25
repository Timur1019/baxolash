package com.test.baxolash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appraisal_purposes")
public class AppraisalPurpose extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name_uz", nullable = false, length = 255)
    private String nameUz;

    @Column(name = "name_ru", length = 255)
    private String nameRu;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
