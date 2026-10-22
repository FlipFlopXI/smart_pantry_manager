package com.smartpantry.manager.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.smartpantry.manager.R;

/** Adds the same help control and themed guide to every screen. */
public final class HelpUi {
    public enum Topic {
        HOME("🍪","Home","See your pantry, cookable recipes, and expiry counts in the four jars. The buttons below open the main parts of the app.","Tap a jar for a little shake. Recently cooked recipes appear further down this screen."),
        PANTRY("🥕","Your pantry","Add ingredients with quantities and units, then edit or delete them here. Your pantry is saved on this device and drives recipe suggestions.","An expiry date is optional. Expiring soon and expired items appear in the Home counts."),
        ADD("🧂","Add an ingredient","Enter an ingredient name, a quantity greater than zero, and a unit. Choose an expiry date if you know it, then save.","Your new ingredient can make more recipes appear under Available."),
        EDIT("✏️","Edit an ingredient","Change the ingredient name, quantity, unit, or expiry date, then save. You can also clear the expiry date.","Recipe availability updates from the saved pantry amount."),
        RECIPES("🍳","Browse recipes","Available shows only meals you can make right now with enough of every ingredient. Nearly ready shows recipes with at least half their ingredients. Unavailable shows the rest.","The screen always opens on Available. Tap a recipe to read its full method."),
        DETAIL("📖","Recipe details","Read the full ingredient amounts and preparation steps here. Mark as cooked to add this recipe to your recent history on Home.","Mark as cooked records the recipe; it does not remove ingredients from your pantry."),
        SETTINGS("⚙️","Settings","Turn expiring soon indicators on or off and choose light or dark mode. Your choices are saved for the next time you open the app.","Dark mode changes the colors throughout the app while keeping the same layout.");

        final String icon,title,description,tip;
        Topic(String icon,String title,String description,String tip) {
            this.icon=icon; this.title=title; this.description=description; this.tip=tip;
        }
    }

    private HelpUi() {}

    public static void attach(Activity activity,Topic topic) {
        View button=LayoutInflater.from(activity).inflate(R.layout.view_help_button,null);
        FrameLayout.LayoutParams placement=new FrameLayout.LayoutParams(dp(activity,48),dp(activity,48),Gravity.TOP|Gravity.END);
        placement.topMargin=dp(activity,10);
        placement.rightMargin=dp(activity,14);
        activity.addContentView(button,placement);
        button.setOnClickListener(view->show(activity,topic));
    }

    private static void show(Activity activity,Topic topic) {
        Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        ((TextView)dialog.findViewById(R.id.help_icon)).setText(topic.icon);
        ((TextView)dialog.findViewById(R.id.help_title)).setText(topic.title);
        ((TextView)dialog.findViewById(R.id.help_description)).setText(topic.description);
        ((TextView)dialog.findViewById(R.id.help_tip)).setText(topic.tip);
        dialog.findViewById(R.id.help_close).setOnClickListener(view->dialog.dismiss());
        dialog.findViewById(R.id.help_done).setOnClickListener(view->dialog.dismiss());
        dialog.show();
        Window window=dialog.getWindow();
        if(window!=null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams attributes=window.getAttributes();
            attributes.dimAmount=.5f;
            window.setAttributes(attributes);
            int width=Math.min(activity.getResources().getDisplayMetrics().widthPixels-dp(activity,36),dp(activity,420));
            window.setLayout(width,WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private static int dp(Activity activity,int value) { return Math.round(value*activity.getResources().getDisplayMetrics().density); }
}
