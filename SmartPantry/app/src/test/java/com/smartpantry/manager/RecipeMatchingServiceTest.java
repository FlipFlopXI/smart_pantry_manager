package com.smartpantry.manager;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.services.IngredientNormalizer;
import com.smartpantry.manager.services.RecipeMatchingService;
import com.smartpantry.manager.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecipeMatchingServiceTest {
    private PantryItem pantry(String name,double amount,String unit) { return new PantryItem(1,name,IngredientNormalizer.normalize(name),amount,unit,null); }
    private Recipe recipe(String... requirements) {
        Recipe recipe=new Recipe(1,"Test","","1. Cook.",5);
        for(String text:requirements) { String[] fields=text.split("\\|"); recipe.ingredients.add(new RecipeIngredient(fields[0],IngredientNormalizer.normalize(fields[0]),Double.parseDouble(fields[1]),fields[2])); }
        return recipe;
    }
    @Test public void allPresentAndExactQuantityMatch() {
        Recipe recipe=recipe("egg|2|pieces","tomato|1|pieces");
        assertTrue(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("Eggs",2,"pieces"),pantry("Tomatoes",1,"pieces"))));
    }
    @Test public void missingOneOrSeveralReject() {
        Recipe recipe=recipe("egg|2|pieces","tomato|1|pieces","bread|2|pieces");
        assertFalse(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("egg",2,"pieces"),pantry("tomato",1,"pieces"))));
        assertFalse(RecipeMatchingService.canMake(recipe,Collections.singletonList(pantry("egg",2,"pieces"))));
    }
    @Test public void insufficientAndGreaterQuantities() {
        Recipe recipe=recipe("rice|300|grams");
        assertFalse(RecipeMatchingService.canMake(recipe,Collections.singletonList(pantry("rice",200,"grams"))));
        assertTrue(RecipeMatchingService.canMake(recipe,Collections.singletonList(pantry("rice",400,"grams"))));
    }
    @Test public void massAndVolumeConversions() {
        assertTrue(RecipeMatchingService.canMake(recipe("rice|500|grams"),Collections.singletonList(pantry("rice",1,"kilograms"))));
        assertFalse(RecipeMatchingService.canMake(recipe("rice|1|kilograms"),Collections.singletonList(pantry("rice",500,"grams"))));
        assertTrue(RecipeMatchingService.canMake(recipe("milk|500|millilitres"),Collections.singletonList(pantry("milk",1,"litres"))));
        assertFalse(RecipeMatchingService.canMake(recipe("milk|500|millilitres"),Collections.singletonList(pantry("milk",500,"grams"))));
    }
    @Test public void duplicatesAggregateOnlyCompatibleDimensions() {
        Recipe recipe=recipe("rice|300|grams");
        assertTrue(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("rice",100,"grams"),pantry("rice",0.2,"kilograms"))));
        assertFalse(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("rice",100,"grams"),pantry("rice",200,"millilitres"))));
    }
    @Test public void repeatedRecipeRequirementsCannotReuseTheSameStock() {
        Recipe recipe=recipe("egg|1|pieces","eggs|2|pieces");
        assertFalse(RecipeMatchingService.canMake(recipe,Collections.singletonList(pantry("egg",2,"pieces"))));
        assertTrue(RecipeMatchingService.canMake(recipe,Collections.singletonList(pantry("egg",3,"pieces"))));
    }
    @Test public void emptyPantryAndChangesRecalculate() {
        Recipe recipe=recipe("egg|2|pieces"); List<PantryItem> pantry=new ArrayList<>();
        assertTrue(RecipeMatchingService.matching(pantry,Collections.singletonList(recipe)).isEmpty());
        pantry.add(pantry("egg",2,"pieces")); assertEquals(1,RecipeMatchingService.matching(pantry,Collections.singletonList(recipe)).size());
        pantry.set(0,pantry("egg",1,"pieces")); assertTrue(RecipeMatchingService.matching(pantry,Collections.singletonList(recipe)).isEmpty());
        pantry.set(0,pantry("egg",2,"pieces")); pantry.clear(); assertTrue(RecipeMatchingService.matching(pantry,Collections.singletonList(recipe)).isEmpty());
    }
    @Test public void nearlyReadyRequiresHalfTheFullIngredientQuantities() {
        Recipe recipe=recipe("egg|2|pieces","tomato|1|pieces","bread|2|pieces","milk|250|millilitres");
        RecipeMatchingService.MatchProgress half=RecipeMatchingService.progress(recipe,Arrays.asList(pantry("eggs",2,"pieces"),pantry("tomatoes",1,"pieces")));
        assertEquals(2,half.matched);
        assertEquals(4,half.total);
        assertTrue(half.isNearlyReady());
        assertFalse(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("eggs",2,"pieces"),pantry("tomatoes",1,"pieces"))));
        assertFalse(RecipeMatchingService.progress(recipe,Collections.singletonList(pantry("egg",2,"pieces"))).isNearlyReady());
        assertFalse(RecipeMatchingService.progress(recipe,Arrays.asList(pantry("egg",1,"pieces"),pantry("tomato",1,"pieces"))).isNearlyReady());
    }
    @Test public void repeatedNamesCountAsOneRequirementAndFullMatchIsNotNearlyReady() {
        Recipe recipe=recipe("egg|1|pieces","eggs|2|pieces","toast|1|pieces");
        RecipeMatchingService.MatchProgress partial=RecipeMatchingService.progress(recipe,Collections.singletonList(pantry("egg",3,"pieces")));
        assertEquals(1,partial.matched);
        assertEquals(2,partial.total);
        assertTrue(partial.isNearlyReady());
        RecipeMatchingService.MatchProgress full=RecipeMatchingService.progress(recipe,Arrays.asList(pantry("egg",3,"pieces"),pantry("toast",1,"pieces")));
        assertFalse(full.isNearlyReady());
        assertTrue(RecipeMatchingService.canMake(recipe,Arrays.asList(pantry("egg",3,"pieces"),pantry("toast",1,"pieces"))));
    }
    @Test public void validationRejectsInvalidAmounts() {
        assertTrue(Double.isNaN(ValidationUtils.positiveQuantity("0")));
        assertTrue(Double.isNaN(ValidationUtils.positiveQuantity("-2")));
        assertTrue(Double.isNaN(ValidationUtils.positiveQuantity("hello")));
        assertTrue(Double.isNaN(ValidationUtils.positiveQuantity("Infinity")));
    }
}
