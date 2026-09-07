package com.deepak.garageinventory.ui.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.BusinessExpense;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityExpensesBinding;
import com.deepak.garageinventory.databinding.DialogAddExpenseBinding;

import java.util.Locale;

public class ExpensesActivity extends AppCompatActivity {

    private ActivityExpensesBinding binding;
    private InventoryRepository repository;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new ExpenseAdapter();
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpenses.setAdapter(adapter);

        binding.btnAddExpense.setOnClickListener(v -> showAddExpenseDialog());

        loadExpenses();
    }

    private void loadExpenses() {
        repository.getAllExpenses().observe(this, expenses -> {
            adapter.setExpenseList(expenses);
        });

        repository.getTotalExpensesAmount().observe(this, total -> {
            double totalVal = total != null ? total : 0.0;
            binding.tvTotalExpenses.setText(String.format(Locale.US, "$%.2f", totalVal));
        });
    }

    private void showAddExpenseDialog() {
        DialogAddExpenseBinding dialogBinding = DialogAddExpenseBinding.inflate(LayoutInflater.from(this));

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save Expense", (dialog, which) -> {
                    String category = dialogBinding.etExpenseCategory.getText().toString().trim();
                    String amountStr = dialogBinding.etExpenseAmount.getText().toString().trim();
                    String notes = dialogBinding.etExpenseNotes.getText().toString().trim();

                    if (category.isEmpty()) category = "General Expense";

                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (Exception ignored) {}

                    if (amount <= 0) {
                        Toast.makeText(this, "Expense amount must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BusinessExpense newExpense = new BusinessExpense(
                            category,
                            amount,
                            notes,
                            System.currentTimeMillis()
                    );

                    repository.insertExpense(newExpense);
                    Toast.makeText(this, "Expense recorded successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
