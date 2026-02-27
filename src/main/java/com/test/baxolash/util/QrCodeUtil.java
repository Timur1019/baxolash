package com.test.baxolash.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Генерация QR-кода в PNG для ссылки на отчёт.
 */
public final class QrCodeUtil {

    private static final int SIZE = 400;
    private static final String FORMAT = "PNG";

    private QrCodeUtil() {}

    /**
     * Сгенерировать PNG QR-код с указанным содержимым (URL).
     */
    public static byte[] generateQrPng(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content обязателен");
        }
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, FORMAT, out);
            return out.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Ошибка генерации QR-кода", e);
        }
    }
}
