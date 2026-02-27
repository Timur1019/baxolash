package com.test.baxolash.mapper;

import com.test.baxolash.dto.UserCreateDto;
import com.test.baxolash.dto.UserDto;
import com.test.baxolash.entity.User;
import com.test.baxolash.entity.enums.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {

    @Override
    default UserDto toDto(User entity) {
        if (entity == null) return null;
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setLogin(entity.getLogin());
        dto.setEmail(entity.getEmail());
        dto.setFullName(entity.getFullName());
        dto.setRole(entity.getRole() != null ? entity.getRole().name() : null);
        dto.setActive(entity.getActive());
        dto.setCanEditEvaluationRequests(entity.getCanEditEvaluationRequests() != null ? entity.getCanEditEvaluationRequests() : true);
        dto.setCanDeleteEvaluationRequests(entity.getCanDeleteEvaluationRequests() != null ? entity.getCanDeleteEvaluationRequests() : true);
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    default User toEntity(UserCreateDto dto) {
        if (dto == null) return null;
        User entity = new User();
        entity.setLogin(dto.getLogin());
        entity.setEmail(dto.getEmail());
        entity.setFullName(dto.getFullName());
        entity.setRole(dto.getRole());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setCanEditEvaluationRequests(dto.getCanEditEvaluationRequests() != null ? dto.getCanEditEvaluationRequests() : true);
        entity.setCanDeleteEvaluationRequests(dto.getCanDeleteEvaluationRequests() != null ? dto.getCanDeleteEvaluationRequests() : true);
        entity.setPasswordHash(dto.getPassword() != null ? dto.getPassword() : ""); // сервис перезапишет хешем
        return entity;
    }

    default void updateEntity(User existing, UserDto dto) {
        if (existing == null || dto == null) return;
        existing.setLogin(dto.getLogin());
        existing.setEmail(dto.getEmail());
        existing.setFullName(dto.getFullName());
        if (dto.getRole() != null) {
            try {
                existing.setRole(UserRole.valueOf(dto.getRole()));
            } catch (IllegalArgumentException ignored) { /* оставляем текущее значение */ }
        }
        existing.setActive(dto.getActive() != null ? dto.getActive() : true);
        if (dto.getCanEditEvaluationRequests() != null) existing.setCanEditEvaluationRequests(dto.getCanEditEvaluationRequests());
        if (dto.getCanDeleteEvaluationRequests() != null) existing.setCanDeleteEvaluationRequests(dto.getCanDeleteEvaluationRequests());
    }
}
