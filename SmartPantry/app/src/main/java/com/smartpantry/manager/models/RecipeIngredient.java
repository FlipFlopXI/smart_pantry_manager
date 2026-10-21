package com.smartpantry.manager.models;

public class RecipeIngredient {
    public final String name, normalizedName, unit;
    public final double quantity;
    public RecipeIngredient(String name, String normalizedName, double quantity, String unit) {
        this.name=name; this.normalizedName=normalizedName; this.quantity=quantity; this.unit=unit;
    }
}
