package com.smartpantry.manager.models;

public class PantryItem {
    public final long id;
    public final String name, normalizedName, unit, expiryDate;
    public final double quantity;
    public PantryItem(long id, String name, String normalizedName, double quantity, String unit, String expiryDate) {
        this.id=id; this.name=name; this.normalizedName=normalizedName; this.quantity=quantity; this.unit=unit; this.expiryDate=expiryDate;
    }
}
