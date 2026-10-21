package com.smartpantry.manager.utils;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartpantry.manager.R;
import com.smartpantry.manager.activities.MainActivity;
import com.smartpantry.manager.activities.PantryActivity;
import com.smartpantry.manager.activities.SettingsActivity;
import com.smartpantry.manager.activities.SuggestedRecipesActivity;

public final class NavigationHelper {
    private NavigationHelper() {}
    public static void bind(Activity activity,BottomNavigationView nav,int selectedId) {
        nav.setSelectedItemId(selectedId);
        nav.setOnItemSelectedListener(item->{
            int id=item.getItemId(); if(id==selectedId) return true;
            Class<?> destination=id==R.id.nav_home?MainActivity.class:id==R.id.nav_pantry?PantryActivity.class:id==R.id.nav_recipes?SuggestedRecipesActivity.class:SettingsActivity.class;
            Intent intent=new Intent(activity,destination); intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); activity.startActivity(intent);
            return true;
        });
    }
}
