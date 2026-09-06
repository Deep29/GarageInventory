package com.deepak.garageinventory.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Random;

public class QrCodeGenerator {

    /**
     * Generates a square QR Code Bitmap from text content.
     */
    public static Bitmap generateQrCode(String content, int width, int height) throws WriterException {
        if (content == null || content.isEmpty()) {
            return null;
        }

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height
        );

        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];

        for (int y = 0; y < matrixHeight; y++) {
            int offset = y * matrixWidth;
            for (int x = 0; x < matrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
        return bitmap;
    }

    /**
     * Helper to generate a 6-digit numeric bin code (e.g. "847291").
     */
    public static String generateShortBinCode() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 6-digit number between 100000 and 999999
        return String.valueOf(number);
    }
}
