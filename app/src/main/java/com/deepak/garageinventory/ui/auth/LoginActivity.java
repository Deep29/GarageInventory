package com.deepak.garageinventory.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.MainActivity;
import com.deepak.garageinventory.databinding.ActivityLoginBinding;
import com.deepak.garageinventory.utils.LicenseManager;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;
import com.deepak.garageinventory.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private ReceiptSettingsManager settingsManager;
    private boolean isRegisterMode = false;

    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult();
                    if (account != null) {
                        String name = account.getDisplayName() != null ? account.getDisplayName() : "Google User";
                        String email = account.getEmail() != null ? account.getEmail() : "user@gmail.com";

                        sessionManager.saveUserSession(name, "My Business", email, "+91 98765 43210");
                        LicenseManager.initTrialIfNeeded(this);

                        Toast.makeText(this, "Signed in as " + email, Toast.LENGTH_SHORT).show();
                        openMainActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        settingsManager = new ReceiptSettingsManager(this);

        // Auto-redirect if already logged in
        if (sessionManager.isLoggedIn()) {
            openMainActivity();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        setupAuthToggle();

        binding.btnSubmitAuth.setOnClickListener(v -> performAuthSubmit());
        binding.btnAuthGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void setupAuthToggle() {
        binding.btnTabSignIn.setOnClickListener(v -> {
            isRegisterMode = false;
            binding.layoutRegisterFields.setVisibility(View.GONE);
            binding.btnSubmitAuth.setText("Sign In to BizMaster");
        });

        binding.btnTabRegister.setOnClickListener(v -> {
            isRegisterMode = true;
            binding.layoutRegisterFields.setVisibility(View.VISIBLE);
            binding.btnSubmitAuth.setText("Create Account & Start Free Trial");
        });
    }

    private void performAuthSubmit() {
        String email = binding.etAuthEmail.getText().toString().trim();
        String password = binding.etAuthPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etAuthEmail.setError("Email address is required");
            return;
        }

        if (password.length() < 4) {
            binding.etAuthPassword.setError("Password must be at least 4 characters");
            return;
        }

        if (isRegisterMode) {
            String businessName = binding.etAuthBusinessName.getText().toString().trim();
            String fullName = binding.etAuthFullName.getText().toString().trim();
            String phone = binding.etAuthPhone.getText().toString().trim();

            if (businessName.isEmpty()) businessName = "Deepak Auto Garage";
            if (fullName.isEmpty()) fullName = "Deepak Gowda";
            if (phone.isEmpty()) phone = "+91 98765 43210";

            settingsManager.setShopName(businessName);
            settingsManager.setShopPhone(phone);

            sessionManager.saveUserSession(fullName, businessName, email, phone);
            LicenseManager.initTrialIfNeeded(this);

            Toast.makeText(this, "Registration Successful! 3-Month Free Trial Started.", Toast.LENGTH_LONG).show();

        } else {
            sessionManager.saveUserSession("Deepak Gowda", settingsManager.getShopName(), email, "+91 98765 43210");
            LicenseManager.initTrialIfNeeded(this);

            Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
        }

        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
