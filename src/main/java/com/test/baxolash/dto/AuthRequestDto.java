package com.test.baxolash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {

    @NotBlank
    private String login;

    @NotBlank
    private String password;
}

