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

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.local.entity.InvoiceItem;
import com.deepak.garageinventory.databinding.ActivityReceiptSettingsBinding;
import com.deepak.garageinventory.utils.BluetoothThermalPrinterHelper;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiptSettingsActivity extends AppCompatActivity {

    private static final int PERMISSION_BT_REQUEST = 2001;

    private ActivityReceiptSettingsBinding binding;
    private ReceiptSettingsManager settingsManager;

    private static final String[] PRINTER_TYPE_OPTIONS = {
            "Bluetooth Thermal Printer",
            "Normal Desktop Printer (Wi-Fi / USB / PDF)"
    };

    private static final String[] PAPER_WIDTH_OPTIONS = {
            "58mm Thermal Paper (32 Chars)",
            "80mm Thermal Paper (48 Chars)"
    };

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

        setupDropdowns();
        loadSavedSettings();

        binding.btnPairBtPrinter.setOnClickListener(v -> checkBtPermissionAndSelectDevice());
        binding.btnTestPrint.setOnClickListener(v -> performTestPrint());
        binding.btnSaveReceiptSettings.setOnClickListener(v -> saveSettings());
    }

    private void setupDropdowns() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                PRINTER_TYPE_OPTIONS
        );
        binding.actvPrinterType.setAdapter(typeAdapter);

        ArrayAdapter<String> widthAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                PAPER_WIDTH_OPTIONS
        );
        binding.actvPaperWidth.setAdapter(widthAdapter);
    }

    private void loadSavedSettings() {
        binding.etShopName.setText(settingsManager.getShopName());
        binding.etShopAddress.setText(settingsManager.getShopAddress());
        binding.etShopPhone.setText(settingsManager.getShopPhone());
        binding.etGstTaxRate.setText(String.valueOf(settingsManager.getGstTaxRate()));
        binding.etFooterNote.setText(settingsManager.getFooterNote());
        binding.tvPairedPrinter.setText("Paired Printer: " + settingsManager.getBtDeviceName());

        String type = settingsManager.getPrinterType();
        if ("NORMAL_PRINTER".equalsIgnoreCase(type)) {
            binding.actvPrinterType.setText(PRINTER_TYPE_OPTIONS[1], false);
        } else {
            binding.actvPrinterType.setText(PRINTER_TYPE_OPTIONS[0], false);
        }

        String width = settingsManager.getPaperWidth();
        if ("80mm".equalsIgnoreCase(width)) {
            binding.actvPaperWidth.setText(PAPER_WIDTH_OPTIONS[1], false);
        } else {
            binding.actvPaperWidth.setText(PAPER_WIDTH_OPTIONS[0], false);
        }
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

    private void performTestPrint() {
        saveSettings();

        CustomerInvoice testInvoice = new CustomerInvoice(
                "TEST-0001",
                "Test Customer",
                "9876543210",
                50.0,
                0.0,
                0.0,
                50.0,
                "Cash",
                System.currentTimeMillis()
        );

        List<InvoiceItem> testItems = new ArrayList<>();
        testItems.add(new InvoiceItem(0, 1, "Sample Spare Part A", "SKU101", 20.0, 1, 20.0));
        testItems.add(new InvoiceItem(0, 2, "Sample Spare Part B", "SKU102", 15.0, 2, 30.0));

        Toast.makeText(this, "Sending Test Print to Printer...", Toast.LENGTH_SHORT).show();

        BluetoothThermalPrinterHelper.printInvoiceReceipt(this, testInvoice, testItems, new BluetoothThermalPrinterHelper.OnPrintListener() {
            @Override
            public void onPrintSuccess() {
                runOnUiThread(() -> Toast.makeText(ReceiptSettingsActivity.this, "Test Print Sent Successfully!", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onPrintFailed(String error) {
                runOnUiThread(() -> Toast.makeText(ReceiptSettingsActivity.this, "Test Print Failed: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void saveSettings() {
        CharSequence nameText = binding.etShopName.getText();
        CharSequence addrText = binding.etShopAddress.getText();
        CharSequence phoneText = binding.etShopPhone.getText();
        CharSequence taxText = binding.etGstTaxRate.getText();
        CharSequence footerText = binding.etFooterNote.getText();

        String name = nameText != null ? nameText.toString().trim() : "";
        String address = addrText != null ? addrText.toString().trim() : "";
        String phone = phoneText != null ? phoneText.toString().trim() : "";
        String taxStr = taxText != null ? taxText.toString().trim() : "";
        String footer = footerText != null ? footerText.toString().trim() : "";

        float taxRate = 0.0f;
        try {
            if (!taxStr.isEmpty()) taxRate = Float.parseFloat(taxStr);
        } catch (Exception ignored) {}

        settingsManager.setShopName(name.isEmpty() ? "Deepak Auto Garage" : name);
        settingsManager.setShopAddress(address);
        settingsManager.setShopPhone(phone);
        settingsManager.setGstTaxRate(taxRate);
        settingsManager.setFooterNote(footer);

        String typeFormat = binding.actvPrinterType.getText().toString();
        if (typeFormat.contains("Normal Desktop")) {
            settingsManager.setPrinterType("NORMAL_PRINTER");
        } else {
            settingsManager.setPrinterType("BLUETOOTH_THERMAL");
        }

        String widthFormat = binding.actvPaperWidth.getText().toString();
        if (widthFormat.contains("80mm")) {
            settingsManager.setPaperWidth("80mm");
        } else {
            settingsManager.setPaperWidth("58mm");
        }

        Toast.makeText(this, "Printer settings saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
