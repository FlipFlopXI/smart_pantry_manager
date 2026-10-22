package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.services.IngredientNormalizer;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME="smart_pantry.db";
    private static final int VERSION=2;
    public DatabaseHelper(Context context) { super(context,NAME,null,VERSION); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items(id INTEGER PRIMARY KEY, name TEXT NOT NULL, normalized_name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, expiry_date TEXT)");
        db.execSQL("CREATE INDEX pantry_name_idx ON pantry_items(normalized_name)");  //1. Start BELOW......................................................
        db.execSQL("CREATE TABLE recipes(id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, description TEXT, instructions TEXT NOT NULL, preparation_time INTEGER)");
        db.execSQL("CREATE TABLE recipe_ingredients(id INTEGER PRIMARY KEY, recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, ingredient_name TEXT NOT NULL, normalized_name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL)");
        db.execSQL("CREATE INDEX recipe_ingredient_recipe_idx ON recipe_ingredients(recipe_id)");
        createRecipeUseTable(db);
        seedRecipes(db);  //1. END ... RECIPE CREATE.................................................................................
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        if(oldVersion<2) createRecipeUseTable(db);
    }
    private void createRecipeUseTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_uses(id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, used_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS recipe_uses_recipe_idx ON recipe_uses(recipe_id)");
    }
    private ContentValues pantryValues(String name,double quantity,String unit,String expiry) {
        ContentValues values=new ContentValues(); values.put("name",name.trim()); values.put("normalized_name",IngredientNormalizer.normalize(name)); values.put("quantity",quantity); values.put("unit",unit); values.put("expiry_date",expiry); return values;
    }
    public long addPantryItem(String name,double quantity,String unit,String expiry) {   // 2. START...CRUD.....................................
        return getWritableDatabase().insertOrThrow("pantry_items",null,pantryValues(name,quantity,unit,expiry));
    }
    public boolean updatePantryItem(long id,String name,double quantity,String unit,String expiry) {
        return getWritableDatabase().update("pantry_items",pantryValues(name,quantity,unit,expiry),"id=?",new String[]{String.valueOf(id)})==1;
    }
    public boolean deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items","id=?",new String[]{String.valueOf(id)})==1;  // 2. END..........................................
    }
    private PantryItem readPantry(Cursor cursor) {
        return new PantryItem(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3),cursor.getString(4),cursor.isNull(5)?null:cursor.getString(5));
    }
    public PantryItem getPantryItem(long id) {
        try(Cursor cursor=getReadableDatabase().rawQuery("SELECT id,name,normalized_name,quantity,unit,expiry_date FROM pantry_items WHERE id=?",new String[]{String.valueOf(id)})) {
            return cursor.moveToFirst()?readPantry(cursor):null;
        }
    }
    public List<PantryItem> getPantryItems() {   //3. START......................Read method queries
        List<PantryItem> items=new ArrayList<>();
        try(Cursor cursor=getReadableDatabase().rawQuery("SELECT id,name,normalized_name,quantity,unit,expiry_date FROM pantry_items ORDER BY name COLLATE NOCASE,id",null)) {
            while(cursor.moveToNext()) items.add(readPantry(cursor));
        }
        return items;  //3. END.........................................
    }
    private Recipe readRecipe(Cursor cursor) { return new Recipe(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4)); }
    public List<Recipe> getRecipes() {
        List<Recipe> recipes=new ArrayList<>();
        try(Cursor cursor=getReadableDatabase().rawQuery("SELECT id,name,description,instructions,preparation_time FROM recipes ORDER BY name",null)) {
            while(cursor.moveToNext()) { Recipe recipe=readRecipe(cursor); recipe.ingredients.addAll(getRecipeIngredients(recipe.id)); recipes.add(recipe); }
        }
        return recipes;
    }
    public Recipe getRecipe(long id) {
        try(Cursor cursor=getReadableDatabase().rawQuery("SELECT id,name,description,instructions,preparation_time FROM recipes WHERE id=?",new String[]{String.valueOf(id)})) {
            if(!cursor.moveToFirst()) return null;
            Recipe recipe=readRecipe(cursor); recipe.ingredients.addAll(getRecipeIngredients(id)); return recipe;
        }
    }
    public List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> items=new ArrayList<>();
        try(Cursor cursor=getReadableDatabase().rawQuery("SELECT ingredient_name,normalized_name,quantity,unit FROM recipe_ingredients WHERE recipe_id=? ORDER BY id",new String[]{String.valueOf(recipeId)})) {
            while(cursor.moveToNext()) items.add(new RecipeIngredient(cursor.getString(0),cursor.getString(1),cursor.getDouble(2),cursor.getString(3)));
        }
        return items;
    }
    public void recordRecipeUsed(long recipeId) {
        ContentValues values=new ContentValues(); values.put("recipe_id",recipeId); values.put("used_at",System.currentTimeMillis());
        getWritableDatabase().insertOrThrow("recipe_uses",null,values);
    }
    public List<Recipe> getRecentlyUsedRecipes(int limit) {
        List<Recipe> recipes=new ArrayList<>();
        if(limit<=0) return recipes;
        String sql="SELECT r.id,r.name,r.description,r.instructions,r.preparation_time FROM recipes r JOIN (SELECT recipe_id,MAX(id) AS latest FROM recipe_uses GROUP BY recipe_id) u ON u.recipe_id=r.id ORDER BY u.latest DESC LIMIT ?";
        try(Cursor cursor=getReadableDatabase().rawQuery(sql,new String[]{String.valueOf(limit)})) {
            while(cursor.moveToNext()) recipes.add(readRecipe(cursor));
        }
        return recipes;
    }
    private void seedRecipes(SQLiteDatabase db) {
        // onCreate runs once; explicit IDs and REPLACE avoidance keep this seed idempotent.
        db.beginTransaction();
        try {
            addRecipe(db,1,"Cheese omelette","A quick cheese filled omelette","1. Beat the eggs.\n2. Melt butter in a pan.\n3. Pour in eggs, add cheese and cook until set.",10,"egg|2|pieces","butter|10|grams","cheese|30|grams");
            addRecipe(db,2,"Scrambled eggs","Soft breakfast eggs","1. Beat eggs.\n2. Melt butter in a pan.\n3. Stir eggs gently until cooked.",8,"egg|2|pieces","butter|10|grams");
            addRecipe(db,3,"Tomato pasta","Pasta with simple tomato sauce","1. Boil pasta in water until tender and drain.\n2. Chop tomato.\n3. Cook tomato in olive oil and toss with pasta.",20,"pasta|100|grams","water|1|litres","tomato|2|pieces","olive oil|1|tablespoons");
            addRecipe(db,4,"Garlic bread","Toasted garlic butter bread","1. Mix chopped garlic with butter.\n2. Spread on bread and toast until golden.",10,"bread|2|pieces","garlic|2|pieces","butter|20|grams");
            addRecipe(db,5,"Pancakes","Simple pan pancakes","1. Mix flour, milk and egg into a batter.\n2. Melt butter in a pan and cook small pancakes on both sides.",20,"flour|150|grams","milk|200|millilitres","egg|1|pieces","butter|15|grams");
            addRecipe(db,6,"Egg fried rice","Rice stir fried with egg","1. Boil rice in water and drain.\n2. Heat oil in a pan.\n3. Add rice, egg and soy sauce; stir until egg is cooked.",25,"rice|150|grams","water|500|millilitres","egg|1|pieces","vegetable oil|1|tablespoons","soy sauce|1|tablespoons");
            addRecipe(db,7,"Mashed potatoes","Creamy mashed potatoes","1. Boil potatoes in water until soft and drain.\n2. Mash with butter and milk.",30,"potato|3|pieces","water|1|litres","butter|20|grams","milk|50|millilitres");
            addRecipe(db,8,"Roast potatoes","Oven roasted potatoes","1. Cut potatoes into wedges.\n2. Toss with olive oil and salt.\n3. Roast until tender and browned.",40,"potato|3|pieces","olive oil|2|tablespoons","salt|1|teaspoons");
            addRecipe(db,9,"Cheese sandwich","Classic cheese sandwich","1. Place cheese between two bread slices.\n2. Serve or toast until cheese melts.",5,"bread|2|pieces","cheese|40|grams");
            addRecipe(db,10,"Tomato sandwich","Fresh tomato sandwich","1. Slice tomato.\n2. Layer tomato between bread slices.",5,"bread|2|pieces","tomato|1|pieces");
            addRecipe(db,11,"Grilled cheese","Buttery grilled cheese","1. Put cheese between bread slices.\n2. Butter the outsides and toast in a pan until golden.",10,"bread|2|pieces","cheese|50|grams","butter|15|grams");
            addRecipe(db,12,"Banana porridge","Warm oat and banana bowl","1. Simmer oats with milk until soft.\n2. Slice banana over the porridge.",10,"oats|60|grams","milk|200|millilitres","banana|1|pieces");
            addRecipe(db,13,"Apple oatmeal","Oats topped with apple","1. Simmer oats with milk until soft.\n2. Chop apple and add on top.",10,"oats|60|grams","milk|200|millilitres","apple|1|pieces");
            addRecipe(db,14,"Egg sandwich","Egg filled bread","1. Boil egg in water until firm and peel.\n2. Slice egg and place between bread slices.",15,"egg|1|pieces","water|500|millilitres","bread|2|pieces");
            addRecipe(db,15,"Boiled eggs","Simple boiled eggs","1. Cover eggs with water.\n2. Boil until firm and peel.",12,"egg|2|pieces","water|500|millilitres");
            addRecipe(db,16,"Tomato salad","Simple fresh tomato salad","1. Slice tomatoes.\n2. Drizzle with olive oil and sprinkle with salt.",5,"tomato|2|pieces","olive oil|1|tablespoons","salt|1|teaspoons");
            addRecipe(db,17,"Carrot salad","Crunchy grated carrots","1. Grate carrots.\n2. Toss with olive oil and lemon juice.",8,"carrot|2|pieces","olive oil|1|tablespoons","lemon juice|1|tablespoons");
            addRecipe(db,18,"Potato and egg hash","Pan cooked potato and egg","1. Dice and cook potatoes in vegetable oil until tender.\n2. Add eggs and cook until set.",25,"potato|2|pieces","egg|2|pieces","vegetable oil|2|tablespoons");
            addRecipe(db,19,"Banana smoothie","Cold banana milk smoothie","1. Blend banana and milk until smooth.",5,"banana|1|pieces","milk|250|millilitres");
            addRecipe(db,20,"Buttered toast","Warm buttered toast","1. Toast bread.\n2. Spread butter over warm toast.",5,"bread|2|pieces","butter|15|grams");
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    private void addRecipe(SQLiteDatabase db,long id,String name,String description,String instructions,int minutes,String... ingredients) {
        ContentValues recipe=new ContentValues(); recipe.put("id",id); recipe.put("name",name); recipe.put("description",description); recipe.put("instructions",instructions); recipe.put("preparation_time",minutes);
        if(db.insertWithOnConflict("recipes",null,recipe,SQLiteDatabase.CONFLICT_IGNORE)==-1) return;
        for(String encoded:ingredients) {
            String[] parts=encoded.split("\\|");
            ContentValues ingredient=new ContentValues(); ingredient.put("recipe_id",id); ingredient.put("ingredient_name",parts[0]); ingredient.put("normalized_name",IngredientNormalizer.normalize(parts[0])); ingredient.put("quantity",Double.parseDouble(parts[1])); ingredient.put("unit",parts[2]);
            db.insertOrThrow("recipe_ingredients",null,ingredient);
        }
    }
}
