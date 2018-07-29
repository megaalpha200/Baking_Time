package com.example.android.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.android.bakingtime.database.AppDatabase;
import com.example.android.bakingtime.models.AppExecutors;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.WidgetRecipe;
import com.example.android.bakingtime.utils.RecipeUtils;
import com.example.android.bakingtime.widget.RecipeWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe currRecipe) {

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.recipe_ingredients_widget);

        views.setTextViewText(R.id.appwidget_recipe_name_text_view, currRecipe.getName());

        Intent intent = new Intent(context, RecipeStepsListActivity.class);
        intent.putExtra(context.getString(R.string.recipe_key), currRecipe);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_recipe_container, pendingIntent);

        Intent listViewServiceIntent = new Intent(context, RecipeWidgetService.class);
        listViewServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listViewServiceIntent.putExtra(context.getString(R.string.widget_ingredients_str_list_key), RecipeUtils.createIngredientFormattedList(currRecipe.getIngredients()));
        listViewServiceIntent.setData(Uri.parse(listViewServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.appwidget_ingredients_list_view, listViewServiceIntent);
        views.setEmptyView(R.id.appwidget_ingredients_list_view, R.id.appwidget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase mDb = AppDatabase.getInstance(context);
                    WidgetRecipe widgetRecipe = mDb.widgetRecipeDao().loadWidgetRecipeById(appWidgetId);
                    if (widgetRecipe != null) {
                        Recipe currRecipe = mDb.recipeDao().loadRecipeById(widgetRecipe.getRecipeId());
                        currRecipe.setSteps(mDb.stepDao().loadAllStepsForRecipe(currRecipe.getId()));
                        currRecipe.setIngredients(mDb.ingredientDao().loadAllIngredientsForRecipe(currRecipe.getId()));

                        updateAppWidget(context, appWidgetManager, appWidgetId, currRecipe);
                    }
                }
            });
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase mDb = AppDatabase.getInstance(context);

                        WidgetRecipe widgetRecipe = mDb.widgetRecipeDao().loadWidgetRecipeById(appWidgetId);

                        if (widgetRecipe != null) {
                            int recipeId = widgetRecipe.getRecipeId();

                            mDb.recipeDao().deleteRecipeBasedOnRecipeId(recipeId);
                            mDb.ingredientDao().deleteIngredientsBasedOnRecipeId(recipeId);
                            mDb.stepDao().deleteStepsBasedOnRecipeId(recipeId);
                            mDb.widgetRecipeDao().deleteWidgetRecipe(widgetRecipe);
                        }
                    }
                });
            }
        }
    }
}

