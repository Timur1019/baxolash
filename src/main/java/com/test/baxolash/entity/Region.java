package com.test.baxolash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "regions")
public class Region extends BaseEntity {

    @Column(name = "name_uz", nullable = false, length = 200)
    private String nameUz;

    @Column(name = "name_ru", length = 200)
    private String nameRu;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
