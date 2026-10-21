package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.RecipeAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.services.RecipeMatchingService;
import com.smartpantry.manager.utils.NavigationHelper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private RecipeAdapter adapter;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_list);
        ((TextView)findViewById(R.id.title)).setText("Suggested recipes");
        ((TextView)findViewById(R.id.subtitle)).setText("Meals you can make right now");
        ((TextView)findViewById(R.id.primary_button)).setText("Open pantry");
        findViewById(R.id.primary_button).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        ((TextView)findViewById(R.id.empty)).setText("No recipes match your pantry yet. Add more ingredients to discover meals you can make!");
        RecyclerView recycler=findViewById(R.id.recycler); recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecipeAdapter(recipe->startActivity(new Intent(this,RecipeDetailActivity.class).putExtra("recipe_id",recipe.id))); recycler.setAdapter(adapter);
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_recipes);
    }
    @Override protected void onResume() { super.onResume(); ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_recipes); worker.execute(()->{ try(DatabaseHelper db=new DatabaseHelper(this)) {
        List<Recipe> matches=RecipeMatchingService.matching(db.getPantryItems(),db.getRecipes());
        runOnUiThread(()->{ adapter.setItems(matches); findViewById(R.id.empty).setVisibility(matches.isEmpty()?View.VISIBLE:View.GONE); });
    } }); }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
