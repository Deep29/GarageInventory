package com.deepak.garageinventory.ui.billing;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deepak.garageinventory.databinding.ActivityReceiptSettingsBinding;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiptSettingsActivity extends AppCompatActivity {

    private static final int PERMISSION_BT_REQUEST = 2001;

    private ActivityReceiptSettingsBinding binding;
    private ReceiptSettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsManager = new ReceiptSettingsManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadSavedSettings();

        binding.btnPairBtPrinter.setOnClickListener(v -> checkBtPermissionAndSelectDevice());
        binding.btnSaveReceiptSettings.setOnClickListener(v -> saveSettings());
    }

    private void loadSavedSettings() {
        binding.etShopName.setText(settingsManager.getShopName());
        binding.etShopAddress.setText(settingsManager.getShopAddress());
        binding.etShopPhone.setText(settingsManager.getShopPhone());
        binding.etGstTaxRate.setText(String.valueOf(settingsManager.getGstTaxRate()));
        binding.etFooterNote.setText(settingsManager.getFooterNote());
        binding.tvPairedPrinter.setText("Paired Printer: " + settingsManager.getBtDeviceName());
    }

    private void checkBtPermissionAndSelectDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        PERMISSION_BT_REQUEST
                );
                return;
            }
        }
        selectPairedBluetoothPrinter();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_BT_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPairedBluetoothPrinter();
            } else {
                Toast.makeText(this, "Bluetooth permission required for thermal printing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void selectPairedBluetoothPrinter() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is disabled. Please turn on Bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.isEmpty()) {
            Toast.makeText(this, "No paired Bluetooth devices found. Pair thermal printer in Android Settings first.", Toast.LENGTH_LONG).show();
            return;
        }

        List<BluetoothDevice> deviceList = new ArrayList<>(pairedDevices);
        List<String> deviceNames = new ArrayList<>();
        for (BluetoothDevice device : deviceList) {
            deviceNames.add(device.getName() + " (" + device.getAddress() + ")");
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Bluetooth Thermal Printer")
                .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames), (dialog, which) -> {
                    BluetoothDevice selectedDevice = deviceList.get(which);
                    settingsManager.setBtPrinter(selectedDevice.getAddress(), selectedDevice.getName());
                    binding.tvPairedPrinter.setText("Paired Printer: " + selectedDevice.getName());
                    Toast.makeText(this, "Printer Selected: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void saveSettings() {
        String name = binding.etShopName.getText().toString().trim();
        String address = binding.etShopAddress.getText().toString().trim();
        String phone = binding.etShopPhone.getText().toString().trim();
        String taxStr = binding.etGstTaxRate.getText().toString().trim();
        String footer = binding.etFooterNote.getText().toString().trim();

        float taxRate = 0.0f;
        try {
            taxRate = Float.parseFloat(taxStr);
        } catch (Exception ignored) {}

        settingsManager.setShopName(name.isEmpty() ? "Deepak Auto Garage" : name);
        settingsManager.setShopAddress(address);
        settingsManager.setShopPhone(phone);
        settingsManager.setGstTaxRate(taxRate);
        settingsManager.setFooterNote(footer);

        Toast.makeText(this, "Receipt settings saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
