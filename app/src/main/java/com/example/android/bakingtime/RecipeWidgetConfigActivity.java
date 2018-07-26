package com.example.android.bakingtime;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeWidgetConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_widget_config);

        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (savedInstanceState == null) {
            RecipeListFragment recipeListFragment = new RecipeListFragment();
            recipeListFragment.setWidgetSetupMode(true);
            recipeListFragment.setAppWidgetId(mAppWidgetId);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_list_container, recipeListFragment)
                    .commit();
        }
    }
}
