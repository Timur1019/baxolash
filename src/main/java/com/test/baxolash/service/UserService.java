package com.test.baxolash.service;

import com.test.baxolash.dto.UserCreateDto;
import com.test.baxolash.dto.UserDto;
import com.test.baxolash.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserDto> getUsers(Pageable pageable);

    Page<UserDto> getUsers(Pageable pageable, String search, UserRole role, Boolean active);

    UserDto getUser(String id);

    UserDto createUser(UserCreateDto userDto);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String id);
}

