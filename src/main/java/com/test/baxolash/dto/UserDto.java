package com.test.baxolash.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {

    private String id;

    private String login;

    private String email;

    private String fullName;

    private String role;

    private Boolean active;

    /** Дата регистрации (created_at сущности). */
    private Instant createdAt;
}

