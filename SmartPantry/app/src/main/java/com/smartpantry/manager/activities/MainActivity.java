package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.services.RecipeMatchingService;
import com.smartpantry.manager.utils.HelpUi;
import com.smartpantry.manager.utils.NavigationHelper;
import com.smartpantry.manager.views.CookieJarStatView;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_home);
        HelpUi.attach(this,HelpUi.Topic.HOME);
        NavigationHelper.bind(this,findViewById(R.id.bottom_nav),R.id.nav_home);
        findViewById(R.id.add).setOnClickListener(v->startActivity(new Intent(this,AddEditIngredientActivity.class)));
        findViewById(R.id.pantry).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        findViewById(R.id.recipes).setOnClickListener(v->startActivity(new Intent(this,SuggestedRecipesActivity.class)));
    }
    @Override protected void onResume() {
        super.onResume();
        ((com.google.android.material.bottomnavigation.BottomNavigationView)findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_home);
        worker.execute(()->{
            try(DatabaseHelper db=new DatabaseHelper(this)) {
                List<PantryItem> pantry=db.getPantryItems();
                int possible=RecipeMatchingService.matching(pantry,db.getRecipes()).size();
                List<Recipe> recent=db.getRecentlyUsedRecipes(5);
                int soon=0, expired=0;
                LocalDate today=LocalDate.now();
                for(PantryItem item:pantry) {
                    if(item.expiryDate==null) continue;
                    try {
                        LocalDate date=LocalDate.parse(item.expiryDate);
                        if(date.isBefore(today)) expired++;
                        else if(!date.isAfter(today.plusDays(3))) soon++;
                    } catch(Exception ignored) { }
                }
                int pantryCount=pantry.size(), soonCount=soon, expiredCount=expired;
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed()) return;
                    ((CookieJarStatView)findViewById(R.id.jar_pantry)).setStat(pantryCount,"Pantry items",ContextCompat.getColor(this,R.color.green));
                    ((CookieJarStatView)findViewById(R.id.jar_recipes)).setStat(possible,"Possible recipes",ContextCompat.getColor(this,R.color.orange));
                    ((CookieJarStatView)findViewById(R.id.jar_soon)).setStat(soonCount,"Expiring soon",ContextCompat.getColor(this,R.color.jar_blue));
                    ((CookieJarStatView)findViewById(R.id.jar_expired)).setStat(expiredCount,"Expired items",ContextCompat.getColor(this,R.color.jar_red));
                    showRecentRecipes(recent);
                });
            }
        });
    }
    private void showRecentRecipes(List<Recipe> recipes) {
        LinearLayout container=findViewById(R.id.recent_list);
        container.removeAllViews();
        findViewById(R.id.recent_empty).setVisibility(recipes.isEmpty()?View.VISIBLE:View.GONE);
        LayoutInflater inflater=LayoutInflater.from(this);
        for(Recipe recipe:recipes) {
            View row=inflater.inflate(R.layout.row_recent_recipe,container,false);
            ((TextView)row.findViewById(R.id.recent_name)).setText(recipe.name);
            ((TextView)row.findViewById(R.id.recent_meta)).setText(recipe.preparationTime+" min  •  Tap to see recipe");
            row.setOnClickListener(v->startActivity(new Intent(this,RecipeDetailActivity.class).putExtra("recipe_id",recipe.id)));
            container.addView(row);
        }
    }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
