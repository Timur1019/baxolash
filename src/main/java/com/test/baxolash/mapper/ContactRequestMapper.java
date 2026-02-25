package com.test.baxolash.mapper;

import com.test.baxolash.dto.ContactRequestCreateDto;
import com.test.baxolash.dto.ContactRequestDto;
import com.test.baxolash.entity.ContactRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactRequestMapper extends BaseMapper<ContactRequest, ContactRequestDto> {

    @Override
    default ContactRequestDto toDto(ContactRequest entity) {
        if (entity == null) return null;
        ContactRequestDto dto = new ContactRequestDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setSubject(entity.getSubject());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    default ContactRequest toEntity(ContactRequestCreateDto dto) {
        if (dto == null) return null;
        ContactRequest entity = new ContactRequest();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setSubject(dto.getSubject());
        entity.setMessage(dto.getMessage());
        return entity;
    }
}
