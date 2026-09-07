package com.deepak.garageinventory.ui.billing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.local.entity.InvoiceItem;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityCreateInvoiceBinding;
import com.deepak.garageinventory.ui.scanner.BarcodeScannerActivity;
import com.deepak.garageinventory.utils.BluetoothThermalPrinterHelper;
import com.deepak.garageinventory.utils.ReceiptSettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateInvoiceActivity extends AppCompatActivity implements InvoiceLineAdapter.OnLineItemRemoveListener {

    private ActivityCreateInvoiceBinding binding;
    private InventoryRepository repository;
    private ReceiptSettingsManager receiptSettings;

    private String invoiceNumber;
    private List<InvoiceItem> lineItems = new ArrayList<>();
    private List<InventoryItem> availableStockItems = new ArrayList<>();
    private InvoiceLineAdapter adapter;

    private double subtotal = 0.0;
    private double taxAmount = 0.0;
    private double grandTotal = 0.0;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_RESULT);
                    if (barcode != null) {
                        addPartToInvoiceByBarcode(barcode);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());
        receiptSettings = new ReceiptSettingsManager(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.US);
        invoiceNumber = "INV-" + sdf.format(new Date());
        binding.tvInvoiceNo.setText("Invoice #: " + invoiceNumber);

        float taxRate = receiptSettings.getGstTaxRate();
        binding.tvTaxLabel.setText(String.format(Locale.US, "Tax / GST (%.1f%%)", taxRate));

        adapter = new InvoiceLineAdapter(this);
        binding.rvLineItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLineItems.setAdapter(adapter);

        // Load stock items for dropdown selection
        repository.getAllItems().observe(this, items -> {
            availableStockItems = items != null ? items : new ArrayList<>();
        });

        binding.btnScanBillItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        binding.btnAddFromStock.setOnClickListener(v -> showSelectStockDialog());

        binding.btnSaveBill.setOnClickListener(v -> saveInvoice(false));
        binding.btnPrintBillThermal.setOnClickListener(v -> saveInvoice(true));
    }

    private void addPartToInvoiceByBarcode(String barcode) {
        InventoryItem matchedItem = null;
        for (InventoryItem item : availableStockItems) {
            if (barcode.equalsIgnoreCase(item.getBarcodeSku())) {
                matchedItem = item;
                break;
            }
        }

        if (matchedItem != null) {
            addItemToBill(matchedItem);
        } else {
            Toast.makeText(this, "No item found with barcode: " + barcode, Toast.LENGTH_LONG).show();
        }
    }

    private void showSelectStockDialog() {
        if (availableStockItems.isEmpty()) {
            Toast.makeText(this, "No items available in stock", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> displayNames = new ArrayList<>();
        for (InventoryItem item : availableStockItems) {
            displayNames.add(item.getName() + " ($" + item.getUnitPrice() + " - Stock: " + item.getQuantity() + ")");
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Item from Stock")
                .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayNames), (dialog, which) -> {
                    InventoryItem selectedItem = availableStockItems.get(which);
                    addItemToBill(selectedItem);
                })
                .show();
    }

    private void addItemToBill(InventoryItem stockItem) {
        // Check if item already exists in lineItems
        for (InvoiceItem line : lineItems) {
            if (line.getItemId() == stockItem.getId()) {
                line.setQuantity(line.getQuantity() + 1);
                line.setTotalPrice(line.getQuantity() * line.getUnitPrice());
                adapter.notifyDataSetChanged();
                recalculateTotals();
                Toast.makeText(this, "Increased qty for " + stockItem.getName(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        InvoiceItem newLine = new InvoiceItem(
                0,
                stockItem.getId(),
                stockItem.getName(),
                stockItem.getBarcodeSku(),
                stockItem.getUnitPrice(),
                1,
                stockItem.getUnitPrice()
        );

        lineItems.add(newLine);
        adapter.setLineItems(lineItems);
        recalculateTotals();
        Toast.makeText(this, "Added " + stockItem.getName() + " to bill", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveLineItem(int position) {
        if (position >= 0 && position < lineItems.size()) {
            lineItems.remove(position);
            adapter.setLineItems(lineItems);
            recalculateTotals();
        }
    }

    private void recalculateTotals() {
        subtotal = 0.0;
        for (InvoiceItem line : lineItems) {
            subtotal += line.getTotalPrice();
        }

        float taxRate = receiptSettings.getGstTaxRate();
        taxAmount = (subtotal * taxRate) / 100.0;
        grandTotal = subtotal + taxAmount;

        binding.tvBillSubtotal.setText(String.format(Locale.US, "$%.2f", subtotal));
        binding.tvBillTax.setText(String.format(Locale.US, "$%.2f", taxAmount));
        binding.tvBillGrandTotal.setText(String.format(Locale.US, "$%.2f", grandTotal));
    }

    private void saveInvoice(boolean triggerThermalPrint) {
        String customerName = binding.etCustomerName.getText().toString().trim();
        String customerPhone = binding.etCustomerPhone.getText().toString().trim();

        if (lineItems.isEmpty()) {
            Toast.makeText(this, "Please add at least one item to the bill", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerInvoice invoice = new CustomerInvoice(
                invoiceNumber,
                customerName.isEmpty() ? "Walk-in Customer" : customerName,
                customerPhone,
                subtotal,
                taxAmount,
                0.0,
                grandTotal,
                "Cash",
                System.currentTimeMillis()
        );

        repository.saveInvoiceAndDeductStock(invoice, lineItems, invoiceId -> {
            Toast.makeText(this, "Bill Saved & Stock Deducted!", Toast.LENGTH_SHORT).show();

            if (triggerThermalPrint) {
                BluetoothThermalPrinterHelper.printInvoiceReceipt(this, invoice, lineItems, new BluetoothThermalPrinterHelper.OnPrintListener() {
                    @Override
                    public void onPrintSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(CreateInvoiceActivity.this, "Receipt Printed Successfully!", Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onPrintFailed(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(CreateInvoiceActivity.this, "Print Warning: " + error, Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }
                });
            } else {
                finish();
            }
        });
    }
}
