package com.deepak.garageinventory.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.databinding.ActivityUserProfileBinding;
import com.deepak.garageinventory.ui.auth.LoginActivity;
import com.deepak.garageinventory.ui.subscription.SubscriptionActivity;
import com.deepak.garageinventory.utils.LicenseManager;
import com.deepak.garageinventory.utils.SessionManager;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadUserProfile();

        binding.btnProfileManageLicense.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubscriptionActivity.class);
            startActivity(intent);
        });

        binding.btnLogoutUser.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        binding.tvProfileName.setText(sessionManager.getUserName());
        binding.tvProfileBusiness.setText(sessionManager.getBusinessName());
        binding.tvProfileEmail.setText("Email: " + sessionManager.getUserEmail());
        binding.tvProfilePhone.setText("Mobile: " + sessionManager.getUserPhone());
        binding.tvProfileLicenseStatus.setText(LicenseManager.getLicenseStatusSummary(this));
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Account")
                .setMessage("Are you sure you want to log out of " + sessionManager.getBusinessName() + "?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
