package com.deepak.garageinventory.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.local.entity.InvoiceItem;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BluetoothThermalPrinterHelper {

    // Standard SPP UUID for Bluetooth Serial Printers
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // ESC/POS Commands
    private static final byte[] ESC_INIT = new byte[]{0x1B, 0x40}; // Initialize printer
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1B, 0x61, 0x01};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1B, 0x61, 0x00};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1B, 0x61, 0x02};
    private static final byte[] ESC_BOLD_ON = new byte[]{0x1B, 0x45, 0x01};
    private static final byte[] ESC_BOLD_OFF = new byte[]{0x1B, 0x45, 0x00};

    public interface OnPrintListener {
        void onPrintSuccess();
        void onPrintFailed(String error);
    }

    @SuppressWarnings("MissingPermission")
    public static void printInvoiceReceipt(Context context,
                                           CustomerInvoice invoice,
                                           List<InvoiceItem> items,
                                           OnPrintListener listener) {

        ReceiptSettingsManager settings = new ReceiptSettingsManager(context);
        String macAddress = settings.getBtMacAddress();

        if (macAddress == null || macAddress.isEmpty()) {
            if (listener != null) {
                listener.onPrintFailed("No Bluetooth printer selected. Please pair in Receipt Settings.");
            }
            return;
        }

        new Thread(() -> {
            BluetoothSocket socket = null;
            try {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (btAdapter == null || !btAdapter.isEnabled()) {
                    if (listener != null) listener.onPrintFailed("Bluetooth is disabled.");
                    return;
                }

                BluetoothDevice device = btAdapter.getRemoteDevice(macAddress);
                socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                btAdapter.cancelDiscovery();
                socket.connect();

                OutputStream os = socket.getOutputStream();

                // 1. Initialize
                os.write(ESC_INIT);

                // 2. Header (Garage Name)
                os.write(ESC_ALIGN_CENTER);
                os.write(ESC_BOLD_ON);
                os.write((settings.getShopName() + "\n").getBytes("UTF-8"));
                os.write(ESC_BOLD_OFF);
                os.write((settings.getShopAddress() + "\n").getBytes("UTF-8"));
                os.write(("Ph: " + settings.getShopPhone() + "\n").getBytes("UTF-8"));
                os.write("--------------------------------\n".getBytes("UTF-8"));

                // 3. Invoice & Customer Details
                os.write(ESC_ALIGN_LEFT);
                os.write(("Inv #: " + invoice.getInvoiceNumber() + "\n").getBytes("UTF-8"));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US);
                os.write(("Date: " + sdf.format(new Date(invoice.getTimestamp())) + "\n").getBytes("UTF-8"));
                if (invoice.getCustomerName() != null && !invoice.getCustomerName().isEmpty()) {
                    os.write(("Customer: " + invoice.getCustomerName() + "\n").getBytes("UTF-8"));
                }
                os.write("--------------------------------\n".getBytes("UTF-8"));

                // 4. Line Items Table Header
                os.write(ESC_BOLD_ON);
                os.write("Item             Qty Rate   Total\n".getBytes("UTF-8"));
                os.write(ESC_BOLD_OFF);
                os.write("--------------------------------\n".getBytes("UTF-8"));

                // 5. Line Items
                if (items != null) {
                    for (InvoiceItem item : items) {
                        String name = item.getItemName();
                        if (name.length() > 16) name = name.substring(0, 16);

                        String line = String.format(Locale.US, "%-16s %2d %5.1f %7.2f\n",
                                name, item.getQuantity(), item.getUnitPrice(), item.getTotalPrice());
                        os.write(line.getBytes("UTF-8"));
                    }
                }

                os.write("--------------------------------\n".getBytes("UTF-8"));

                // 6. Subtotal & Totals
                os.write(ESC_ALIGN_RIGHT);
                os.write(String.format(Locale.US, "Subtotal: $%.2f\n", invoice.getSubtotal()).getBytes("UTF-8"));
                if (invoice.getTaxAmount() > 0) {
                    os.write(String.format(Locale.US, "Tax / GST: $%.2f\n", invoice.getTaxAmount()).getBytes("UTF-8"));
                }
                os.write(ESC_BOLD_ON);
                os.write(String.format(Locale.US, "TOTAL: $%.2f\n", invoice.getTotalAmount()).getBytes("UTF-8"));
                os.write(ESC_BOLD_OFF);
                os.write(("Payment Mode: " + invoice.getPaymentMode() + "\n").getBytes("UTF-8"));
                os.write("--------------------------------\n".getBytes("UTF-8"));

                // 7. Footer
                os.write(ESC_ALIGN_CENTER);
                os.write((settings.getFooterNote() + "\n\n\n\n").getBytes("UTF-8"));

                os.flush();
                socket.close();

                if (listener != null) {
                    listener.onPrintSuccess();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (socket != null) {
                    try { socket.close(); } catch (Exception ignored) {}
                }
                if (listener != null) {
                    listener.onPrintFailed("Failed to print: " + e.getMessage());
                }
            }
        }).start();
    }
}
