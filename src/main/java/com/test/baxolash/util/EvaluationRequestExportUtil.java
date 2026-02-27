package com.test.baxolash.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.exception.BusinessException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class EvaluationRequestExportUtil {


    private static final Locale RU = new Locale("ru");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", RU)
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy", RU)
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter DAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd", RU);

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy", RU);

    // Word — цвета
    private static final String COLOR_PRIMARY   = "2C3E50";
    private static final String COLOR_SECONDARY = "7F8C8D";
    private static final String COLOR_BLACK     = "000000";

    // Word — шрифт и размеры
    private static final String FONT_FAMILY  = "Times New Roman";
    private static final int FONT_TITLE      = 18;
    private static final int FONT_ID         = 12;
    private static final int FONT_SECTION    = 14;
    private static final int FONT_BODY       = 11;
    private static final int FONT_LINE       = 10;

    // Excel — максимальная ширина колонки
    private static final int MAX_COLUMN_WIDTH = 10_000;

    // Excel — заголовки колонок
    private static final String[] EXCEL_COLUMNS = {
            "ID", "Дата создания", "Статус", "Клиент", "Email",
            "Объект оценки", "Описание", "Стоимость", "Отчёт",
            "Регион", "Район", "Кадастровый номер",
            "Цель оценки", "Заёмщик", "ИНН заёмщика",
            "Телефон владельца", "Телефон банка",
            "Адрес объекта", "Координаты"
    };

    private EvaluationRequestExportUtil() {}

    // =====================================================
    //                       EXCEL
    // =====================================================

    public static byte[] buildExcelForRequests(List<EvaluationRequestDto> items) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Заявки на оценку");

            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle cellStyle   = buildCellStyle(workbook);

            writeHeader(sheet, headerStyle);
            writeRows(sheet, items, cellStyle);
            autoSizeColumns(sheet, EXCEL_COLUMNS.length);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new BusinessException("Ошибка при создании Excel-файла");
        }
    }

    private static void writeHeader(Sheet sheet, CellStyle style) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < EXCEL_COLUMNS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(EXCEL_COLUMNS[i]);
            cell.setCellStyle(style);
        }
    }

    private static void writeRows(Sheet sheet, List<EvaluationRequestDto> items, CellStyle style) {
        int rowIdx = 1;
        for (EvaluationRequestDto dto : items) {
            Row row = sheet.createRow(rowIdx++);
            int col = 0;

            setCell(row, col++, dto.getId(), style);
            setCell(row, col++, formatDate(dto.getCreatedAt()), style);
            setCell(row, col++, formatStatus(dto.getStatus()), style);
            setCell(row, col++, dto.getClientFullName(), style);
            setCell(row, col++, dto.getClientEmail(), style);
            setCell(row, col++, dto.getAppraisedObjectName(), style);
            setCell(row, col++, dto.getObjectDescription(), style);
            setCell(row, col++, formatCost(dto.getCost()), style);
            setCell(row, col++, Boolean.TRUE.equals(dto.getHasReportFile()) ? "✓" : "—", style);
            setCell(row, col++, dto.getRegionNameUz(), style);
            setCell(row, col++, dto.getDistrictNameUz(), style);
            setCell(row, col++, dto.getCadastralNumber(), style);
            setCell(row, col++, dto.getAppraisalPurpose(), style);
            setCell(row, col++, dto.getBorrowerName(), style);
            setCell(row, col++, dto.getBorrowerInn(), style);
            setCell(row, col++, dto.getOwnerPhone(), style);
            setCell(row, col++, dto.getBankEmployeePhone(), style);
            setCell(row, col++, dto.getObjectAddress(), style);
            setCell(row, col,   formatCoordinates(dto.getLatitude(), dto.getLongitude()), style);
        }
    }

    private static void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    private static void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i), MAX_COLUMN_WIDTH));
        }
    }

    private static CellStyle buildHeaderStyle(Workbook wb) {
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(style);
        return style;
    }

    private static CellStyle buildCellStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        applyThinBorders(style);
        style.setWrapText(true);
        return style;
    }

    private static void applyThinBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // =====================================================
    //                       WORD
    // =====================================================

    /**
     * Сборка PDF-отчёта по заявке (для просмотра по QR-коду).
     */
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
            String shortId = dto.getId() != null ? dto.getId().substring(0, Math.min(8, dto.getId().length())) : "—";
            doc.add(new Paragraph("№ " + shortId, fontId));
            doc.add(new Paragraph(" "));

            addPdfSection(doc, "1. ОСНОВНАЯ ИНФОРМАЦИЯ", fontSection);
            addPdfRow(doc, "Дата создания:", formatDateTime(dto.getCreatedAt()), fontLabel, fontBody);
            addPdfRow(doc, "Статус заявки:", formatStatus(dto.getStatus()), fontLabel, fontBody);
            addPdfRow(doc, "Клиент:", formatClient(dto.getClientFullName(), dto.getClientEmail()), fontLabel, fontBody);
            addPdfRow(doc, "Стоимость оценки:", formatCost(dto.getCost()), fontLabel, fontBody);

            addPdfSection(doc, "2. ОБЪЕКТ ОЦЕНКИ", fontSection);
            addPdfRow(doc, "Наименование:", dto.getAppraisedObjectName(), fontLabel, fontBody);
            addPdfRow(doc, "Описание:", dto.getObjectDescription(), fontLabel, fontBody);
            addPdfRow(doc, "Кадастровый номер:", dto.getCadastralNumber(), fontLabel, fontBody);
            addPdfRow(doc, "Цель оценки:", dto.getAppraisalPurpose(), fontLabel, fontBody);
            addPdfRow(doc, "Адрес:", dto.getObjectAddress(), fontLabel, fontBody);
            addPdfRow(doc, "Координаты:", formatCoordinates(dto.getLatitude(), dto.getLongitude()), fontLabel, fontBody);

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

    public static byte[] buildWordForRequest(EvaluationRequestDto dto) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            addDocumentHeader(doc, dto.getId());

            addSection(doc, "1. ОСНОВНАЯ ИНФОРМАЦИЯ");
            addInfoRow(doc, "Дата создания:",    formatDateTime(dto.getCreatedAt()));
            addInfoRow(doc, "Статус заявки:",    formatStatus(dto.getStatus()));
            addInfoRow(doc, "Клиент:",           formatClient(dto.getClientFullName(), dto.getClientEmail()));
            addInfoRow(doc, "Стоимость оценки:", formatCost(dto.getCost()));

            addSection(doc, "2. ОБЪЕКТ ОЦЕНКИ");
            addInfoRow(doc, "Наименование:",      dto.getAppraisedObjectName());
            addInfoRow(doc, "Описание:",          dto.getObjectDescription());
            addInfoRow(doc, "Кадастровый номер:", dto.getCadastralNumber());
            addInfoRow(doc, "Цель оценки:",       dto.getAppraisalPurpose());
            addInfoRow(doc, "Адрес:",             dto.getObjectAddress());
            addInfoRow(doc, "Координаты:",        formatCoordinates(dto.getLatitude(), dto.getLongitude()));

            addSection(doc, "3. ЗАЁМЩИК И КОНТАКТЫ");
            addInfoRow(doc, "Заёмщик:",           dto.getBorrowerName());
            addInfoRow(doc, "ИНН заёмщика:",      dto.getBorrowerInn());
            addInfoRow(doc, "Телефон владельца:", dto.getOwnerPhone());
            addInfoRow(doc, "Телефон банка:",     dto.getBankEmployeePhone());

            if (dto.getRegionNameUz() != null || dto.getDistrictNameUz() != null) {
                addSection(doc, "4. МЕСТОПОЛОЖЕНИЕ");
                addInfoRow(doc, "Регион:",                dto.getRegionNameUz());
                addInfoRow(doc, "Район:",                 dto.getDistrictNameUz());
                addInfoRow(doc, "Адрес местоположения:", dto.getLocationAddress());
            }

            addSignatures(doc);

            doc.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new BusinessException("Ошибка при создании Word-документа");
        }
    }

    private static void addDocumentHeader(XWPFDocument doc, String requestId) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        title.setSpacingAfter(200);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("ОТЧЁТ ПО ЗАЯВКЕ НА ОЦЕНКУ");
        applyStyle(titleRun, FONT_TITLE, true, false, COLOR_PRIMARY);
        titleRun.addBreak();

        String shortId = requestId != null
                ? requestId.substring(0, Math.min(8, requestId.length()))
                : "—";

        XWPFRun idRun = title.createRun();
        idRun.setText("№ " + shortId);
        applyStyle(idRun, FONT_ID, false, true, COLOR_SECONDARY);

        addEmptyLines(doc, 1);
    }

    private static void addSection(XWPFDocument doc, String sectionTitle) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(400);
        p.setSpacingAfter(200);

        XWPFRun titleRun = p.createRun();
        titleRun.setText(sectionTitle);
        applyStyle(titleRun, FONT_SECTION, true, false, COLOR_PRIMARY);
        titleRun.addBreak();

        XWPFRun lineRun = p.createRun();
        lineRun.setText("—".repeat(50));
        applyStyle(lineRun, FONT_LINE, false, false, COLOR_SECONDARY);

        addEmptyLines(doc, 1);
    }

    private static void addInfoRow(XWPFDocument doc, String label, String value) {
        if (value == null || value.isBlank() || "—".equals(value)) return;

        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(60);
        p.setIndentationLeft(400);

        XWPFRun labelRun = p.createRun();
        labelRun.setText(label);
        applyStyle(labelRun, FONT_BODY, true, false, COLOR_PRIMARY);

        XWPFRun valueRun = p.createRun();
        valueRun.setText(" " + value);
        applyStyle(valueRun, FONT_BODY, false, false, COLOR_BLACK);
    }

    private static void addSignatures(XWPFDocument doc) {
        addEmptyLines(doc, 3);

        XWPFTable table = doc.createTable(1, 2);
        table.setWidth("100%");
        table.setCellMargins(200, 200, 200, 200);
        removeTableBorders(table);

        // Левая ячейка — исполнитель
        XWPFParagraph leftP = getFirstParagraph(table.getRow(0).getCell(0));
        leftP.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun leftRun = leftP.createRun();
        leftRun.setText("Исполнитель:");
        applyStyle(leftRun, FONT_BODY, true, false, COLOR_PRIMARY);
        leftRun.addBreak();
        leftRun.addBreak();
        leftRun.setText("____________________");
        leftRun.setFontSize(FONT_BODY);

        // Правая ячейка — дата
        XWPFParagraph rightP = getFirstParagraph(table.getRow(0).getCell(1));
        rightP.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun rightRun = rightP.createRun();
        rightRun.setText("Дата:");
        applyStyle(rightRun, FONT_BODY, true, false, COLOR_PRIMARY);
        rightRun.addBreak();
        rightRun.addBreak();

        LocalDate today = LocalDate.now();
        rightRun.setText("\"" + today.format(DAY_FORMATTER) + "\" " + today.format(MONTH_YEAR_FORMATTER));
        rightRun.setFontSize(FONT_BODY);
    }

    // ==================== УТИЛИТЫ ДЛЯ WORD ====================

    private static void applyStyle(XWPFRun run, int size, boolean bold, boolean italic, String color) {
        run.setFontSize(size);
        run.setFontFamily(FONT_FAMILY);
        run.setBold(bold);
        run.setItalic(italic);
        run.setColor(color);
    }

    private static void removeTableBorders(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblBorders b = tblPr.isSetTblBorders()
                ? tblPr.getTblBorders()
                : tblPr.addNewTblBorders();
        b.addNewTop().setVal(STBorder.NONE);
        b.addNewBottom().setVal(STBorder.NONE);
        b.addNewLeft().setVal(STBorder.NONE);
        b.addNewRight().setVal(STBorder.NONE);
        b.addNewInsideH().setVal(STBorder.NONE);
        b.addNewInsideV().setVal(STBorder.NONE);
    }

    private static XWPFParagraph getFirstParagraph(XWPFTableCell cell) {
        return cell.getParagraphs().isEmpty()
                ? cell.addParagraph()
                : cell.getParagraphs().get(0);
    }

    private static void addEmptyLines(XWPFDocument doc, int count) {
        for (int i = 0; i < count; i++) {
            doc.createParagraph().setSpacingAfter(120);
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
        return switch (status.name()) {
            // ✅ Реальные статусы из БД
            case "NOT_REVIEWED"          -> "Не рассмотрена";
            case "ASSIGNED_TO_APPRAISER" -> "Назначен оценщик";
            case "IN_APPRAISAL"          -> "На оценке";
            case "IN_IDENTIFICATION"     -> "На идентификации";
            case "APPROVED"              -> "Утверждена";
            case "NOT_READY"             -> "Не готова";
            case "CANCELLED"             -> "Отменена";
            // Легаси — на всякий случай
            case "NEW"                   -> "Новая";
            case "IN_PROGRESS"           -> "В работе";
            case "COMPLETED"             -> "Завершена";
            case "REPORT_READY"          -> "Отчёт готов";
            default                      -> status.name();
        };
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
