package com.test.baxolash.repository;

import com.test.baxolash.entity.EvaluationRequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRequestDocumentRepository extends JpaRepository<EvaluationRequestDocument, String> {

    @Query("SELECT d FROM EvaluationRequestDocument d LEFT JOIN FETCH d.uploadedBy WHERE d.evaluationRequest.id = :requestId ORDER BY d.createdAt ASC")
    List<EvaluationRequestDocument> findByEvaluationRequestIdOrderByCreatedAtAsc(@Param("requestId") String evaluationRequestId);
}
