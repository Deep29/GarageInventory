package com.deepak.garageinventory.ui.bin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityBulkBinPrintBinding;
import com.deepak.garageinventory.utils.LabelPrintHelper;
import com.deepak.garageinventory.utils.QrCodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class BulkBinPrintActivity extends AppCompatActivity {

    private ActivityBulkBinPrintBinding binding;
    private InventoryRepository repository;
    private Bitmap generatedSheetBitmap;
    private List<StorageBin> generatedBinsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBulkBinPrintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnGenerateSheet.setOnClickListener(v -> generateBarcodeSheet());

        binding.btnPrintSheetNormal.setOnClickListener(v -> {
            if (generatedSheetBitmap != null) {
                LabelPrintHelper.printLabel(this, "BinBarcodeSheet_A4", generatedSheetBitmap);
            } else {
                Toast.makeText(this, "Please generate barcode sheet first", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveBinsToDb.setOnClickListener(v -> saveGeneratedBinsToDatabase());

        // Default initial sheet generation
        generateBarcodeSheet();
    }

    private void generateBarcodeSheet() {
        String startStr = binding.etStartNumber.getText().toString().trim();
        String qtyStr = binding.etBinQuantity.getText().toString().trim();

        int startNum = 1;
        try {
            startNum = Integer.parseInt(startStr);
        } catch (Exception ignored) {}

        int qty = 12;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (Exception ignored) {}

        if (qty <= 0) qty = 1;
        if (qty > 48) qty = 48; // Max per sheet

        generatedBinsList.clear();

        // A4 Paper Dimensions at 150 DPI (approx 1240 x 1754 px)
        int sheetWidth = 1240;
        int sheetHeight = 1754;

        Bitmap bitmap = Bitmap.createBitmap(sheetWidth, sheetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        int cols = 2;
        int rows = (int) Math.ceil((double) qty / cols);

        int margin = 40;
        int cellWidth = (sheetWidth - (margin * 3)) / cols;
        int cellHeight = (sheetHeight - (margin * 2) - 100) / Math.max(rows, 1);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(26f);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);

        long now = System.currentTimeMillis();

        for (int i = 0; i < qty; i++) {
            int currentNum = startNum + i;
            String binCode = QrCodeGenerator.formatFiveDigitBinCode(currentNum);
            String binName = "BIN " + binCode;

            StorageBin bin = new StorageBin(binCode, binName, "Storage Location " + binCode, binCode, now);
            generatedBinsList.add(bin);

            int col = i % cols;
            int row = i / cols;

            int left = margin + col * (cellWidth + margin);
            int top = margin + row * (cellHeight + margin / 2);
            int right = left + cellWidth;
            int bottom = top + cellHeight;

            // Draw cell border
            canvas.drawRect(left, top, right, bottom, borderPaint);

            // Draw Bin Name & Code
            canvas.drawText(binName, left + 20, top + 45, textPaint);
            canvas.drawText("CODE: " + binCode, left + 20, top + 85, textPaint);

            // Draw QR Code
            try {
                Bitmap qrBitmap = QrCodeGenerator.generateQrCode(binCode, 150, 150);
                if (qrBitmap != null) {
                    canvas.drawBitmap(qrBitmap, left + cellWidth - 170, top + 15, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        generatedSheetBitmap = bitmap;
        binding.ivSheetPreview.setImageBitmap(generatedSheetBitmap);
        Toast.makeText(this, "Generated " + qty + " Bin Barcodes!", Toast.LENGTH_SHORT).show();
    }

    private void saveGeneratedBinsToDatabase() {
        if (generatedBinsList.isEmpty()) {
            Toast.makeText(this, "No bins to save", Toast.LENGTH_SHORT).show();
            return;
        }

        for (StorageBin bin : generatedBinsList) {
            repository.insertBin(bin, null);
        }

        Toast.makeText(this, "Saved " + generatedBinsList.size() + " Storage Bins to Database!", Toast.LENGTH_LONG).show();
    }
}
