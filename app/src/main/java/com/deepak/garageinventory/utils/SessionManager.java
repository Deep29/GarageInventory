package com.deepak.garageinventory.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "bizmaster_user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_BUSINESS_NAME = "business_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUserSession(String name, String businessName, String email, String phone) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_BUSINESS_NAME, businessName)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PHONE, phone)
                .apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Deepak Gowda");
    }

    public String getBusinessName() {
        return prefs.getString(KEY_BUSINESS_NAME, "Deepak Auto Garage");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "deepakgowda.nr@gmail.com");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "+91 98765 43210");
    }

    public void logoutUser() {
        prefs.edit().clear().apply();
    }
}
