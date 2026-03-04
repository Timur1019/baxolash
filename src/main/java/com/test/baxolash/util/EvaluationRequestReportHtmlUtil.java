package com.test.baxolash.util;

import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.FixedAssetItemDto;
import com.test.baxolash.entity.EvaluationRequestType;

/**
 * Генерация HTML-страницы отчёта по заявке для просмотра по QR (мобильные браузеры).
 */
public final class EvaluationRequestReportHtmlUtil {

    private EvaluationRequestReportHtmlUtil() {
    }

    /**
     * Строит HTML-страницу отчёта. pdfDownloadUrl — ссылка для кнопки «Скачать PDF» (тот же URL с download=1).
     */
    public static String buildHtml(EvaluationRequestDto dto, String pdfDownloadUrl) {
        String shortId = dto.getId() != null
                ? dto.getId().substring(0, Math.min(8, dto.getId().length()))
                : "—";

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang=\"ru\"><head>")
                .append("<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">")
                .append("<title>Отчёт № ").append(escape(shortId)).append("</title>")
                .append("<style>")
                .append("body{font-family:system-ui,-apple-system,sans-serif;margin:0;padding:16px;background:#f5f5f5;color:#1a1a1a;}")
                .append(".wrap{max-width:600px;margin:0 auto;background:#fff;border-radius:12px;padding:24px;box-shadow:0 2px 8px rgba(0,0,0,.08);}")
                .append("h1{font-size:1.25rem;margin:0 0 4px;color:#333;}")
                .append(".id{font-size:0.875rem;color:#666;margin-bottom:20px;}")
                .append("h2{font-size:1rem;margin:20px 0 10px;color:#444;border-bottom:1px solid #eee;padding-bottom:6px;}")
                .append(".row{margin:8px 0;font-size:0.9375rem;}")
                .append(".row .l{color:#666;margin-right:6px;}")
                .append(".row .v{color:#1a1a1a;}")
                .append(".dl{margin-top:24px;}")
                .append("a.dl{display:inline-block;background:#2563eb;color:#fff;padding:12px 20px;border-radius:8px;text-decoration:none;font-weight:500;}")
                .append("a.dl:hover{background:#1d4ed8;}")
                .append("</style></head><body><div class=\"wrap\">");

        sb.append("<h1>ОТЧЁТ ПО ЗАЯВКЕ НА ОЦЕНКУ</h1>");
        sb.append("<p class=\"id\">№ ").append(escape(shortId)).append("</p>");

        section(sb, "1. Основная информация");
        row(sb, "Дата создания", EvaluationRequestExportUtil.formatDateTime(dto.getCreatedAt()));
        row(sb, "Дата завершения", EvaluationRequestExportUtil.formatDateTime(dto.getCompletedAt()));
        row(sb, "Статус заявки", EvaluationRequestExportUtil.formatStatus(dto.getStatus()));
        row(sb, "Тип заявки", EvaluationRequestExportUtil.formatRequestType(dto.getRequestType()));
        row(sb, "Клиент", EvaluationRequestExportUtil.formatClient(dto.getClientFullName(), dto.getClientEmail()));
        row(sb, "Стоимость оценки", EvaluationRequestExportUtil.formatCost(dto.getCost()));

        section(sb, "2. Объект оценки");
        row(sb, "Наименование", dto.getAppraisedObjectName());
        row(sb, "Описание", dto.getObjectDescription());
        row(sb, "Кадастровый номер", dto.getCadastralNumber());
        row(sb, "Цель оценки", dto.getAppraisalPurpose());
        row(sb, "Собственник объекта", dto.getPropertyOwnerName());
        row(sb, "Адрес", dto.getObjectAddress());
        row(sb, "Координаты", EvaluationRequestExportUtil.formatCoordinates(dto.getLatitude(), dto.getLongitude()));

        if (dto.getRequestType() == EvaluationRequestType.VEHICLE) {
            section(sb, "3. Транспортное средство");
            row(sb, "Тип ТС", dto.getVehicleType());
            row(sb, "Техпаспорт", dto.getTechPassportNumber());
            row(sb, "Гос. номер", dto.getLicensePlate());
        }

        section(sb, "Заёмщик и контакты");
        row(sb, "Заёмщик", dto.getBorrowerName());
        row(sb, "ИНН заёмщика", dto.getBorrowerInn());
        row(sb, "Телефон владельца", dto.getOwnerPhone());
        row(sb, "Телефон банка", dto.getBankEmployeePhone());

        if (dto.getRegionNameUz() != null || dto.getDistrictNameUz() != null) {
            section(sb, "Местоположение");
            row(sb, "Регион", dto.getRegionNameUz());
            row(sb, "Район", dto.getDistrictNameUz());
            row(sb, "Адрес местоположения", dto.getLocationAddress());
        }

        if (dto.getRequestType() == EvaluationRequestType.FIXED_ASSETS
                && dto.getFixedAssetItems() != null
                && !dto.getFixedAssetItems().isEmpty()) {
            section(sb, "Перечень основных средств");
            for (FixedAssetItemDto item : dto.getFixedAssetItems()) {
                if (item == null) continue;
                StringBuilder line = new StringBuilder();
                if (item.getAssetType() != null && !item.getAssetType().isBlank())
                    line.append(item.getAssetType()).append(": ");
                if (item.getName() != null && !item.getName().isBlank())
                    line.append(item.getName());
                String qty = EvaluationRequestExportUtil.formatQuantity(item.getQuantity());
                if (!"—".equals(qty)) {
                    if (line.length() > 0) line.append(" — ");
                    line.append(qty);
                    if (item.getUnitOfMeasurement() != null && !item.getUnitOfMeasurement().isBlank())
                        line.append(" ").append(item.getUnitOfMeasurement());
                }
                if (line.length() > 0)
                    row(sb, "•", line.toString());
            }
        }

        if (pdfDownloadUrl != null && !pdfDownloadUrl.isBlank()) {
            sb.append("<p class=\"dl\"><a class=\"dl\" href=\"").append(escape(pdfDownloadUrl)).append("\">Скачать отчёт PDF</a></p>");
        }

        sb.append("</div></body></html>");
        return sb.toString();
    }

    private static void section(StringBuilder sb, String title) {
        sb.append("<h2>").append(escape(title)).append("</h2>");
    }

    private static void row(StringBuilder sb, String label, String value) {
        if (value == null || value.isBlank() || "—".equals(value)) return;
        sb.append("<div class=\"row\"><span class=\"l\">").append(escape(label)).append(":</span> <span class=\"v\">").append(escape(value)).append("</span></div>");
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
