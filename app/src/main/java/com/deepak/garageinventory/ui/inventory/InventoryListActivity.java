package com.deepak.garageinventory.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityInventoryListBinding;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;

import java.util.List;

public class InventoryListActivity extends AppCompatActivity implements InventoryAdapter.OnItemClickListener {

    private ActivityInventoryListBinding binding;
    private InventoryRepository repository;
    private InventoryAdapter adapter;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String scannedCode = result.getData().getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT);
                    if (scannedCode != null) {
                        binding.etSearch.setText(scannedCode);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new InventoryAdapter(this);
        binding.rvInventory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInventory.setAdapter(adapter);

        binding.fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditItemActivity.class);
            startActivity(intent);
        });

        binding.btnScanSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchInventory(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAllInventory();
    }

    private void loadAllInventory() {
        repository.getAllItems().observe(this, items -> {
            updateList(items);
        });
    }

    private void searchInventory(String query) {
        if (query.isEmpty()) {
            loadAllInventory();
        } else {
            repository.searchItems(query).observe(this, this::updateList);
        }
    }

    private void updateList(List<InventoryItem> items) {
        if (items == null || items.isEmpty()) {
            binding.tvEmptyInventory.setVisibility(View.VISIBLE);
            binding.rvInventory.setVisibility(View.GONE);
        } else {
            binding.tvEmptyInventory.setVisibility(View.GONE);
            binding.rvInventory.setVisibility(View.VISIBLE);
            adapter.setItemList(items);
        }
    }

    @Override
    public void onItemClick(InventoryItem item) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("extra_item_id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(InventoryItem item) {
        if (item == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete Part")
                .setMessage("Are you sure you want to delete '" + item.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteItem(item);
                    Toast.makeText(this, "Part deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
