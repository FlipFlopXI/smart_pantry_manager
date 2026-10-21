package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.services.RecipeMatchingService;
import com.smartpantry.manager.utils.NavigationHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_home);
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_home);
        findViewById(R.id.add).setOnClickListener(v->startActivity(new Intent(this,AddEditIngredientActivity.class)));
        findViewById(R.id.pantry).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        findViewById(R.id.recipes).setOnClickListener(v->startActivity(new Intent(this,SuggestedRecipesActivity.class)));
    }
    @Override protected void onResume() { super.onResume(); ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_home); worker.execute(()->{
        try(DatabaseHelper db=new DatabaseHelper(this)) {
            List<PantryItem> pantry=db.getPantryItems(); int recipes=RecipeMatchingService.matching(pantry,db.getRecipes()).size();
            int soon=0; if(getSharedPreferences("settings",MODE_PRIVATE).getBoolean("expiry_alerts",true)) for(PantryItem item:pantry) {
                try { if(item.expiryDate!=null) { LocalDate date=LocalDate.parse(item.expiryDate), today=LocalDate.now(); if(!date.isBefore(today) && !date.isAfter(today.plusDays(3))) soon++; } } catch(Exception ignored) {}
            }
            int count=pantry.size(), expiring=soon;
            runOnUiThread(()->{ ((TextView)findViewById(R.id.stats)).setText("🥕 "+count+" pantry items\n🍽️ "+recipes+" recipes possible\n⏳ "+expiring+" expiring soon"); findViewById(R.id.home_empty).setVisibility(count==0?View.VISIBLE:View.GONE); });
        }
    }); }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
