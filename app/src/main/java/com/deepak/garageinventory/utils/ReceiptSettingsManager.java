package com.deepak.garageinventory.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ReceiptSettingsManager {

    private static final String PREF_NAME = "receipt_settings";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_ADDRESS = "shop_address";
    private static final String KEY_SHOP_PHONE = "shop_phone";
    private static final String KEY_GST_TAX_RATE = "gst_tax_rate";
    private static final String KEY_FOOTER_NOTE = "footer_note";
    private static final String KEY_PAPER_WIDTH = "paper_width"; // "58mm" or "80mm"
    private static final String KEY_BT_MAC_ADDRESS = "bt_mac_address";
    private static final String KEY_BT_DEVICE_NAME = "bt_device_name";

    private final SharedPreferences prefs;

    public ReceiptSettingsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getShopName() {
        return prefs.getString(KEY_SHOP_NAME, "Deepak Auto Garage");
    }

    public void setShopName(String name) {
        prefs.edit().putString(KEY_SHOP_NAME, name).apply();
    }

    public String getShopAddress() {
        return prefs.getString(KEY_SHOP_ADDRESS, "Main Workshop Road, City");
    }

    public void setShopAddress(String address) {
        prefs.edit().putString(KEY_SHOP_ADDRESS, address).apply();
    }

    public String getShopPhone() {
        return prefs.getString(KEY_SHOP_PHONE, "+91 98765 43210");
    }

    public void setShopPhone(String phone) {
        prefs.edit().putString(KEY_SHOP_PHONE, phone).apply();
    }

    public float getGstTaxRate() {
        return prefs.getFloat(KEY_GST_TAX_RATE, 0.0f);
    }

    public void setGstTaxRate(float rate) {
        prefs.edit().putFloat(KEY_GST_TAX_RATE, rate).apply();
    }

    public String getFooterNote() {
        return prefs.getString(KEY_FOOTER_NOTE, "Thank you for visiting! Drive safe.");
    }

    public void setFooterNote(String note) {
        prefs.edit().putString(KEY_FOOTER_NOTE, note).apply();
    }

    public String getPaperWidth() {
        return prefs.getString(KEY_PAPER_WIDTH, "58mm");
    }

    public void setPaperWidth(String width) {
        prefs.edit().putString(KEY_PAPER_WIDTH, width).apply();
    }

    public String getBtMacAddress() {
        return prefs.getString(KEY_BT_MAC_ADDRESS, "");
    }

    public String getBtDeviceName() {
        return prefs.getString(KEY_BT_DEVICE_NAME, "No Printer Paired");
    }

    public void setBtPrinter(String macAddress, String deviceName) {
        prefs.edit()
                .putString(KEY_BT_MAC_ADDRESS, macAddress)
                .putString(KEY_BT_DEVICE_NAME, deviceName)
                .apply();
    }
}
