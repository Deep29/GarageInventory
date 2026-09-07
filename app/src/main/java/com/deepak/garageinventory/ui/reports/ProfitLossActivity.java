package com.deepak.garageinventory.ui.reports;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityProfitLossBinding;

import java.util.Locale;

public class ProfitLossActivity extends AppCompatActivity {

    private ActivityProfitLossBinding binding;
    private InventoryRepository repository;

    private double totalSales = 0.0;
    private double totalPurchases = 0.0;
    private double totalExpenses = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfitLossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        calculateProfitAndLoss();
    }

    private void calculateProfitAndLoss() {
        repository.getTotalSalesAmount().observe(this, sales -> {
            totalSales = sales != null ? sales : 0.0;
            binding.tvSalesRevenue.setText(String.format(Locale.US, "$%.2f", totalSales));
            updateNetProfit();
        });

        repository.getTotalPurchasesAmount().observe(this, purchases -> {
            totalPurchases = purchases != null ? purchases : 0.0;
            binding.tvPurchasesCost.setText(String.format(Locale.US, "$%.2f", totalPurchases));
            updateNetProfit();
        });

        repository.getTotalExpensesAmount().observe(this, expenses -> {
            totalExpenses = expenses != null ? expenses : 0.0;
            binding.tvOperatingExpenses.setText(String.format(Locale.US, "$%.2f", totalExpenses));
            updateNetProfit();
        });
    }

    private void updateNetProfit() {
        double netProfit = totalSales - totalPurchases - totalExpenses;
        String formattedVal = String.format(Locale.US, "$%.2f", netProfit);

        binding.tvNetProfit.setText(formattedVal);
        binding.tvCalculatedNetProfit.setText(formattedVal);

        if (netProfit < 0) {
            binding.tvNetProfit.setTextColor(Color.parseColor("#C62828")); // Red Loss
        } else {
            binding.tvNetProfit.setTextColor(Color.parseColor("#2E7D32")); // Green Profit
        }
    }
}
