package com.example.petcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences prefs;
    private static final String KEY_USER_ID = "user_id";

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
    }

    public void saveUserId(int id) {
        prefs.edit().putInt(KEY_USER_ID, id).apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
