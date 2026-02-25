package com.test.baxolash.entity.enums;

/**
 * Статусы заявки на оценку (для фильтра и отображения).
 * Соответствие подписям в UI: Ҳали кўрилмади, Баҳоловчига бириктирилди, и т.д.
 */
public enum EvaluationRequestStatus {
    /** Ҳали кўрилмади */
    NOT_REVIEWED,
    /** Баҳоловчига бириктирилди */
    ASSIGNED_TO_APPRAISER,
    /** Баҳолаш жараёнида */
    IN_APPRAISAL,
    /** Тасдиқланган */
    APPROVED,
    /** Бекор қилинган */
    CANCELLED,
    /** Тайёр эмас */
    NOT_READY,
    /** Идентификацияда */
    IN_IDENTIFICATION,
    /** Устаревшие — для совместимости */
    SUBMITTED,
    IN_PROGRESS,
    REPORT_READY,
    COMPLETED
}
