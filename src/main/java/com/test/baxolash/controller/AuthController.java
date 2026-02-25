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
    @Operation(summary = "Вход в систему по логину и паролю")
    @ApiResponse(
            responseCode = "200",
            description = "Успешная аутентификация",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
    )
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        String login = request.getLogin() != null ? request.getLogin().trim() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";
        if (login.isEmpty() || password.isEmpty()) {
            throw new BusinessException("Неверный логин или пароль");
        }
        LogUtil.info("Login attempt for user={}", login);

        // Вход по логину или по email (одно поле на фронте)
        User user = userRepository.findByLoginAndDeletedAtIsNull(login)
                .or(() -> userRepository.findByEmailAndDeletedAtIsNull(login))
                .orElseThrow(() -> new BusinessException("Неверный логин или пароль"));

        if (!user.getActive()) {
            throw new BusinessException("Пользователь не активен");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Неверный логин или пароль");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        AuthResponseDto response = new AuthResponseDto(
                token,
                user.getFullName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}

