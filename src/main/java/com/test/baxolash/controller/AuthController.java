package com.test.baxolash.controller;

import com.test.baxolash.dto.AuthRequestDto;
import com.test.baxolash.dto.AuthResponseDto;
import com.test.baxolash.entity.User;
import com.test.baxolash.exception.BusinessException;
import com.test.baxolash.repository.UserRepository;
import com.test.baxolash.security.JwtTokenProvider;
import com.test.baxolash.util.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Авторизация пользователей Baholash")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        String login = request.getLogin().trim();
        String password = request.getPassword();

        // Находим пользователя по логину или email
        User user = userRepository.findByLoginAndDeletedAtIsNull(login)
                .or(() -> userRepository.findByEmailAndDeletedAtIsNull(login))
                .orElseThrow(() -> new BusinessException("Неверный логин или пароль"));

        if (!user.getActive()) throw new BusinessException("Пользователь не активен");
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Неверный логин или пароль");
        }

        // ✅ Используем ЛОГИН (не email) для создания Authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLogin(), password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        boolean canEdit = "ADMIN".equals(user.getRole().name())
                || Boolean.TRUE.equals(user.getCanEditEvaluationRequests());
        boolean canDelete = "ADMIN".equals(user.getRole().name())
                || Boolean.TRUE.equals(user.getCanDeleteEvaluationRequests());
        return ResponseEntity.ok(new AuthResponseDto(token, user.getFullName(), user.getRole().name(), canEdit, canDelete));
    }

}

