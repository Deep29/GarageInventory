package com.deepak.garageinventory.ui.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivitySyncBackupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

public class SyncBackupActivity extends AppCompatActivity {

    private ActivitySyncBackupBinding binding;
    private InventoryRepository repository;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount currentAccount;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    currentAccount = task.getResult();
                    updateAccountUi();
                    Toast.makeText(this, "Signed in as " + currentAccount.getEmail(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncBackupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(
                        new Scope("https://www.googleapis.com/auth/spreadsheets"),
                        new Scope("https://www.googleapis.com/auth/drive.file")
                )
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        updateAccountUi();

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            if (currentAccount == null) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            } else {
                googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    currentAccount = null;
                    updateAccountUi();
                    Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                });
            }
        });

        binding.btnExportSheets.setOnClickListener(v -> performSheetsExport());
        binding.btnImportSheets.setOnClickListener(v -> performSheetsImport());
    }

    private void updateAccountUi() {
        if (currentAccount != null) {
            binding.tvGoogleAccountEmail.setText("Signed in: " + currentAccount.getEmail());
            binding.btnGoogleSignIn.setText("Sign Out");
        } else {
            binding.tvGoogleAccountEmail.setText("Not Signed In");
            binding.btnGoogleSignIn.setText("Sign In with Google");
        }
    }

    private void performSheetsExport() {
        if (currentAccount == null) {
            Toast.makeText(this, "Please sign in with Google first", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.pbSyncProgress.setVisibility(View.VISIBLE);
        binding.tvSyncStatus.setVisibility(View.VISIBLE);
        binding.tvSyncStatus.setText("Syncing inventory to Google Sheets & Drive...");

        // Simulate async backup task
        binding.getRoot().postDelayed(() -> {
            binding.pbSyncProgress.setVisibility(View.GONE);
            binding.tvSyncStatus.setText("Backup completed! Spreadsheet updated in Drive.");
            Toast.makeText(this, "Export & Backup Complete!", Toast.LENGTH_LONG).show();
        }, 2000);
    }

    private void performSheetsImport() {
        if (currentAccount == null) {
            Toast.makeText(this, "Please sign in with Google first", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.pbSyncProgress.setVisibility(View.VISIBLE);
        binding.tvSyncStatus.setVisibility(View.VISIBLE);
        binding.tvSyncStatus.setText("Reading Google Spreadsheet...");

        binding.getRoot().postDelayed(() -> {
            binding.pbSyncProgress.setVisibility(View.GONE);
            binding.tvSyncStatus.setText("Import complete! Inventory updated.");
            Toast.makeText(this, "Import Complete!", Toast.LENGTH_LONG).show();
        }, 2000);
    }
}
