package com.deepak.garageinventory.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityItemDetailBinding;
import com.deepak.garageinventory.databinding.DialogStockAdjustmentBinding;

import java.io.File;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity {

    private ActivityItemDetailBinding binding;
    private InventoryRepository repository;
    private long itemId = -1;
    private InventoryItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("extra_item_id")) {
            itemId = getIntent().getLongExtra("extra_item_id", -1);
        }

        if (itemId != -1) {
            loadItemDetails();
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnStockIn.setOnClickListener(v -> showStockAdjustmentDialog("STOCK_IN"));
        binding.btnStockOut.setOnClickListener(v -> showStockAdjustmentDialog("STOCK_OUT"));

        binding.btnEditPart.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditItemActivity.class);
            intent.putExtra("extra_item_id", itemId);
            startActivity(intent);
        });

        binding.btnDeletePart.setOnClickListener(v -> confirmDeleteItem());
    }

    private void loadItemDetails() {
        repository.getItemById(itemId).observe(this, item -> {
            if (item != null) {
                currentItem = item;
                binding.tvDetailPartName.setText(item.getName());
                binding.tvDetailBarcode.setText("Barcode / SKU: " + item.getBarcodeSku());
                binding.tvDetailQuantity.setText(item.getQuantity() + " " + item.getUnitType());
                binding.tvDetailUnitPrice.setText(String.format(Locale.US, "$%.2f", item.getUnitPrice()));
                binding.tvDetailCategory.setText("Category: " + item.getGroupCategory());
                binding.tvDetailBinLocation.setText("Bin Location: " + item.getLocationBin());
                binding.tvDetailMinAlert.setText("Low Stock Threshold: " + item.getMinStockAlert() + " " + item.getUnitType());
                binding.tvDetailDescription.setText(item.getDescription() != null ? item.getDescription() : "No notes");

                if (item.getLocalImagePath() != null && !item.getLocalImagePath().isEmpty()) {
                    Glide.with(this)
                            .load(new File(item.getLocalImagePath()))
                            .centerCrop()
                            .into(binding.ivDetailPartImage);
                } else {
                    binding.ivDetailPartImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        });
    }

    private void showStockAdjustmentDialog(String type) {
        if (currentItem == null) return;

        DialogStockAdjustmentBinding dialogBinding = DialogStockAdjustmentBinding.inflate(LayoutInflater.from(this));

        boolean isStockIn = "STOCK_IN".equalsIgnoreCase(type);
        dialogBinding.tvDialogTitle.setText(isStockIn ? "Stock IN (+)" : "Stock OUT (-)");

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String qtyStr = dialogBinding.etAdjustmentQty.getText().toString().trim();
                    String notes = dialogBinding.etAdjustmentNotes.getText().toString().trim();

                    int changeQty = 1;
                    try {
                        changeQty = Integer.parseInt(qtyStr);
                    } catch (Exception ignored) {}

                    if (changeQty <= 0) {
                        Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    repository.recordStockTransaction(
                            itemId,
                            type,
                            changeQty,
                            notes.isEmpty() ? (isStockIn ? "Stock Addition" : "Stock Consumption") : notes,
                            "admin"
                    );

                    Toast.makeText(this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteItem() {
        if (currentItem == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Part")
                .setMessage("Are you sure you want to permanently delete '" + currentItem.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteItem(currentItem);
                    Toast.makeText(this, "Part deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
