package com.test.baxolash.service;

import com.test.baxolash.dto.ContactRequestCreateDto;
import com.test.baxolash.dto.ContactRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactRequestService {

    ContactRequestDto create(ContactRequestCreateDto dto);

    Page<ContactRequestDto> getAll(Pageable pageable);

    void deleteById(String id);

    void deleteAll();
}
