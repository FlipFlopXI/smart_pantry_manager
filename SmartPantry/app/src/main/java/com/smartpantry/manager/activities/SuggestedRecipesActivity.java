package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.RecipeAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.services.RecipeMatchingService;
import com.smartpantry.manager.utils.HelpUi;
import com.smartpantry.manager.utils.NavigationHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private RecipeAdapter adapter;
    private List<Recipe> recipes=new ArrayList<>();
    private List<PantryItem> pantry=new ArrayList<>();
    private int selectedFilter=R.id.filter_available;            //11. START...................................................END
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_list);
        HelpUi.attach(this,HelpUi.Topic.RECIPES);
        ((TextView)findViewById(R.id.title)).setText("Suggested recipes");
        ((TextView)findViewById(R.id.primary_button)).setText("Open pantry");
        findViewById(R.id.primary_button).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        findViewById(R.id.recipe_filters).setVisibility(View.VISIBLE);
        RecyclerView recycler=findViewById(R.id.recycler); recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecipeAdapter(recipe->startActivity(new Intent(this,RecipeDetailActivity.class).putExtra("recipe_id",recipe.id))); recycler.setAdapter(adapter);
        MaterialButtonToggleGroup filters=findViewById(R.id.filter_group);
        filters.check(R.id.filter_available);               //12. START..................................................................
        filters.addOnButtonCheckedListener((group,checkedId,isChecked)->{
            if(isChecked) { selectedFilter=checkedId; showSelectedRecipes(); }
        });                                                 //12. END..........................................................................
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_recipes);
        showSelectedRecipes();
    }
    @Override protected void onResume() { super.onResume(); ((BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_recipes); worker.execute(()->{ try(DatabaseHelper db=new DatabaseHelper(this)) {
        List<PantryItem> currentPantry=db.getPantryItems();
        List<Recipe> allRecipes=db.getRecipes();
        runOnUiThread(()->{ if(isFinishing()||isDestroyed()) return; pantry=currentPantry; recipes=allRecipes; showSelectedRecipes(); });
    } }); }
    private void showSelectedRecipes() {
        List<Recipe> displayed=new ArrayList<>();
        Map<Long,String> progressLabels=new HashMap<>();
        for(Recipe recipe:recipes) {                        //13. START................................................................................
            RecipeMatchingService.MatchProgress progress=RecipeMatchingService.progress(recipe,pantry);
            boolean available=progress.total>0 && progress.matched==progress.total;
            boolean nearlyReady=progress.isNearlyReady();
            if((selectedFilter==R.id.filter_available && available)
                    || (selectedFilter==R.id.filter_nearly && nearlyReady)
                    || (selectedFilter==R.id.filter_unavailable && !available && !nearlyReady)) {
                displayed.add(recipe);
                if(!available) progressLabels.put(recipe.id,progress.matched+" of "+progress.total+" ingredients ready");
            }
        }
        adapter.setItems(displayed,progressLabels);           //13. END.................................................................................
        TextView subtitle=findViewById(R.id.subtitle);
        TextView empty=findViewById(R.id.empty);
        if(selectedFilter==R.id.filter_available) {
            subtitle.setText("Meals you can make right now");
            empty.setText("No recipes match your pantry yet. Add more ingredients to discover meals you can make!");
        } else if(selectedFilter==R.id.filter_nearly) {
            subtitle.setText("You have at least half the ingredients");
            empty.setText("No nearly ready recipes yet. Add ingredients to your pantry to get closer.");
        } else {
            subtitle.setText("Recipes needing more ingredients");
            empty.setText("No unavailable recipes. Your pantry is looking good!");
        }
        empty.setVisibility(displayed.isEmpty()?View.VISIBLE:View.GONE);
    }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
