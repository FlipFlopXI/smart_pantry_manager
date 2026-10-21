package com.smartpantry.manager.utils;

import java.util.Locale;

public final class UnitConverter {
    private UnitConverter() {}
    public static String dimension(String unit) {
        if(unit==null) return "";
        switch(unit.toLowerCase(Locale.ROOT)) {
            case "grams": case "kilograms": return "mass";
            case "millilitres": case "litres": return "volume";
            case "pieces": return "pieces";
            case "teaspoons": return "teaspoons";
            case "tablespoons": return "tablespoons";
            case "cups": return "cups";
            default: return "";
        }
    }
    public static double baseAmount(double quantity, String unit) {
        if("kilograms".equals(unit)||"litres".equals(unit)) return quantity*1000d;
        return quantity;
    }
    public static boolean compatible(String left,String right) {
        String dimension=dimension(left);
        return !dimension.isEmpty() && dimension.equals(dimension(right));
    }
    public static String format(double amount) {
        return amount==Math.rint(amount)?String.format(Locale.ROOT,"%.0f",amount):String.format(Locale.ROOT,"%.2f",amount);
    }
}
