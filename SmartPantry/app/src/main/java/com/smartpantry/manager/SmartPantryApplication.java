package com.smartpantry.manager;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class SmartPantryApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        boolean dark=getSharedPreferences("settings",MODE_PRIVATE).getBoolean("dark_mode",false);
        AppCompatDelegate.setDefaultNightMode(dark?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
    }
}
