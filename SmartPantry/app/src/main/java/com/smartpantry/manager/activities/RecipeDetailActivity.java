package com.smartpantry.manager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.HelpUi;
import com.smartpantry.manager.utils.UnitConverter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_detail);
        HelpUi.attach(this,HelpUi.Topic.DETAIL);
        findViewById(R.id.back).setOnClickListener(v->finish());
        long id=getIntent().getLongExtra("recipe_id",-1);
        Recipe recipe; try(DatabaseHelper db=new DatabaseHelper(this)) { recipe=db.getRecipe(id); }
        if(recipe==null) { ((TextView)findViewById(R.id.title)).setText("Recipe unavailable"); ((TextView)findViewById(R.id.description)).setText("This recipe could not be found."); findViewById(R.id.mark_cooked).setVisibility(View.GONE); return; }
        ((TextView)findViewById(R.id.title)).setText(recipe.name);
        ((TextView)findViewById(R.id.description)).setText(recipe.description+" • "+recipe.preparationTime+" min");
        StringBuilder ingredients=new StringBuilder("Ingredients\n"); for(RecipeIngredient item:recipe.ingredients) ingredients.append("• ").append(item.name).append(" — ").append(UnitConverter.format(item.quantity)).append(" ").append(item.unit).append("\n");
        ((TextView)findViewById(R.id.ingredients)).setText(ingredients.toString().trim());
        ((TextView)findViewById(R.id.instructions)).setText("Method\n"+recipe.instructions);
        View markCooked=findViewById(R.id.mark_cooked);
        markCooked.setOnClickListener(v->{
            markCooked.setEnabled(false);
            worker.execute(()->{
                boolean saved;
                try(DatabaseHelper db=new DatabaseHelper(this)) { db.recordRecipeUsed(id); saved=true; }
                catch(Exception error) { saved=false; }
                boolean success=saved;
                runOnUiThread(()->{ if(isFinishing()||isDestroyed()) return; markCooked.setEnabled(true); Toast.makeText(this,success?"Added to recently cooked":"Could not save recipe use",Toast.LENGTH_SHORT).show(); });
            });
        });
    }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
