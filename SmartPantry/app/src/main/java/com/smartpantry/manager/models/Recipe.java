package com.smartpantry.manager.models;

import java.util.List;
import java.util.ArrayList;

public class Recipe {
    public final long id;
    public final String name, description, instructions;
    public final int preparationTime;
    public final List<RecipeIngredient> ingredients = new ArrayList<>();
    public Recipe(long id, String name, String description, String instructions, int preparationTime) {
        this.id=id; this.name=name; this.description=description; this.instructions=instructions; this.preparationTime=preparationTime;
    }
}
