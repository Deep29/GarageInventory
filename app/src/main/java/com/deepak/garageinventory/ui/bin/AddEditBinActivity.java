package com.deepak.garageinventory.ui.bin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityAddEditBinBinding;
import com.deepak.garageinventory.utils.LabelPrintHelper;
import com.deepak.garageinventory.utils.QrCodeGenerator;

public class AddEditBinActivity extends AppCompatActivity {

    private ActivityAddEditBinBinding binding;
    private InventoryRepository repository;
    private long currentBinId = -1;
    private StorageBin currentBin;
    private Bitmap currentLabelBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("extra_bin_id")) {
            currentBinId = getIntent().getLongExtra("extra_bin_id", -1);
        }

        if (currentBinId != -1) {
            setTitle("Edit Storage Bin");
            binding.btnSaveBin.setText("Update Storage Bin");
            loadBinData();
        } else {
            setTitle("Add Storage Bin");
            String newCode = QrCodeGenerator.formatFiveDigitBinCode(1);
            binding.etBinCode.setText(newCode);
            binding.etBinName.setText("BIN " + newCode);
            updatePreviewLabel();
        }

        binding.btnGenerateCode.setOnClickListener(v -> {
            CharSequence codeText = binding.etBinCode.getText();
            String code = codeText != null ? codeText.toString().trim() : "";
            int num = 1;
            try {
                if (!code.isEmpty()) num = Integer.parseInt(code) + 1;
            } catch (Exception ignored) {}
            String nextCode = QrCodeGenerator.formatFiveDigitBinCode(num);
            binding.etBinCode.setText(nextCode);
            binding.etBinName.setText("BIN " + nextCode);
            updatePreviewLabel();
        });

        TextWatcher labelTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreviewLabel();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etBinName.addTextChangedListener(labelTextWatcher);
        binding.etBinCode.addTextChangedListener(labelTextWatcher);

        binding.btnPrintLabelNow.setOnClickListener(v -> {
            if (currentLabelBitmap != null) {
                String code = binding.etBinCode.getText().toString().trim();
                LabelPrintHelper.printLabel(this, "BinLabel_" + code, currentLabelBitmap);
            } else {
                Toast.makeText(this, "Label not ready yet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveBin.setOnClickListener(v -> saveBin());
    }

    private void loadBinData() {
        repository.getBinById(currentBinId).observe(this, bin -> {
            if (bin != null) {
                currentBin = bin;
                binding.etBinName.setText(bin.getBinName());
                binding.etBinCode.setText(bin.getBinCode());
                binding.etBinDesc.setText(bin.getDescription());
                updatePreviewLabel();
            }
        });
    }

    private void updatePreviewLabel() {
        CharSequence nameText = binding.etBinName.getText();
        CharSequence codeText = binding.etBinCode.getText();

        String name = nameText != null ? nameText.toString().trim() : "";
        String code = codeText != null ? codeText.toString().trim() : "";

        if (name.isEmpty()) name = "Sample Bin Name";
        if (code.isEmpty()) code = "00000";

        try {
            Bitmap qrBitmap = QrCodeGenerator.generateQrCode(code, 200, 200);
            currentLabelBitmap = LabelPrintHelper.createBinLabelBitmap(name, code, qrBitmap);
            binding.ivLabelPreview.setImageBitmap(currentLabelBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBin() {
        String name = binding.etBinName.getText().toString().trim();
        String code = binding.etBinCode.getText().toString().trim();
        String desc = binding.etBinDesc.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etBinName.setError("Bin name is required");
            return;
        }

        if (code.isEmpty()) {
            binding.etBinCode.setError("Bin code is required");
            return;
        }

        long now = System.currentTimeMillis();

        if (currentBin != null) {
            currentBin.setBinName(name);
            currentBin.setBinCode(code);
            currentBin.setDescription(desc);
            currentBin.setQrCodeContent(code);
            repository.updateBin(currentBin);
            Toast.makeText(this, "Bin updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            StorageBin newBin = new StorageBin(code, name, desc, code, now);
            repository.insertBin(newBin, binId -> {
                Toast.makeText(this, "Bin saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
