package com.deepak.garageinventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.deepak.garageinventory.databinding.ActivityMainBinding;
import com.deepak.garageinventory.ui.auth.LoginActivity;
import com.deepak.garageinventory.ui.billing.ReceiptSettingsActivity;
import com.deepak.garageinventory.ui.fragments.DashboardFragment;
import com.deepak.garageinventory.ui.fragments.PartiesFragment;
import com.deepak.garageinventory.ui.fragments.SalesFragment;
import com.deepak.garageinventory.ui.fragments.StockFragment;
import com.deepak.garageinventory.ui.profile.UserProfileActivity;
import com.deepak.garageinventory.ui.subscription.SubscriptionActivity;
import com.deepak.garageinventory.ui.sync.SyncBackupActivity;
import com.deepak.garageinventory.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        setupBottomNavigation();

        // Default to Dashboard tab
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_sales) {
                fragment = new SalesFragment();
            } else if (itemId == R.id.nav_stock) {
                fragment = new StockFragment();
            } else if (itemId == R.id.nav_khata) {
                fragment = new PartiesFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_user_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        } else if (id == R.id.menu_printer_settings) {
            startActivity(new Intent(this, ReceiptSettingsActivity.class));
            return true;
        } else if (id == R.id.menu_sheets_sync) {
            startActivity(new Intent(this, SyncBackupActivity.class));
            return true;
        } else if (id == R.id.menu_saas_subscription) {
            startActivity(new Intent(this, SubscriptionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
