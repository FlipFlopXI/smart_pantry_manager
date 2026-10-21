package com.smartpantry.manager.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartpantry.manager.R;
import com.smartpantry.manager.utils.NavigationHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_settings);
        SwitchMaterial alerts=findViewById(R.id.alerts);
        alerts.setChecked(getSharedPreferences("settings",MODE_PRIVATE).getBoolean("expiry_alerts",true));
        alerts.setOnCheckedChangeListener((button,checked)->getSharedPreferences("settings",MODE_PRIVATE).edit().putBoolean("expiry_alerts",checked).apply());
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_settings);
    }
    @Override protected void onResume() { super.onResume(); ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_settings); }
}
