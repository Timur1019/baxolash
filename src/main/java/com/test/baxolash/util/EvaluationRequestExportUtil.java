package com.test.baxolash.util;

import com.test.baxolash.dto.EvaluationRequestDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class EvaluationRequestExportUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private static final String FONT_FAMILY = "Times New Roman";
    private static final String PRIMARY_COLOR = "2C3E50";
    private static final String SECONDARY_COLOR = "7F8C8D";
    private static final String ACCENT_COLOR = "3498DB";

    private EvaluationRequestExportUtil() {
    }

    public static byte[] buildExcelForRequests(List<EvaluationRequestDto> items) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Заявки на оценку");

            // Стиль для заголовка
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Стиль для ячеек
            org.apache.poi.ss.usermodel.CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setWrapText(true);

            Row header = sheet.createRow(0);
            String[] columns = new String[]{
                    "ID", "Дата создания", "Статус", "Клиент", "Email",
                    "Объект оценки", "Описание", "Стоимость", "Отчёт",
                    "Регион", "Район", "Кадастровый номер",
                    "Цель оценки", "Заёмщик", "ИНН заёмщика",
                    "Телефон владельца", "Телефон банка",
                    "Адрес объекта", "Координаты"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (EvaluationRequestDto dto : items) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;

                createCell(row, col++, dto.getId(), cellStyle);
                createCell(row, col++, formatDate(dto.getCreatedAt()), cellStyle);
                createCell(row, col++, formatStatus(dto.getStatus()), cellStyle);
                createCell(row, col++, dto.getClientFullName(), cellStyle);
                createCell(row, col++, dto.getClientEmail(), cellStyle);
                createCell(row, col++, dto.getAppraisedObjectName(), cellStyle);
                createCell(row, col++, dto.getObjectDescription(), cellStyle);
                createCell(row, col++, formatCost(dto.getCost()), cellStyle);
                createCell(row, col++, dto.getHasReportFile() ? "✓" : "—", cellStyle);
                createCell(row, col++, dto.getRegionNameUz(), cellStyle);
                createCell(row, col++, dto.getDistrictNameUz(), cellStyle);
                createCell(row, col++, dto.getCadastralNumber(), cellStyle);
                createCell(row, col++, dto.getAppraisalPurpose(), cellStyle);
                createCell(row, col++, dto.getBorrowerName(), cellStyle);
                createCell(row, col++, dto.getBorrowerInn(), cellStyle);
                createCell(row, col++, dto.getOwnerPhone(), cellStyle);
                createCell(row, col++, dto.getBankEmployeePhone(), cellStyle);
                createCell(row, col++, dto.getObjectAddress(), cellStyle);
                createCell(row, col++, formatCoordinates(dto.getLatitude(), dto.getLongitude()), cellStyle);
            }

            // Автоматическая ширина колонок
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i), 10000));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Excel-файла", e);
        }
    }

    public static byte[] buildWordForRequest(EvaluationRequestDto dto) {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // === ШАПКА ДОКУМЕНТА ===
            addHeader(doc, dto.getId());

            // === ОСНОВНАЯ ИНФОРМАЦИЯ ===
            addSection(doc, "1. ОСНОВНАЯ ИНФОРМАЦИЯ", 1);
            addInfoRow(doc, "Дата создания:", formatDateTime(dto.getCreatedAt()));
            addInfoRow(doc, "Статус заявки:", formatStatus(dto.getStatus()));
            addInfoRow(doc, "Клиент:", formatClient(dto.getClientFullName(), dto.getClientEmail()));
            addInfoRow(doc, "Стоимость оценки:", formatCost(dto.getCost()));

            // === ОБЪЕКТ ОЦЕНКИ ===
            addSection(doc, "2. ОБЪЕКТ ОЦЕНКИ", 2);
            addInfoRow(doc, "Наименование:", dto.getAppraisedObjectName());
            addInfoRow(doc, "Описание:", dto.getObjectDescription());
            addInfoRow(doc, "Кадастровый номер:", dto.getCadastralNumber());
            addInfoRow(doc, "Цель оценки:", dto.getAppraisalPurpose());
            addInfoRow(doc, "Адрес:", dto.getObjectAddress());
            addInfoRow(doc, "Координаты:", formatCoordinates(dto.getLatitude(), dto.getLongitude()));

            // === ЗАЁМЩИК И КОНТАКТЫ ===
            addSection(doc, "3. ЗАЁМЩИК И КОНТАКТЫ", 3);
            addInfoRow(doc, "Заёмщик:", dto.getBorrowerName());
            addInfoRow(doc, "ИНН заёмщика:", dto.getBorrowerInn());
            addInfoRow(doc, "Телефон владельца:", dto.getOwnerPhone());
            addInfoRow(doc, "Телефон банка:", dto.getBankEmployeePhone());

            // === МЕСТОПОЛОЖЕНИЕ ===
            if (dto.getRegionNameUz() != null || dto.getDistrictNameUz() != null) {
                addSection(doc, "4. МЕСТОПОЛОЖЕНИЕ", 4);
                addInfoRow(doc, "Регион:", dto.getRegionNameUz());
                addInfoRow(doc, "Район:", dto.getDistrictNameUz());
                addInfoRow(doc, "Адрес местоположения:", dto.getLocationAddress());
            }

            // === ПОДПИСИ ===
            addSignatures(doc);

            doc.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Word-документа", e);
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ EXCEL ====================

    private static void createCell(Row row, int col, String value, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    private static void createCell(Row row, int col, Boolean value, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? (value ? "✓" : "—") : "—");
        cell.setCellStyle(style);
    }

    private static void createCell(Row row, int col, BigDecimal value, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(formatCost(value));
        cell.setCellStyle(style);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ WORD ====================

    private static void addHeader(XWPFDocument doc, String requestId) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        title.setSpacingAfter(200);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("ОТЧЁТ ПО ЗАЯВКЕ НА ОЦЕНКУ");
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setColor(PRIMARY_COLOR);
        titleRun.addBreak();

        XWPFRun idRun = title.createRun();
        idRun.setText("№ " + (requestId != null ? requestId.substring(0, Math.min(8, requestId.length())) : "—"));
        idRun.setFontSize(12);
        idRun.setFontFamily(FONT_FAMILY);
        idRun.setColor(SECONDARY_COLOR);
        idRun.setItalic(true);

        addEmptyLine(doc, 1);
    }

    private static void addSection(XWPFDocument doc, String title, int number) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(400);
        p.setSpacingAfter(200);
        p.setPageBreak(number > 1 && number % 2 == 1);

        XWPFRun r = p.createRun();
        r.setText(title);
        r.setBold(true);
        r.setFontSize(14);
        r.setFontFamily(FONT_FAMILY);
        r.setColor(PRIMARY_COLOR);
        r.addBreak();

        XWPFRun line = p.createRun();
        line.setText("—".repeat(50));
        line.setFontSize(10);
        line.setFontFamily(FONT_FAMILY);
        line.setColor(SECONDARY_COLOR);

        addEmptyLine(doc, 1);
    }

    private static void addInfoRow(XWPFDocument doc, String label, String value) {
        if (value == null || value.trim().isEmpty() || value.equals("—")) return;

        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(60);
        p.setIndentationLeft(400);

        XWPFRun labelRun = p.createRun();
        labelRun.setText(label);
        labelRun.setBold(true);
        labelRun.setFontSize(11);
        labelRun.setFontFamily(FONT_FAMILY);
        labelRun.setColor(PRIMARY_COLOR);

        XWPFRun valueRun = p.createRun();
        valueRun.setText(" " + value);
        valueRun.setFontSize(11);
        valueRun.setFontFamily(FONT_FAMILY);
        valueRun.setColor("000000");
    }

    private static void addSignatures(XWPFDocument doc) {
        addEmptyLine(doc, 3);

        XWPFTable table = doc.createTable(1, 2);
        table.setWidth("100%");
        table.setCellMargins(200, 200, 200, 200);

        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();

        CTTblBorders borders = tblPr.getTblBorders();
        if (borders == null) borders = tblPr.addNewTblBorders();
        borders.addNewTop().setVal(STBorder.NONE);
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);
        borders.addNewInsideH().setVal(STBorder.NONE);
        borders.addNewInsideV().setVal(STBorder.NONE);

        // Левая ячейка - исполнитель
        XWPFTableCell leftCell = table.getRow(0).getCell(0);
        leftCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFParagraph leftP = leftCell.addParagraph();
        leftP.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun leftRun = leftP.createRun();
        leftRun.setText("Исполнитель:");
        leftRun.setBold(true);
        leftRun.setFontSize(11);
        leftRun.setFontFamily(FONT_FAMILY);
        leftRun.setColor(PRIMARY_COLOR);
        leftRun.addBreak();
        leftRun.addBreak();
        leftRun.setText("____________________");
        leftRun.setFontSize(11);

        // Правая ячейка - дата
        XWPFTableCell rightCell = table.getRow(0).getCell(1);
        rightCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFParagraph rightP = rightCell.addParagraph();
        rightP.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun rightRun = rightP.createRun();
        rightRun.setText("Дата:");
        rightRun.setBold(true);
        rightRun.setFontSize(11);
        rightRun.setFontFamily(FONT_FAMILY);
        rightRun.setColor(PRIMARY_COLOR);
        rightRun.addBreak();
        rightRun.addBreak();
        rightRun.setText("\"" + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd")) + "\" "
                + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        rightRun.setFontSize(11);
    }

    private static void addEmptyLine(XWPFDocument doc, int count) {
        for (int i = 0; i < count; i++) {
            XWPFParagraph p = doc.createParagraph();
            p.setSpacingAfter(120);
        }
    }

    // ==================== ФОРМАТТЕРЫ ====================

    private static String formatDate(Object date) {
        if (date == null) return "—";
        try {
            return DATE_FORMATTER.format((java.time.temporal.TemporalAccessor) date);
        } catch (Exception e) {
            return "—";
        }
    }

    private static String formatDateTime(Object date) {
        if (date == null) return "—";
        try {
            return DATE_TIME_FORMATTER.format((java.time.temporal.TemporalAccessor) date);
        } catch (Exception e) {
            return "—";
        }
    }

    private static String formatStatus(Enum<?> status) {
        if (status == null) return "—";
        String name = status.name();
        switch (name) {
            case "NEW": return "Новая";
            case "IN_PROGRESS": return "В работе";
            case "COMPLETED": return "Завершена";
            case "CANCELLED": return "Отменена";
            case "REPORT_READY": return "Отчёт готов";
            default: return name;
        }
    }

    private static String formatCost(BigDecimal cost) {
        if (cost == null) return "—";
        return String.format("%,d сум", cost.longValue()).replace(',', ' ');
    }

    private static String formatClient(String name, String email) {
        if (name == null && email == null) return "—";
        if (name == null) return email;
        if (email == null) return name;
        return name + " (" + email + ")";
    }

    private static String formatCoordinates(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null) return "—";
        return String.format("%.6f, %.6f", lat.doubleValue(), lng.doubleValue());
    }
}