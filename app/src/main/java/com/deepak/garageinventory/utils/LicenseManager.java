package com.deepak.garageinventory.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LicenseManager {

    private static final String PREF_NAME = "biz_license_prefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch_time";
    private static final String KEY_LICENSE_TYPE = "license_type"; // "TRIAL", "KEY_ACTIVE", "EXPIRED"
    private static final String KEY_EXPIRATION_TIME = "license_expiration_time";
    private static final String KEY_PRODUCT_KEY = "product_key_used";

    // 90 Days Free Trial = 3 Months
    private static final long THREE_MONTHS_MS = 90L * 24L * 60L * 60L * 1000L;
    // 365 Days = 1 Year
    private static final long ONE_YEAR_MS = 365L * 24L * 60L * 60L * 1000L;

    public static void initTrialIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_FIRST_LAUNCH)) {
            long now = System.currentTimeMillis();
            long trialExpiry = now + THREE_MONTHS_MS;

            prefs.edit()
                    .putLong(KEY_FIRST_LAUNCH, now)
                    .putString(KEY_LICENSE_TYPE, "TRIAL")
                    .putLong(KEY_EXPIRATION_TIME, trialExpiry)
                    .apply();
        }
    }

    public static boolean isLicenseActive(Context context) {
        initTrialIfNeeded(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long expiryTime = prefs.getLong(KEY_EXPIRATION_TIME, 0);
        return System.currentTimeMillis() < expiryTime;
    }

    public static int getRemainingDays(Context context) {
        initTrialIfNeeded(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long expiryTime = prefs.getLong(KEY_EXPIRATION_TIME, 0);
        long diffMs = expiryTime - System.currentTimeMillis();
        if (diffMs <= 0) return 0;
        return (int) (diffMs / (24L * 60L * 60L * 1000L));
    }

    public static String getLicenseStatusSummary(Context context) {
        initTrialIfNeeded(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String type = prefs.getString(KEY_LICENSE_TYPE, "TRIAL");
        long expiryTime = prefs.getLong(KEY_EXPIRATION_TIME, 0);
        int daysLeft = getRemainingDays(context);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String dateStr = sdf.format(new Date(expiryTime));

        if (daysLeft <= 0) {
            return "License Expired. Please enter a Product Key to activate.";
        }

        if ("KEY_ACTIVE".equalsIgnoreCase(type)) {
            return "Active 1-Year License (Valid until " + dateStr + " • " + daysLeft + " Days Left)";
        } else {
            return "Active 3-Month Free Trial (Valid until " + dateStr + " • " + daysLeft + " Days Left)";
        }
    }

    public static boolean activateProductKey(Context context, String rawKey) {
        if (rawKey == null) return false;
        String formattedKey = rawKey.trim().toUpperCase(Locale.US);

        if (!isValidProductKeyFormat(formattedKey)) {
            return false;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long currentExpiry = prefs.getLong(KEY_EXPIRATION_TIME, System.currentTimeMillis());
        long baseTime = Math.max(currentExpiry, System.currentTimeMillis());
        long newExpiry = baseTime + ONE_YEAR_MS; // Extends by 1 Year

        prefs.edit()
                .putString(KEY_LICENSE_TYPE, "KEY_ACTIVE")
                .putString(KEY_PRODUCT_KEY, formattedKey)
                .putLong(KEY_EXPIRATION_TIME, newExpiry)
                .apply();

        return true;
    }

    /**
     * Validates product keys matching BIZ-XXXX-XXXX-XXXX or 16-character alphanumeric pattern.
     */
    public static boolean isValidProductKeyFormat(String key) {
        if (key.length() < 12) return false;

        // Valid key prefixes: "BIZ-", "BIZMASTER-", "VYAPAR-", "TALLY-", or custom 16-char format
        return key.startsWith("BIZ-") ||
               key.startsWith("BIZMASTER-") ||
               key.startsWith("VYAPAR-") ||
               key.matches("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$");
    }

    /**
     * Helper to generate sample valid 1-Year Product Keys for testing/admin issuance.
     */
    public static String generateSampleProductKey() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("BIZ-");
        Random r = new Random();
        for (int i = 0; i < 4; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        sb.append("-");
        for (int i = 0; i < 4; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        sb.append("-");
        for (int i = 0; i < 4; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
