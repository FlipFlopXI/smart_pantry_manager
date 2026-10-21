package com.smartpantry.manager.utils;

public final class ValidationUtils {
    private ValidationUtils() {}
    public static double positiveQuantity(String raw) {
        try { double result=Double.parseDouble(raw.trim()); return Double.isFinite(result)&&result>0?result:Double.NaN; }
        catch(Exception ignored) { return Double.NaN; }
    }
}
