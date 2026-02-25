package com.test.baxolash.service;

import com.test.baxolash.entity.EvaluationRequest;
import com.test.baxolash.entity.EvaluationRequestDocument;
import com.test.baxolash.entity.User;
import com.test.baxolash.entity.enums.UserRole;
import com.test.baxolash.exception.BusinessException;
import com.test.baxolash.exception.NotFoundException;
import com.test.baxolash.repository.EvaluationRequestDocumentRepository;
import com.test.baxolash.repository.EvaluationRequestRepository;
import com.test.baxolash.repository.UserRepository;
import com.test.baxolash.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Валидация прав доступа к заявкам на оценку.
 */
@Component
@RequiredArgsConstructor
public class EvaluationRequestAccessValidator {

    private final UserRepository userRepository;
    private final EvaluationRequestRepository requestRepository;
    private final EvaluationRequestDocumentRepository documentRepository;

    public User getCurrentUser() {
        String login = SecurityUtil.getCurrentUserLogin();
        if (login == null) {
            throw new BusinessException("Необходима авторизация");
        }
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new BusinessException("Пользователь не найден"));
    }

    /** Клиент может создавать заявки. */
    public void validateClientCanCreate(User current) {
        if (current.getRole() != UserRole.CLIENT_EMPLOYEE) {
            throw new BusinessException("Создавать заявки могут только клиенты (банк)");
        }
    }

    /** Только клиент. */
    public void validateClientOnly(User current) {
        if (current.getRole() != UserRole.CLIENT_EMPLOYEE) {
            throw new BusinessException("Доступ только для клиента");
        }
    }

    /** Только сотрудник компании или админ. */
    public void validateCompanyOrAdmin(User current) {
        if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Доступ только для сотрудника оценочной компании или админа");
        }
    }

    /** Клиент — только свои; компания/админ — любые. */
    public void validateAccessToRequest(User current, EvaluationRequest request) {
        if (current.getRole() == UserRole.CLIENT_EMPLOYEE) {
            if (request.getClientUser() == null || !request.getClientUser().getId().equals(current.getId())) {
                throw new BusinessException("Нет доступа к чужой заявке");
            }
        } else if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Недостаточно прав");
        }
    }

    /** Клиент — только свои; компания/админ — любые. */
    public void validateAccessToRequestId(User current, String requestId) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        validateAccessToRequest(current, request);
    }

    /** Редактировать заявку может только компания/админ. */
    public void validateCanUpdate(User current) {
        if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Редактировать заявку могут только сотрудники оценочной компании");
        }
    }

    /** Загружать документы: клиент — свои, компания/админ — любые. */
    public void validateCanUploadDocument(User current, String requestId) {
        EvaluationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));
        if (current.getRole() == UserRole.CLIENT_EMPLOYEE) {
            if (request.getClientUser() == null || !request.getClientUser().getId().equals(current.getId())) {
                throw new BusinessException("Нельзя загружать документы к чужой заявке");
            }
        } else if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Недостаточно прав");
        }
    }

    /** Загружать отчёт — только компания/админ. */
    public void validateCanUploadReport(User current) {
        if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Загружать отчёт могут только сотрудники оценочной компании");
        }
    }

    /** Подтверждать завершение — только компания/админ. */
    public void validateCanConfirmCompletion(User current) {
        if (current.getRole() != UserRole.COMPANY_EMPLOYEE && current.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Подтверждать завершение могут только сотрудники оценочной компании");
        }
    }

    /** Доступ к документу по id: клиент — свои, компания/админ — любые. */
    public void validateAccessToDocument(User current, String documentId) {
        EvaluationRequestDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Документ не найден"));
        EvaluationRequest request = doc.getEvaluationRequest();
        validateAccessToRequest(current, request);
    }
}
