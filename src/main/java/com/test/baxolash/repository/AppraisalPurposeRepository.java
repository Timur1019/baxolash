package com.test.baxolash.repository;

import com.test.baxolash.entity.AppraisalPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppraisalPurposeRepository extends JpaRepository<AppraisalPurpose, String> {

    List<AppraisalPurpose> findByDeletedAtIsNullOrderBySortOrderAscNameUzAsc();
}
