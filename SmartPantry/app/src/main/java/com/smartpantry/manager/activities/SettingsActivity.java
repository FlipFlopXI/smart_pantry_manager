package com.smartpantry.manager.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartpantry.manager.R;
import com.smartpantry.manager.utils.HelpUi;
import com.smartpantry.manager.utils.NavigationHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_settings);
        HelpUi.attach(this,HelpUi.Topic.SETTINGS);
        SwitchMaterial alerts=findViewById(R.id.alerts);
        alerts.setChecked(getSharedPreferences("settings",MODE_PRIVATE).getBoolean("expiry_alerts",true));
        alerts.setOnCheckedChangeListener((button,checked)->getSharedPreferences("settings",MODE_PRIVATE).edit().putBoolean("expiry_alerts",checked).apply());
        SwitchMaterial darkMode=findViewById(R.id.dark_mode);
        darkMode.setChecked(getSharedPreferences("settings",MODE_PRIVATE).getBoolean("dark_mode",false));
        darkMode.setOnCheckedChangeListener((button,checked)->{
            getSharedPreferences("settings",MODE_PRIVATE).edit().putBoolean("dark_mode",checked).apply();
            AppCompatDelegate.setDefaultNightMode(checked?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_settings);
    }
    @Override protected void onResume() { super.onResume(); ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_settings); }
}
