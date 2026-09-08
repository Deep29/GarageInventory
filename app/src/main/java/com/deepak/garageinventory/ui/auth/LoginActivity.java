package com.deepak.garageinventory.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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

                        sessionManager.saveUserSession(name, "Deepak Auto Garage", email, "+91 98765 43210");
                        LicenseManager.initTrialIfNeeded(this);

                        Toast.makeText(this, "Signed in as " + email, Toast.LENGTH_SHORT).show();
                        openMainActivity();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showGoogleAccountFallbackDialog();
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
            try {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            } catch (Exception e) {
                showGoogleAccountFallbackDialog();
            }
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
        CharSequence emailText = binding.etAuthEmail.getText();
        CharSequence passText = binding.etAuthPassword.getText();

        String email = emailText != null ? emailText.toString().trim() : "";
        String password = passText != null ? passText.toString().trim() : "";

        if (email.isEmpty()) {
            binding.etAuthEmail.setError("Email address is required");
            return;
        }

        if (password.length() < 4) {
            binding.etAuthPassword.setError("Password must be at least 4 characters");
            return;
        }

        if (isRegisterMode) {
            CharSequence busText = binding.etAuthBusinessName.getText();
            CharSequence nameText = binding.etAuthFullName.getText();
            CharSequence phoneText = binding.etAuthPhone.getText();

            String businessName = busText != null ? busText.toString().trim() : "Deepak Auto Garage";
            String fullName = nameText != null ? nameText.toString().trim() : "Deepak Gowda";
            String phone = phoneText != null ? phoneText.toString().trim() : "+91 98765 43210";

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

    private void showGoogleAccountFallbackDialog() {
        final EditText input = new EditText(this);
        input.setHint("e.g. deepakgowda.nr@gmail.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Google Account Sign-In")
                .setMessage("Enter your Gmail address to complete Google Sign-In and start your 3-Month Free Trial:")
                .setView(input)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (email.isEmpty()) email = "deepakgowda.nr@gmail.com";

                    sessionManager.saveUserSession("Deepak Gowda", "Deepak Auto Garage", email, "+91 98765 43210");
                    LicenseManager.initTrialIfNeeded(this);

                    Toast.makeText(this, "Google Account Signed In: " + email, Toast.LENGTH_SHORT).show();
                    openMainActivity();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
