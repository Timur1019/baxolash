package com.test.baxolash.entity;

import com.test.baxolash.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "login", nullable = false, unique = true, length = 100)
    private String login;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "active", nullable = false)
    private Boolean active;

    /** Админ назначает: может ли пользователь редактировать заявки на оценку. */
    @Column(name = "can_edit_evaluation_requests", nullable = false)
    private Boolean canEditEvaluationRequests = true;

    /** Админ назначает: может ли пользователь удалять заявки на оценку. */
    @Column(name = "can_delete_evaluation_requests", nullable = false)
    private Boolean canDeleteEvaluationRequests = true;
}

