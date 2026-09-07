package com.deepak.garageinventory.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.FragmentStockBinding;
import com.deepak.garageinventory.ui.bin.BinListActivity;
import com.deepak.garageinventory.ui.inventory.AddEditItemActivity;
import com.deepak.garageinventory.ui.inventory.InventoryAdapter;
import com.deepak.garageinventory.ui.inventory.ItemDetailActivity;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;

public class StockFragment extends Fragment implements InventoryAdapter.OnItemClickListener {

    private FragmentStockBinding binding;
    private InventoryRepository repository;
    private InventoryAdapter adapter;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    String scannedCode = result.getData().getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT);
                    if (scannedCode != null) {
                        binding.etStockSearch.setText(scannedCode);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return;
        repository = new InventoryRepository(getActivity().getApplication());

        adapter = new InventoryAdapter(this);
        binding.rvStockItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvStockItems.setAdapter(adapter);

        binding.fabAddStockItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditItemActivity.class);
            startActivity(intent);
        });

        binding.btnStockScan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        binding.btnNavBins.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BinListActivity.class);
            startActivity(intent);
        });

        binding.etStockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchStock(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAllStock();
    }

    private void loadAllStock() {
        repository.getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItemList(items);
        });
    }

    private void searchStock(String query) {
        if (query.isEmpty()) {
            loadAllStock();
        } else {
            repository.searchItems(query).observe(getViewLifecycleOwner(), items -> {
                adapter.setItemList(items);
            });
        }
    }

    @Override
    public void onItemClick(InventoryItem item) {
        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
        intent.putExtra("extra_item_id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(InventoryItem item) {
        if (item == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Part")
                .setMessage("Are you sure you want to delete '" + item.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteItem(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
