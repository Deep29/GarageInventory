package com.deepak.garageinventory.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.FragmentDashboardBinding;
import com.deepak.garageinventory.ui.billing.CreateInvoiceActivity;
import com.deepak.garageinventory.ui.billing.InvoiceHistoryActivity;
import com.deepak.garageinventory.ui.bin.BinListActivity;
import com.deepak.garageinventory.ui.bin.BulkBinPrintActivity;
import com.deepak.garageinventory.ui.expenses.ExpensesActivity;
import com.deepak.garageinventory.ui.inventory.AddEditItemActivity;
import com.deepak.garageinventory.ui.inventory.InventoryListActivity;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;
import com.deepak.garageinventory.utils.LicenseManager;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private InventoryRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return;
        repository = new InventoryRepository(getActivity().getApplication());

        ReceiptSettingsManager settingsManager = new ReceiptSettingsManager(getActivity());
        binding.tvShopHeaderName.setText(settingsManager.getShopName());
        binding.tvLicenseSummaryHeader.setText(LicenseManager.getLicenseStatusSummary(getActivity()));

        // Bind Action Tiles Navigation
        binding.tileCreateBill.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateInvoiceActivity.class);
            startActivity(intent);
        });

        binding.tileViewStock.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InventoryListActivity.class);
            startActivity(intent);
        });

        binding.tileAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditItemActivity.class);
            startActivity(intent);
        });

        binding.tileScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            startActivity(intent);
        });

        binding.tileManageBins.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BinListActivity.class);
            startActivity(intent);
        });

        binding.tileSeriesBarcodes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BulkBinPrintActivity.class);
            startActivity(intent);
        });

        binding.tileBillHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InvoiceHistoryActivity.class);
            startActivity(intent);
        });

        binding.tileExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExpensesActivity.class);
            startActivity(intent);
        });

        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        repository.getTotalSalesAmount().observe(getViewLifecycleOwner(), sales -> {
            double totalVal = sales != null ? sales : 0.0;
            binding.tvHomeSales.setText(String.format(Locale.US, "$%.2f", totalVal));
        });

        repository.getTotalInventoryValue().observe(getViewLifecycleOwner(), val -> {
            double totalVal = val != null ? val : 0.0;
            binding.tvHomeStockValuation.setText(String.format(Locale.US, "$%.2f", totalVal));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
