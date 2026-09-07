package com.deepak.garageinventory.ui.fragments;

import android.content.Intent;
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

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private InventoryRepository repository;
    private InventoryAdapter lowStockAdapter;

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
            double totalVal = sales != null ? sales : 0.0;
            binding.tvHomeSales.setText(String.format(Locale.US, "$%.2f", totalVal));
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
            double totalVal = expenses != null ? expenses : 0.0;
            binding.tvHomeTotalExpenses.setText(String.format(Locale.US, "$%.2f", totalVal));
        });

        repository.getLowStockItems().observe(getViewLifecycleOwner(), lowItems -> {
            lowStockAdapter.setItemList(lowItems);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
