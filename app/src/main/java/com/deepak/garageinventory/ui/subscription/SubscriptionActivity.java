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

public class SubscriptionActivity extends AppCompatActivity {

    private ActivitySubscriptionBinding binding;
    private BillingClient billingClient;

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Toast.makeText(this, "Subscription Purchase Successful! Active License Granted.", Toast.LENGTH_LONG).show();
            binding.tvLicenseStatus.setText("Status: Active Pro License (Subscribed)");
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

        setupBillingClient();

        binding.btnSubscribeMonthly.setOnClickListener(v -> launchSubscriptionFlow("monthly_pro_plan"));
        binding.btnSubscribeYearly.setOnClickListener(v -> launchSubscriptionFlow("yearly_pro_plan"));
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
            // Google Play Billing Purchase Flow Handler
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
