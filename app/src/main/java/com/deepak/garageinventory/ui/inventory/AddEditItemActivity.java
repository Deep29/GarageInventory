package com.deepak.garageinventory.ui.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityAddEditItemBinding;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddEditItemActivity extends AppCompatActivity {

    private ActivityAddEditItemBinding binding;
    private InventoryRepository repository;
    private long currentItemId = -1;
    private InventoryItem currentItem;

    private List<StorageBin> availableBins = new ArrayList<>();
    private long selectedBinId = 0;
    private String selectedImagePath = null;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String code = result.getData().getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT);
                    if (code != null) {
                        binding.etPartBarcode.setText(code);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    copyImageToLocalCache(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("extra_item_id")) {
            currentItemId = getIntent().getLongExtra("extra_item_id", -1);
        }

        setupBinDropdown();

        if (currentItemId != -1) {
            setTitle("Edit Part Details");
            binding.btnSaveItem.setText("Update Part");
            loadItemData();
        } else {
            setTitle("Add Spare Part");
        }

        binding.btnScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        binding.ivPartImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.btnSaveItem.setOnClickListener(v -> saveItem());
    }

    private void setupBinDropdown() {
        repository.getAllBins().observe(this, bins -> {
            availableBins = bins != null ? bins : new ArrayList<>();
            List<String> binNames = new ArrayList<>();
            binNames.add("Unassigned / None");

            for (StorageBin b : availableBins) {
                binNames.add(b.getBinName() + " (" + b.getBinCode() + ")");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    binNames
            );

            binding.actvBinLocation.setAdapter(adapter);

            binding.actvBinLocation.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    selectedBinId = 0;
                } else if (position - 1 < availableBins.size()) {
                    selectedBinId = availableBins.get(position - 1).getId();
                }
            });
        });
    }

    private void loadItemData() {
        repository.getItemById(currentItemId).observe(this, item -> {
            if (item != null) {
                currentItem = item;
                binding.etPartName.setText(item.getName());
                binding.etPartBarcode.setText(item.getBarcodeSku());
                binding.etGroupCategory.setText(item.getGroupCategory());
                binding.etQuantity.setText(String.valueOf(item.getQuantity()));
                binding.etMinStockAlert.setText(String.valueOf(item.getMinStockAlert()));
                binding.etUnitPrice.setText(String.valueOf(item.getUnitPrice()));
                binding.etUnitType.setText(item.getUnitType());
                binding.etDescription.setText(item.getDescription());

                selectedBinId = item.getBinId();
                binding.actvBinLocation.setText(item.getLocationBin() != null ? item.getLocationBin() : "", false);

                selectedImagePath = item.getLocalImagePath();
                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    Glide.with(this)
                            .load(new File(selectedImagePath))
                            .centerCrop()
                            .into(binding.ivPartImage);
                }
            }
        });
    }

    private void copyImageToLocalCache(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            File destDir = new File(getFilesDir(), "part_images");
            if (!destDir.exists()) destDir.mkdirs();

            File destFile = new File(destDir, "part_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int read;
            if (is != null) {
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                is.close();
            }
            fos.close();

            selectedImagePath = destFile.getAbsolutePath();
            Glide.with(this)
                    .load(destFile)
                    .centerCrop()
                    .into(binding.ivPartImage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveItem() {
        String name = binding.etPartName.getText().toString().trim();
        String barcode = binding.etPartBarcode.getText().toString().trim();
        String category = binding.etGroupCategory.getText().toString().trim();
        String binLocationStr = binding.actvBinLocation.getText().toString().trim();
        String qtyStr = binding.etQuantity.getText().toString().trim();
        String minAlertStr = binding.etMinStockAlert.getText().toString().trim();
        String priceStr = binding.etUnitPrice.getText().toString().trim();
        String unitType = binding.etUnitType.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etPartName.setError("Part name is required");
            return;
        }

        if (barcode.isEmpty()) {
            binding.etPartBarcode.setError("Barcode / SKU is required");
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (Exception ignored) {}

        int minAlert = 5;
        try {
            minAlert = Integer.parseInt(minAlertStr);
        } catch (Exception ignored) {}

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception ignored) {}

        long now = System.currentTimeMillis();

        if (currentItem != null) {
            currentItem.setName(name);
            currentItem.setBarcodeSku(barcode);
            currentItem.setGroupCategory(category.isEmpty() ? "General" : category);
            currentItem.setBinId(selectedBinId);
            currentItem.setLocationBin(binLocationStr.isEmpty() ? "Unassigned" : binLocationStr);
            currentItem.setQuantity(quantity);
            currentItem.setMinStockAlert(minAlert);
            currentItem.setUnitPrice(price);
            currentItem.setUnitType(unitType.isEmpty() ? "Pcs" : unitType);
            currentItem.setDescription(desc);
            currentItem.setLocalImagePath(selectedImagePath);
            currentItem.setUpdatedAt(now);

            repository.updateItem(currentItem);
            Toast.makeText(this, "Part updated successfully", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            InventoryItem newItem = new InventoryItem(
                    name,
                    barcode,
                    category.isEmpty() ? "General" : category,
                    selectedBinId,
                    binLocationStr.isEmpty() ? "Unassigned" : binLocationStr,
                    quantity,
                    minAlert,
                    price,
                    unitType.isEmpty() ? "Pcs" : unitType,
                    desc,
                    null,
                    selectedImagePath,
                    now,
                    now
            );

            final int finalQuantity = quantity;
            repository.insertItem(newItem, itemId -> {
                if (finalQuantity > 0) {
                    repository.recordStockTransaction(itemId, "STOCK_IN", finalQuantity, "Initial Stock", "admin");
                }
                Toast.makeText(this, "Part saved to inventory", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
