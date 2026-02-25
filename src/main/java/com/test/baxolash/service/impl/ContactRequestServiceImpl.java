package com.test.baxolash.service.impl;

import com.test.baxolash.dto.ContactRequestCreateDto;
import com.test.baxolash.dto.ContactRequestDto;
import com.test.baxolash.entity.ContactRequest;
import com.test.baxolash.mapper.ContactRequestMapper;
import com.test.baxolash.repository.ContactRequestRepository;
import com.test.baxolash.service.ContactRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactRequestServiceImpl implements ContactRequestService {

    private final ContactRequestRepository repository;
    private final ContactRequestMapper mapper;

    @Override
    @Transactional
    public ContactRequestDto create(ContactRequestCreateDto dto) {
        if (dto == null || dto.getName() == null || dto.getEmail() == null || dto.getMessage() == null) {
            throw new IllegalArgumentException("Имя, email и сообщение обязательны");
        }
        ContactRequest entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactRequestDto> getAll(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
