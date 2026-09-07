package com.deepak.garageinventory.ui.billing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityInvoiceHistoryBinding;
import com.deepak.garageinventory.utils.BluetoothThermalPrinterHelper;

public class InvoiceHistoryActivity extends AppCompatActivity implements InvoiceHistoryAdapter.OnInvoiceClickListener {

    private ActivityInvoiceHistoryBinding binding;
    private InventoryRepository repository;
    private InvoiceHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvoiceHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new InvoiceHistoryAdapter(this);
        binding.rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInvoices.setAdapter(adapter);

        binding.fabCreateBill.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateInvoiceActivity.class);
            startActivity(intent);
        });

        loadInvoices();
    }

    private void loadInvoices() {
        repository.getAllInvoices().observe(this, invoices -> {
            if (invoices == null || invoices.isEmpty()) {
                binding.tvEmptyInvoices.setVisibility(View.VISIBLE);
                binding.rvInvoices.setVisibility(View.GONE);
            } else {
                binding.tvEmptyInvoices.setVisibility(View.GONE);
                binding.rvInvoices.setVisibility(View.VISIBLE);
                adapter.setInvoiceList(invoices);
            }
        });
    }

    @Override
    public void onInvoiceClick(CustomerInvoice invoice) {
        repository.getItemsForInvoice(invoice.getId()).observe(this, items -> {
            BluetoothThermalPrinterHelper.printInvoiceReceipt(this, invoice, items, new BluetoothThermalPrinterHelper.OnPrintListener() {
                @Override
                public void onPrintSuccess() {
                    runOnUiThread(() -> Toast.makeText(InvoiceHistoryActivity.this, "Receipt Printed!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onPrintFailed(String error) {
                    runOnUiThread(() -> Toast.makeText(InvoiceHistoryActivity.this, "Print Error: " + error, Toast.LENGTH_LONG).show());
                }
            });
        });
    }
}
