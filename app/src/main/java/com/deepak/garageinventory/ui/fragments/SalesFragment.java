package com.deepak.garageinventory.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.FragmentSalesBinding;
import com.deepak.garageinventory.ui.billing.CreateInvoiceActivity;
import com.deepak.garageinventory.ui.billing.InvoiceHistoryAdapter;
import com.deepak.garageinventory.ui.reports.ProfitLossActivity;
import com.deepak.garageinventory.utils.BluetoothThermalPrinterHelper;

public class SalesFragment extends Fragment implements InvoiceHistoryAdapter.OnInvoiceClickListener {

    private FragmentSalesBinding binding;
    private InventoryRepository repository;
    private InvoiceHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSalesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return;
        repository = new InventoryRepository(getActivity().getApplication());

        adapter = new InvoiceHistoryAdapter(this);
        binding.rvSalesInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSalesInvoices.setAdapter(adapter);

        binding.btnNavNewBill.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateInvoiceActivity.class);
            startActivity(intent);
        });

        binding.fabCreateInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateInvoiceActivity.class);
            startActivity(intent);
        });

        binding.btnNavProfitLoss.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfitLossActivity.class);
            startActivity(intent);
        });

        loadInvoices();
    }

    private void loadInvoices() {
        repository.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            adapter.setInvoiceList(invoices);
        });
    }

    @Override
    public void onInvoiceClick(CustomerInvoice invoice) {
        if (getActivity() == null) return;
        repository.getItemsForInvoice(invoice.getId()).observe(getViewLifecycleOwner(), items -> {
            BluetoothThermalPrinterHelper.printInvoiceReceipt(getActivity(), invoice, items, new BluetoothThermalPrinterHelper.OnPrintListener() {
                @Override
                public void onPrintSuccess() {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Receipt Printed!", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onPrintFailed(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Print Error: " + error, Toast.LENGTH_LONG).show());
                    }
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
