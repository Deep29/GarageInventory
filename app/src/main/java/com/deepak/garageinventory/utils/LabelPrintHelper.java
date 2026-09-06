package com.deepak.garageinventory.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.print.PrintHelper;

public class LabelPrintHelper {

    /**
     * Creates a composite printable Label Bitmap with Title, 5-8 Digit Bin Code, and QR Code.
     */
    public static Bitmap createBinLabelBitmap(String binName, String binCode, Bitmap qrBitmap) {
        int labelWidth = 600;
        int labelHeight = 400;

        Bitmap bitmap = Bitmap.createBitmap(labelWidth, labelHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);

        // Header Title
        paintText.setTextSize(32f);
        paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("BIN LABEL", 30, 50, paintText);

        // Bin Name
        paintText.setTextSize(26f);
        paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Name: " + binName, 30, 100, paintText);

        // Short Code (For handwriting / fast identification)
        paintText.setTextSize(28f);
        paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Code: " + binCode, 30, 150, paintText);

        // Draw QR Code on the right
        if (qrBitmap != null) {
            Bitmap scaledQr = Bitmap.createScaledBitmap(qrBitmap, 200, 200, false);
            canvas.drawBitmap(scaledQr, 360, 80, null);
        }

        // Footer Border
        Paint paintBorder = new Paint();
        paintBorder.setColor(Color.BLACK);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(6f);
        canvas.drawRect(10, 10, labelWidth - 10, labelHeight - 10, paintBorder);

        return bitmap;
    }

    /**
     * Sends the generated Label Bitmap to the Android System Print Manager.
     */
    public static void printLabel(Context context, String jobName, Bitmap labelBitmap) {
        if (labelBitmap == null) return;
        PrintHelper printHelper = new PrintHelper(context);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        printHelper.printBitmap(jobName, labelBitmap);
    }
}
