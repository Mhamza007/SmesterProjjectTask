package com.mhamza007.videoapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static final String FILE_NAME = "SharedPref";
    Context context;

    public SharedPref(Context context) {
        this.context = context;
    }

    public void setEmail(String email) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getEmail() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("email", "");
    }

    public void setPassword(String password) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String getPassword() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("password", "");
    }


}
