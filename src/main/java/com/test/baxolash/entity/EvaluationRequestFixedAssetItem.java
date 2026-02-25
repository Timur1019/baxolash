package com.test.baxolash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "evaluation_request_fixed_asset_items")
public class EvaluationRequestFixedAssetItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_request_id", nullable = false)
    private EvaluationRequest evaluationRequest;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_of_measurement", length = 50)
    private String unitOfMeasurement;
}
