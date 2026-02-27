package com.test.baxolash.service;

/**
 * Генерация и проверка токена для публичной ссылки на PDF-отчёт (по QR-коду).
 */
public interface ReportTokenService {

    /**
     * Создать токен для заявки (действует 30 дней).
     */
    String generateToken(String evaluationRequestId);

    /**
     * Проверить токен и вернуть id заявки; иначе null или исключение.
     */
    String validateAndGetRequestId(String token);
}
