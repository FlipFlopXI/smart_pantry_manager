package com.smartpantry.manager.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.UnitConverter;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_detail);
        findViewById(R.id.back).setOnClickListener(v->finish());
        long id=getIntent().getLongExtra("recipe_id",-1);
        Recipe recipe; try(DatabaseHelper db=new DatabaseHelper(this)) { recipe=db.getRecipe(id); }
        if(recipe==null) { ((TextView)findViewById(R.id.title)).setText("Recipe unavailable"); ((TextView)findViewById(R.id.description)).setText("This recipe could not be found."); return; }
        ((TextView)findViewById(R.id.title)).setText(recipe.name);
        ((TextView)findViewById(R.id.description)).setText(recipe.description+" • "+recipe.preparationTime+" min");
        StringBuilder ingredients=new StringBuilder("Ingredients\n"); for(RecipeIngredient item:recipe.ingredients) ingredients.append("• ").append(item.name).append(" — ").append(UnitConverter.format(item.quantity)).append(" ").append(item.unit).append("\n");
        ((TextView)findViewById(R.id.ingredients)).setText(ingredients.toString().trim());
        ((TextView)findViewById(R.id.instructions)).setText("Method\n"+recipe.instructions);
    }
}
