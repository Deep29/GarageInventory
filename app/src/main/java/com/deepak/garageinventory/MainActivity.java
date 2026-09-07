package com.deepak.garageinventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityMainBinding;
import com.deepak.garageinventory.ui.billing.CreateInvoiceActivity;
import com.deepak.garageinventory.ui.billing.InvoiceHistoryActivity;
import com.deepak.garageinventory.ui.billing.ReceiptSettingsActivity;
import com.deepak.garageinventory.ui.bin.BinListActivity;
import com.deepak.garageinventory.ui.inventory.AddEditItemActivity;
import com.deepak.garageinventory.ui.inventory.InventoryListActivity;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;
import com.deepak.garageinventory.ui.subscription.SubscriptionActivity;
import com.deepak.garageinventory.ui.sync.SyncBackupActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private InventoryRepository repository;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String scannedCode = result.getData().getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT);
                    if (scannedCode != null) {
                        handleScannedCode(scannedCode);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new InventoryRepository(getApplication());

        setupDashboardMetrics();
        setupNavigationActions();
    }

    private void setupDashboardMetrics() {
        repository.getTotalItemCount().observe(this, count -> {
            binding.tvTotalItems.setText(String.valueOf(count != null ? count : 0));
        });

        repository.getAllBins().observe(this, bins -> {
            binding.tvTotalBins.setText(String.valueOf(bins != null ? bins.size() : 0));
        });

        repository.getLowStockCount().observe(this, lowCount -> {
            binding.tvLowStockCount.setText(String.valueOf(lowCount != null ? lowCount : 0));
        });

        repository.getTotalInventoryValue().observe(this, val -> {
            double totalVal = val != null ? val : 0.0;
            binding.tvStockValuation.setText(String.format(Locale.US, "$%.2f", totalVal));
        });
    }

    private void setupNavigationActions() {
        // Billing & Invoicing Actions
        binding.cardCreateBill.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateInvoiceActivity.class);
            startActivity(intent);
        });

        binding.btnBillHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceHistoryActivity.class);
            startActivity(intent);
        });

        binding.btnReceiptSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReceiptSettingsActivity.class);
            startActivity(intent);
        });

        // View Available Parts & Stock List
        binding.cardViewParts.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryListActivity.class);
            startActivity(intent);
        });

        binding.cardTotalParts.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryListActivity.class);
            startActivity(intent);
        });

        binding.cardLowStock.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryListActivity.class);
            startActivity(intent);
        });

        // Add New Item Form
        binding.cardAddNewItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditItemActivity.class);
            startActivity(intent);
        });

        // Camera Barcode / QR Scanner
        binding.cardScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        // Storage Bins & Label Printing
        binding.cardManageBins.setOnClickListener(v -> {
            Intent intent = new Intent(this, BinListActivity.class);
            startActivity(intent);
        });

        binding.cardBins.setOnClickListener(v -> {
            Intent intent = new Intent(this, BinListActivity.class);
            startActivity(intent);
        });

        // Import & Export Google Sheets
        binding.btnImportExport.setOnClickListener(v -> {
            Intent intent = new Intent(this, SyncBackupActivity.class);
            startActivity(intent);
        });

        // Account Registration & SaaS Subscription
        binding.btnNavSubscription.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubscriptionActivity.class);
            startActivity(intent);
        });
    }

    private void handleScannedCode(String scannedCode) {
        Toast.makeText(this, "Scanned Code: " + scannedCode, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, InventoryListActivity.class);
        startActivity(intent);
    }
}
