package com.test.baxolash.repository;

import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequest, String>, JpaSpecificationExecutor<EvaluationRequest> {

    Page<EvaluationRequest> findByClientUser_IdOrderByCreatedAtDesc(String clientUserId, Pageable pageable);

    Page<EvaluationRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<EvaluationRequest> findByStatusOrderByCreatedAtDesc(EvaluationRequestStatus status, Pageable pageable);
}
