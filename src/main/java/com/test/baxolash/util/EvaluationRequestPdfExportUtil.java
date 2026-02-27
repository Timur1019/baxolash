package com.test.baxolash.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.FixedAssetItemDto;
import com.test.baxolash.entity.EvaluationRequestType;
import com.test.baxolash.exception.BusinessException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Экспорт одной заявки в PDF (для просмотра по QR-коду).
 */
public final class EvaluationRequestPdfExportUtil {

    private EvaluationRequestPdfExportUtil() {
    }

    public static byte[] buildPdfForRequest(EvaluationRequestDto dto) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font fontId = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, Color.GRAY);
            Font fontSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            doc.add(new Paragraph("ОТЧЁТ ПО ЗАЯВКЕ НА ОЦЕНКУ", fontTitle));
            String shortId = dto.getId() != null
                    ? dto.getId().substring(0, Math.min(8, dto.getId().length()))
                    : "—";
            doc.add(new Paragraph("№ " + shortId, fontId));
            doc.add(new Paragraph(" "));

            addPdfSection(doc, "1. ОСНОВНАЯ ИНФОРМАЦИЯ", fontSection);
            addPdfRow(doc, "Дата создания:",
                    EvaluationRequestExportUtil.formatDateTime(dto.getCreatedAt()),
                    fontLabel, fontBody);
            addPdfRow(doc, "Дата завершения:",
                    EvaluationRequestExportUtil.formatDateTime(dto.getCompletedAt()),
                    fontLabel, fontBody);
            addPdfRow(doc, "Статус заявки:",
                    EvaluationRequestExportUtil.formatStatus(dto.getStatus()),
                    fontLabel, fontBody);
            addPdfRow(doc, "Тип заявки:",
                    EvaluationRequestExportUtil.formatRequestType(dto.getRequestType()),
                    fontLabel, fontBody);
            addPdfRow(doc, "Клиент:",
                    EvaluationRequestExportUtil.formatClient(dto.getClientFullName(), dto.getClientEmail()),
                    fontLabel, fontBody);
            addPdfRow(doc, "Стоимость оценки:",
                    EvaluationRequestExportUtil.formatCost(dto.getCost()),
                    fontLabel, fontBody);

            addPdfSection(doc, "2. ОБЪЕКТ ОЦЕНКИ", fontSection);
            addPdfRow(doc, "Наименование:", dto.getAppraisedObjectName(), fontLabel, fontBody);
            addPdfRow(doc, "Описание:", dto.getObjectDescription(), fontLabel, fontBody);
            addPdfRow(doc, "Кадастровый номер:", dto.getCadastralNumber(), fontLabel, fontBody);
            addPdfRow(doc, "Цель оценки:", dto.getAppraisalPurpose(), fontLabel, fontBody);
            addPdfRow(doc, "Собственник объекта:", dto.getPropertyOwnerName(), fontLabel, fontBody);
            addPdfRow(doc, "Адрес:", dto.getObjectAddress(), fontLabel, fontBody);
            addPdfRow(doc, "Координаты:",
                    EvaluationRequestExportUtil.formatCoordinates(dto.getLatitude(), dto.getLongitude()),
                    fontLabel, fontBody);

            if (dto.getRequestType() == EvaluationRequestType.VEHICLE) {
                addPdfSection(doc, "3. ТРАНСПОРТНОЕ СРЕДСТВО", fontSection);
                addPdfRow(doc, "Тип ТС:", dto.getVehicleType(), fontLabel, fontBody);
                addPdfRow(doc, "Техпаспорт:", dto.getTechPassportNumber(), fontLabel, fontBody);
                addPdfRow(doc, "Гос. номер:", dto.getLicensePlate(), fontLabel, fontBody);
            }

            addPdfSection(doc, "3. ЗАЁМЩИК И КОНТАКТЫ", fontSection);
            addPdfRow(doc, "Заёмщик:", dto.getBorrowerName(), fontLabel, fontBody);
            addPdfRow(doc, "ИНН заёмщика:", dto.getBorrowerInn(), fontLabel, fontBody);
            addPdfRow(doc, "Телефон владельца:", dto.getOwnerPhone(), fontLabel, fontBody);
            addPdfRow(doc, "Телефон банка:", dto.getBankEmployeePhone(), fontLabel, fontBody);

            if (dto.getRegionNameUz() != null || dto.getDistrictNameUz() != null) {
                addPdfSection(doc, "4. МЕСТОПОЛОЖЕНИЕ", fontSection);
                addPdfRow(doc, "Регион:", dto.getRegionNameUz(), fontLabel, fontBody);
                addPdfRow(doc, "Район:", dto.getDistrictNameUz(), fontLabel, fontBody);
                addPdfRow(doc, "Адрес местоположения:", dto.getLocationAddress(), fontLabel, fontBody);
            }

            if (dto.getRequestType() == EvaluationRequestType.FIXED_ASSETS
                    && dto.getFixedAssetItems() != null
                    && !dto.getFixedAssetItems().isEmpty()) {
                addPdfSection(doc, "5. ПЕРЕЧЕНЬ ОСНОВНЫХ СРЕДСТВ", fontSection);
                for (FixedAssetItemDto item : dto.getFixedAssetItems()) {
                    if (item == null) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    if (item.getAssetType() != null && !item.getAssetType().isBlank()) {
                        sb.append(item.getAssetType()).append(": ");
                    }
                    if (item.getName() != null && !item.getName().isBlank()) {
                        sb.append(item.getName());
                    }
                    String qty = EvaluationRequestExportUtil.formatQuantity(item.getQuantity());
                    if (!"—".equals(qty)) {
                        if (sb.length() > 0) {
                            sb.append(" — ");
                        }
                        sb.append(qty);
                        if (item.getUnitOfMeasurement() != null && !item.getUnitOfMeasurement().isBlank()) {
                            sb.append(" ").append(item.getUnitOfMeasurement());
                        }
                    }
                    String line = sb.length() > 0 ? sb.toString() : null;
                    addPdfRow(doc, "•", line, fontLabel, fontBody);
                }
            }

            doc.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new BusinessException("Ошибка при создании PDF-отчёта");
        }
    }

    private static void addPdfSection(Document doc, String title, Font fontSection) throws DocumentException {
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(title, fontSection));
        doc.add(new Paragraph(" "));
    }

    private static void addPdfRow(Document doc, String label, String value, Font fontLabel, Font fontBody) throws DocumentException {
        if (value == null || value.isBlank() || "—".equals(value)) return;
        Paragraph p = new Paragraph();
        p.add(new com.lowagie.text.Chunk(label, fontLabel));
        p.add(new com.lowagie.text.Chunk(" " + value, fontBody));
        doc.add(p);
    }
}

