package com.test.baxolash.repository;

import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class EvaluationRequestSpecs {

    private EvaluationRequestSpecs() {
    }

    public static Specification<EvaluationRequest> forClientWithFilters(
            String clientUserId,
            EvaluationRequestType requestType,
            EvaluationRequestStatus status,
            String regionId,
            String districtId,
            String search,
            Instant dateFrom,
            Instant dateTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("clientUser").get("id"), clientUserId));
            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (regionId != null && !regionId.isBlank()) {
                predicates.add(cb.equal(root.get("region").get("id"), regionId));
            }
            if (districtId != null && !districtId.isBlank()) {
                predicates.add(cb.equal(root.get("district").get("id"), districtId));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate cadastral = cb.like(cb.lower(cb.coalesce(root.get("cadastralNumber"), "")), pattern);
                Predicate borrowerInn = cb.like(cb.lower(cb.coalesce(root.get("borrowerInn"), "")), pattern);
                Predicate objectName = cb.like(cb.lower(cb.coalesce(root.get("appraisedObjectName"), "")), pattern);
                Predicate borrowerName = cb.like(cb.lower(cb.coalesce(root.get("borrowerName"), "")), pattern);
                Predicate description = cb.like(cb.lower(cb.coalesce(root.get("objectDescription"), "")), pattern);
                Predicate licensePlate = cb.like(cb.lower(cb.coalesce(root.get("licensePlate"), "")), pattern);
                Predicate techPassport = cb.like(cb.lower(cb.coalesce(root.get("techPassportNumber"), "")), pattern);
                Predicate propertyOwner = cb.like(cb.lower(cb.coalesce(root.get("propertyOwnerName"), "")), pattern);
                Predicate objectAddress = cb.like(cb.lower(cb.coalesce(root.get("objectAddress"), "")), pattern);
                predicates.add(cb.or(cadastral, borrowerInn, objectName, borrowerName, description, licensePlate, techPassport, propertyOwner, objectAddress));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
