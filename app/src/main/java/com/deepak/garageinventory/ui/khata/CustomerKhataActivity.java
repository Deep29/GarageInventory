package com.deepak.garageinventory.ui.khata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.CustomerKhata;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityCustomerKhataBinding;
import com.deepak.garageinventory.databinding.DialogAddPartyBinding;

import java.util.Locale;

public class CustomerKhataActivity extends AppCompatActivity {

    private ActivityCustomerKhataBinding binding;
    private InventoryRepository repository;
    private CustomerKhataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerKhataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new CustomerKhataAdapter();
        binding.rvParties.setLayoutManager(new LinearLayoutManager(this));
        binding.rvParties.setAdapter(adapter);

        binding.btnAddParty.setOnClickListener(v -> showAddPartyDialog());

        loadCustomers();
    }

    private void loadCustomers() {
        repository.getAllCustomers().observe(this, customers -> {
            adapter.setPartyList(customers);
        });

        repository.getTotalCustomerDues().observe(this, totalDues -> {
            double totalVal = totalDues != null ? totalDues : 0.0;
            binding.tvTotalKhataDues.setText(String.format(Locale.US, "$%.2f", totalVal));
        });
    }

    private void showAddPartyDialog() {
        DialogAddPartyBinding dialogBinding = DialogAddPartyBinding.inflate(LayoutInflater.from(this));

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save Customer", (dialog, which) -> {
                    String name = dialogBinding.etPartyName.getText().toString().trim();
                    String phone = dialogBinding.etPartyPhone.getText().toString().trim();
                    String balanceStr = dialogBinding.etOpeningBalance.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Customer name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double balance = 0.0;
                    try {
                        balance = Double.parseDouble(balanceStr);
                    } catch (Exception ignored) {}

                    CustomerKhata newParty = new CustomerKhata(
                            name,
                            phone,
                            "CUSTOMER",
                            balance,
                            System.currentTimeMillis()
                    );

                    repository.insertParty(newParty);
                    Toast.makeText(this, "Customer added to Khata", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
