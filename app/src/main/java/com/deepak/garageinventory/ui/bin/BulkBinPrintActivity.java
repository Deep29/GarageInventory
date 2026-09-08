package com.deepak.garageinventory.ui.bin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private final List<StorageBin> generatedBinsList = new ArrayList<>();

    private static final String[] PAPER_SIZE_OPTIONS = {
            "A4 Standard Sheet (3 x 8 = 24 Labels)",
            "A5 Compact Sheet (2 x 6 = 12 Labels)",
            "Letter Sheet (3 x 8 = 24 Labels)",
            "Custom Grid Format (Set Cols & Rows)"
    };

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

        setupPaperSizeDropdown();

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
        binding.getRoot().post(this::generateBarcodeSheet);
    }

    private void setupPaperSizeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                PAPER_SIZE_OPTIONS
        );
        binding.actvPaperSize.setAdapter(adapter);

        binding.actvPaperSize.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 3) { // Custom Grid Format
                binding.layoutCustomDimensions.setVisibility(View.VISIBLE);
            } else {
                binding.layoutCustomDimensions.setVisibility(View.GONE);
            }
            generateBarcodeSheet();
        });
    }

    private void generateBarcodeSheet() {
        try {
            CharSequence startText = binding.etStartNumber.getText();
            CharSequence qtyText = binding.etBinQuantity.getText();

            String startStr = startText != null ? startText.toString().trim() : "1";
            String qtyStr = qtyText != null ? qtyText.toString().trim() : "24";

            int startNum = 1;
            try {
                if (!startStr.isEmpty()) startNum = Integer.parseInt(startStr);
            } catch (Exception ignored) {}

            int qty = 24;
            try {
                if (!qtyStr.isEmpty()) qty = Integer.parseInt(qtyStr);
            } catch (Exception ignored) {}

            if (qty <= 0) qty = 1;
            if (qty > 100) qty = 100; // Upper limit safety

            int paperSelection = 0;
            String selectedFormat = binding.actvPaperSize.getText().toString();
            for (int i = 0; i < PAPER_SIZE_OPTIONS.length; i++) {
                if (PAPER_SIZE_OPTIONS[i].equalsIgnoreCase(selectedFormat)) {
                    paperSelection = i;
                    break;
                }
            }

            int cols = 3;
            int rowsPerSheet = 8;

            if (paperSelection == 1) { // A5 Sheet
                cols = 2;
                rowsPerSheet = 6;
            } else if (paperSelection == 3) { // Custom
                CharSequence colText = binding.etCustomColumns.getText();
                CharSequence rowText = binding.etCustomRows.getText();
                try {
                    if (colText != null && !colText.toString().trim().isEmpty()) {
                        cols = Integer.parseInt(colText.toString().trim());
                    }
                    if (rowText != null && !rowText.toString().trim().isEmpty()) {
                        rowsPerSheet = Integer.parseInt(rowText.toString().trim());
                    }
                } catch (Exception ignored) {}
            }

            if (cols < 1) cols = 1;
            if (rowsPerSheet < 1) rowsPerSheet = 1;

            generatedBinsList.clear();

            // Preview Canvas Dimensions (620 x 877 px for A4 aspect ratio preview)
            int sheetWidth = 620;
            int sheetHeight = (int) (sheetWidth * 1.414); // A4 aspect ratio

            Bitmap bitmap = Bitmap.createBitmap(sheetWidth, sheetHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);

            int actualRows = Math.min((int) Math.ceil((double) qty / cols), rowsPerSheet);

            int margin = 16;
            int cellWidth = (sheetWidth - (margin * (cols + 1))) / cols;
            int cellHeight = (sheetHeight - (margin * (actualRows + 1))) / Math.max(actualRows, 1);

            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(Math.max(10f, cellHeight * 0.18f));

            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.LTGRAY);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(2f);

            long now = System.currentTimeMillis();

            int displayQty = Math.min(qty, cols * rowsPerSheet);

            for (int i = 0; i < displayQty; i++) {
                int currentNum = startNum + i;
                String binCode = QrCodeGenerator.formatFiveDigitBinCode(currentNum);
                String binName = "BIN " + binCode;

                StorageBin bin = new StorageBin(binCode, binName, "Storage Location " + binCode, binCode, now);
                generatedBinsList.add(bin);

                int col = i % cols;
                int row = i / cols;

                int left = margin + col * (cellWidth + margin);
                int top = margin + row * (cellHeight + margin);
                int right = left + cellWidth;
                int bottom = top + cellHeight;

                // Draw cell border
                canvas.drawRect(left, top, right, bottom, borderPaint);

                // Draw Bin Name & Code
                canvas.drawText(binName, left + 8, top + (cellHeight * 0.3f), textPaint);
                canvas.drawText(binCode, left + 8, top + (cellHeight * 0.6f), textPaint);

                // Draw QR Code on the right
                try {
                    int qrSize = (int) (cellHeight * 0.75f);
                    Bitmap qrBitmap = QrCodeGenerator.generateQrCode(binCode, qrSize, qrSize);
                    if (qrBitmap != null) {
                        canvas.drawBitmap(qrBitmap, right - qrSize - 6, top + (cellHeight - qrSize) / 2f, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            generatedSheetBitmap = bitmap;
            binding.ivSheetPreview.setImageBitmap(generatedSheetBitmap);
            binding.tvSheetPreviewTitle.setText("Printable Sheet Preview (" + cols + "x" + actualRows + " = " + displayQty + " Labels)");
            Toast.makeText(this, "Generated " + displayQty + " Barcodes on Sheet!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating sheet: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveGeneratedBinsToDatabase() {
        if (generatedBinsList.isEmpty()) {
            Toast.makeText(this, "No bins to save", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            for (StorageBin bin : generatedBinsList) {
                repository.insertBin(bin, null);
            }
            Toast.makeText(this, "Saved " + generatedBinsList.size() + " Storage Bins to Database!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save bins: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
