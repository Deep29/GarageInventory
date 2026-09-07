package com.deepak.garageinventory.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Locale;

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
     * Generates 1D Linear Barcode (CODE_128) Bitmap.
     */
    public static Bitmap generateBarcode128(String content, int width, int height) throws WriterException {
        if (content == null || content.isEmpty()) {
            return null;
        }

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.CODE_128,
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
     * Formats integer number into sequential 5-digit string (e.g. 1 -> "00001", 42 -> "00042").
     */
    public static String formatFiveDigitBinCode(int number) {
        if (number < 1) number = 1;
        return String.format(Locale.US, "%05d", number);
    }
}
