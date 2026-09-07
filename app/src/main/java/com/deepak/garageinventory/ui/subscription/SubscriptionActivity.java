package com.deepak.garageinventory.ui.subscription;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.deepak.garageinventory.databinding.ActivitySubscriptionBinding;
import com.deepak.garageinventory.utils.LicenseManager;

public class SubscriptionActivity extends AppCompatActivity {

    private ActivitySubscriptionBinding binding;
    private BillingClient billingClient;

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Toast.makeText(this, "Subscription Purchase Successful! 1-Year Active License Granted.", Toast.LENGTH_LONG).show();
            LicenseManager.activateProductKey(this, "BIZMASTER-PLAYSTORE-1YEAR");
            updateLicenseStatusDisplay();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Purchase Failed: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize 3-month trial if first launch
        LicenseManager.initTrialIfNeeded(this);
        updateLicenseStatusDisplay();

        binding.btnActivateKey.setOnClickListener(v -> {
            String rawKey = binding.etProductKey.getText().toString().trim();
            if (rawKey.isEmpty()) {
                binding.etProductKey.setError("Please enter a product key");
                return;
            }

            boolean success = LicenseManager.activateProductKey(this, rawKey);
            if (success) {
                Toast.makeText(this, "Product Key Activated! 1-Year License Granted.", Toast.LENGTH_LONG).show();
                binding.etProductKey.setText("");
                updateLicenseStatusDisplay();
            } else {
                Toast.makeText(this, "Invalid Product Key format. Use format BIZ-XXXX-XXXX-XXXX", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnGenerateDemoKey.setOnClickListener(v -> {
            String sampleKey = LicenseManager.generateSampleProductKey();
            binding.etProductKey.setText(sampleKey);
            Toast.makeText(this, "Generated Sample 1-Year Product Key", Toast.LENGTH_SHORT).show();
        });

        setupBillingClient();

        binding.btnSubscribeMonthly.setOnClickListener(v -> launchSubscriptionFlow("monthly_pro_plan"));
        binding.btnSubscribeYearly.setOnClickListener(v -> launchSubscriptionFlow("yearly_pro_plan"));
    }

    private void updateLicenseStatusDisplay() {
        String summary = LicenseManager.getLicenseStatusSummary(this);
        binding.tvLicenseSummary.setText(summary);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing client connected successfully
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Retry connection logic
            }
        });
    }

    private void launchSubscriptionFlow(String productId) {
        if (billingClient != null && billingClient.isReady()) {
            Toast.makeText(this, "Connecting to Play Store for " + productId + "...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Connecting to Google Play Store...", Toast.LENGTH_SHORT).show();
            setupBillingClient();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
}
