package com.deepak.garageinventory.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.FragmentDashboardBinding;
import com.deepak.garageinventory.ui.billing.CreateInvoiceActivity;
import com.deepak.garageinventory.ui.expenses.ExpensesActivity;
import com.deepak.garageinventory.ui.inventory.InventoryAdapter;
import com.deepak.garageinventory.ui.inventory.ItemDetailActivity;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;
import com.deepak.garageinventory.utils.LicenseManager;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private InventoryRepository repository;
    private InventoryAdapter lowStockAdapter;

    private double totalSales = 0.0;
    private double totalPurchases = 0.0;
    private double totalExpenses = 0.0;

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

        lowStockAdapter = new InventoryAdapter(new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InventoryItem item) {
                Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                intent.putExtra("extra_item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(InventoryItem item) {
                repository.deleteItem(item);
            }
        });

        binding.rvHomeLowStock.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHomeLowStock.setAdapter(lowStockAdapter);

        binding.btnHeaderScan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            startActivity(intent);
        });

        binding.btnHomeCreateBill.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateInvoiceActivity.class);
            startActivity(intent);
        });

        binding.btnHomeAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExpensesActivity.class);
            startActivity(intent);
        });

        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        repository.getTotalSalesAmount().observe(getViewLifecycleOwner(), sales -> {
            totalSales = sales != null ? sales : 0.0;
            binding.tvHomeSales.setText(String.format(Locale.US, "$%.2f", totalSales));
            updateNetProfit();
        });

        repository.getTotalCustomerDues().observe(getViewLifecycleOwner(), dues -> {
            double totalVal = dues != null ? dues : 0.0;
            binding.tvHomeDues.setText(String.format(Locale.US, "$%.2f", totalVal));
        });

        repository.getTotalInventoryValue().observe(getViewLifecycleOwner(), val -> {
            double totalVal = val != null ? val : 0.0;
            binding.tvHomeStockValuation.setText(String.format(Locale.US, "$%.2f", totalVal));
        });

        repository.getTotalExpensesAmount().observe(getViewLifecycleOwner(), expenses -> {
            totalExpenses = expenses != null ? expenses : 0.0;
            binding.tvHomeTotalExpenses.setText(String.format(Locale.US, "$%.2f", totalExpenses));
            updateNetProfit();
        });

        repository.getTotalPurchasesAmount().observe(getViewLifecycleOwner(), purchases -> {
            totalPurchases = purchases != null ? purchases : 0.0;
            updateNetProfit();
        });

        repository.getLowStockItems().observe(getViewLifecycleOwner(), lowItems -> {
            lowStockAdapter.setItemList(lowItems);
        });
    }

    private void updateNetProfit() {
        double netProfit = totalSales - totalPurchases - totalExpenses;
        String formattedVal = String.format(Locale.US, "$%.2f", netProfit);
        binding.tvHomeNetProfit.setText(formattedVal);

        if (netProfit < 0) {
            binding.tvHomeNetProfit.setTextColor(Color.parseColor("#D32F2F")); // Red Loss
        } else {
            binding.tvHomeNetProfit.setTextColor(Color.parseColor("#2E7D32")); // Green Profit
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
