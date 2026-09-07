package com.deepak.garageinventory.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.CustomerKhata;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityCustomerKhataBinding;
import com.deepak.garageinventory.databinding.DialogAddPartyBinding;
import com.deepak.garageinventory.databinding.FragmentPartiesBinding;
import com.deepak.garageinventory.ui.khata.CustomerKhataAdapter;

import java.util.Locale;

public class PartiesFragment extends Fragment {

    private FragmentPartiesBinding binding;
    private InventoryRepository repository;
    private CustomerKhataAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPartiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return;
        repository = new InventoryRepository(getActivity().getApplication());

        adapter = new CustomerKhataAdapter();
        binding.rvPartiesList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPartiesList.setAdapter(adapter);

        binding.fabAddCustomer.setOnClickListener(v -> showAddPartyDialog());

        loadCustomers();
    }

    private void loadCustomers() {
        repository.getAllCustomers().observe(getViewLifecycleOwner(), customers -> {
            adapter.setPartyList(customers);
        });

        repository.getTotalCustomerDues().observe(getViewLifecycleOwner(), totalDues -> {
            double totalVal = totalDues != null ? totalDues : 0.0;
            binding.tvKhataTotalDues.setText(String.format(Locale.US, "$%.2f", totalVal));
        });
    }

    private void showAddPartyDialog() {
        if (getContext() == null) return;
        DialogAddPartyBinding dialogBinding = DialogAddPartyBinding.inflate(LayoutInflater.from(getContext()));

        new AlertDialog.Builder(getContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save Customer", (dialog, which) -> {
                    String name = dialogBinding.etPartyName.getText().toString().trim();
                    String phone = dialogBinding.etPartyPhone.getText().toString().trim();
                    String balanceStr = dialogBinding.etOpeningBalance.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Customer name is required", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Customer added to Khata", Toast.LENGTH_SHORT).show();
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
