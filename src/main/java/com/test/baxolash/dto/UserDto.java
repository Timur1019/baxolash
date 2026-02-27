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

    /** Может редактировать заявки на оценку (назначает админ). */
    private Boolean canEditEvaluationRequests;

    /** Может удалять заявки на оценку (назначает админ). */
    private Boolean canDeleteEvaluationRequests;

    /** Дата регистрации (created_at сущности). */
    private Instant createdAt;
}

