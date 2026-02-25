package com.test.baxolash.dto;

import com.test.baxolash.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

    @NotBlank
    @Size(max = 100)
    private String login;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String fullName;

    @NotNull
    private UserRole role;

    @NotNull
    private Boolean active;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}

