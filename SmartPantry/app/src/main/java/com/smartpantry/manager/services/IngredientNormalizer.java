package com.smartpantry.manager.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IngredientNormalizer {
    private static final Map<String,String> NAMES = new HashMap<>();
    static {
        String[][] forms={{"tomatoes","tomato"},{"eggs","egg"},{"potatoes","potato"},{"onions","onion"},{"carrots","carrot"},{"bananas","banana"},{"apples","apple"},{"garlic cloves","garlic"},{"garlic clove","garlic"},{"bread slices","bread"},{"bread slice","bread"},{"rice grains","rice"}};
        for(String[] pair:forms) NAMES.put(pair[0],pair[1]);
    }
    private IngredientNormalizer() {}
    public static String normalize(String value) {
        String clean=value==null?"":value.trim().replaceAll("\\s+"," ").toLowerCase(Locale.ROOT);
        return NAMES.getOrDefault(clean,clean);
    }
}
