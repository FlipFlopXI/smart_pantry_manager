package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.PantryAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.NavigationHelper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PantryActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private PantryAdapter adapter;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_list);
        ((TextView)findViewById(R.id.title)).setText("Your pantry");
        ((TextView)findViewById(R.id.subtitle)).setText("Everything you have on hand");
        ((TextView)findViewById(R.id.primary_button)).setText("+ Add ingredient");
        findViewById(R.id.primary_button).setOnClickListener(v->startActivity(new Intent(this,AddEditIngredientActivity.class)));
        ((TextView)findViewById(R.id.empty)).setText("Your pantry is empty. Add your first ingredient!");
        RecyclerView recycler=findViewById(R.id.recycler); recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PantryAdapter(new PantryAdapter.Actions() {
            @Override public void edit(PantryItem item) { startActivity(new Intent(PantryActivity.this,AddEditIngredientActivity.class).putExtra("item_id",item.id)); }
            @Override public void delete(PantryItem item) { new MaterialAlertDialogBuilder(PantryActivity.this).setTitle("Delete "+item.name+"?").setMessage("This will update your suggested recipes.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(dialog,which)->worker.execute(()->{ try(DatabaseHelper db=new DatabaseHelper(PantryActivity.this)) { db.deletePantryItem(item.id); } runOnUiThread(()->load()); })).show(); }
        });
        recycler.setAdapter(adapter); NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_pantry);
    }
    private void load() { worker.execute(()->{ try(DatabaseHelper db=new DatabaseHelper(this)) {
        List<PantryItem> items=db.getPantryItems(); boolean alerts=getSharedPreferences("settings",MODE_PRIVATE).getBoolean("expiry_alerts",true);
        runOnUiThread(()->{ adapter.setItems(items,alerts); findViewById(R.id.empty).setVisibility(items.isEmpty()?View.VISIBLE:View.GONE); });
    } }); }
    @Override protected void onResume() { super.onResume(); ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_pantry); load(); }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
