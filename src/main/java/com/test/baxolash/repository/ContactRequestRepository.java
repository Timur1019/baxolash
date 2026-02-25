package com.test.baxolash.repository;

import com.test.baxolash.entity.ContactRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, String> {

    Page<ContactRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
