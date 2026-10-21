package com.smartpantry.manager.services;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.UnitConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public final class RecipeMatchingService {
    private RecipeMatchingService() {}
    public static List<Recipe> matching(List<PantryItem> pantry,List<Recipe> recipes) {
        List<Recipe> result=new ArrayList<>();
        for(Recipe recipe:recipes) if(canMake(recipe,pantry)) result.add(recipe);
        return result;
    }
    public static boolean canMake(Recipe recipe,List<PantryItem> pantry) {
        if(recipe.ingredients.isEmpty()) return false;
        // Combine repeated recipe requirements before checking stock, so one pantry row is never reused twice.
        Map<String,Double> requiredAmounts=new HashMap<>();
        for(RecipeIngredient need:recipe.ingredients) {
            String dimension=UnitConverter.dimension(need.unit);
            if(dimension.isEmpty()) return false;
            String key=need.normalizedName+"|"+dimension;
            requiredAmounts.put(key,requiredAmounts.getOrDefault(key,0d)+UnitConverter.baseAmount(need.quantity,need.unit));
        }
        for(Map.Entry<String,Double> requirement:requiredAmounts.entrySet()) {
            String[] parts=requirement.getKey().split("\\|",2);
            double available=0;
            for(PantryItem item:pantry) {
                if(parts[0].equals(IngredientNormalizer.normalize(item.name)) && parts[1].equals(UnitConverter.dimension(item.unit)))
                    available+=UnitConverter.baseAmount(item.quantity,item.unit);
            }
            double required=requirement.getValue();
            if(available+1e-9*Math.max(1d,required)<required) return false;
        }
        return true;
    }
}
