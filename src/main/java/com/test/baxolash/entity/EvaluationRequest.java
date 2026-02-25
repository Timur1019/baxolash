package com.test.baxolash.entity;

import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "evaluation_requests")
public class EvaluationRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id", nullable = false)
    private User clientUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EvaluationRequestStatus status;

    @Column(name = "object_description", columnDefinition = "TEXT")
    private String objectDescription;

    @Column(name = "cost", precision = 19, scale = 2)
    private BigDecimal cost;

    @Column(name = "report_file_name", length = 255)
    private String reportFileName;

    @Column(name = "report_file_url", length = 1024)
    private String reportFileUrl;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cadastral_number", length = 100)
    private String cadastralNumber;

    @Column(name = "appraisal_purpose", length = 100)
    private String appraisalPurpose;

    @Column(name = "owner_phone", length = 30)
    private String ownerPhone;

    @Column(name = "bank_employee_phone", length = 30)
    private String bankEmployeePhone;

    @Column(name = "borrower_inn", length = 50)
    private String borrowerInn;

    @Column(name = "cadastral_document_url", length = 1024)
    private String cadastralDocumentUrl;

    @Column(name = "cadastral_document_file_name", length = 255)
    private String cadastralDocumentFileName;

    @Column(name = "appraised_object_name", length = 500)
    private String appraisedObjectName;

    @Column(name = "borrower_name", length = 255)
    private String borrowerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 50, nullable = false)
    private EvaluationRequestType requestType = EvaluationRequestType.REAL_ESTATE;

    @Column(name = "vehicle_type", length = 100)
    private String vehicleType;

    @Column(name = "tech_passport_number", length = 100)
    private String techPassportNumber;

    @Column(name = "license_plate", length = 50)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private District district;

    @Column(name = "property_owner_name", length = 255)
    private String propertyOwnerName;

    @Column(name = "object_address", length = 500)
    private String objectAddress;

    @Column(name = "latitude", precision = 10, scale = 7)
    private java.math.BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private java.math.BigDecimal longitude;

    @Column(name = "location_address", length = 500)
    private String locationAddress;

    @OneToMany(mappedBy = "evaluationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationRequestFixedAssetItem> fixedAssetItems = new ArrayList<>();
}
