package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String fullName;
    private String role;

    /** Может редактировать заявки (для COMPANY_EMPLOYEE, CLIENT_EMPLOYEE). ADMIN — всегда true. */
    private Boolean canEditEvaluationRequests;

    /** Может удалять заявки (для COMPANY_EMPLOYEE, CLIENT_EMPLOYEE). ADMIN — всегда true. */
    private Boolean canDeleteEvaluationRequests;

    public AuthResponseDto(String token, String fullName, String role) {
        this.token = token;
        this.fullName = fullName;
        this.role = role;
        this.canEditEvaluationRequests = true;
        this.canDeleteEvaluationRequests = true;
    }
}

