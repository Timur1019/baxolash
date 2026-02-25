package com.test.baxolash.service.impl;

import com.test.baxolash.dto.UserCreateDto;
import com.test.baxolash.dto.UserDto;
import com.test.baxolash.entity.User;
import com.test.baxolash.entity.enums.UserRole;
import com.test.baxolash.exception.BusinessException;
import com.test.baxolash.exception.NotFoundException;
import com.test.baxolash.mapper.UserMapper;
import com.test.baxolash.repository.UserRepository;
import com.test.baxolash.service.UserService;
import com.test.baxolash.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(Pageable pageable) {
        LogUtil.info("Fetching users with pageable: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(Pageable pageable, String search, UserRole role, Boolean active) {
        LogUtil.info("Fetching users with filters search={}, role={}, active={}, pageable={}",
                search, role, active, pageable);

        Specification<User> spec = null;

        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = (root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("login").as(String.class)), searchPattern),
                            cb.like(cb.lower(root.get("email").as(String.class)), searchPattern),
                            cb.like(cb.lower(root.get("fullName").as(String.class)), searchPattern)
                    );
        }

        if (role != null) {
            Specification<User> roleSpec = (root, query, cb) -> cb.equal(root.get("role"), role);
            spec = spec == null ? roleSpec : spec.and(roleSpec);
        }

        if (active != null) {
            Specification<User> activeSpec = (root, query, cb) -> cb.equal(root.get("active"), active);
            spec = spec == null ? activeSpec : spec.and(activeSpec);
        }

        if (spec == null) {
            return userRepository.findAll(pageable).map(userMapper::toDto);
        }

        return userRepository.findAll(spec, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(String id) {
        LogUtil.info("Fetching user by id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserCreateDto userDto) {
        LogUtil.info("Creating user with login={}", userDto.getLogin());

        if (userRepository.existsByLogin(userDto.getLogin())) {
            throw new BusinessException("User with the same login already exists");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("User with the same email already exists");
        }

        User user = userMapper.toEntity(userDto);
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        LogUtil.info("Updating user id={}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!existing.getLogin().equals(userDto.getLogin()) && userRepository.existsByLogin(userDto.getLogin())) {
            throw new BusinessException("User with the same login already exists");
        }
        if (!existing.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("User with the same email already exists");
        }

        userMapper.updateEntity(existing, userDto);
        User saved = userRepository.save(existing);
        return userMapper.toDto(saved);
    }

    @Override
    public void deleteUser(String id) {
        LogUtil.info("Deleting user id={}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}

