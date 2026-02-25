package com.test.baxolash.repository;

import com.test.baxolash.entity.EvaluationRequestFixedAssetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRequestFixedAssetItemRepository extends JpaRepository<EvaluationRequestFixedAssetItem, String> {

    List<EvaluationRequestFixedAssetItem> findByEvaluationRequestIdOrderByCreatedAtAsc(String evaluationRequestId);
}
