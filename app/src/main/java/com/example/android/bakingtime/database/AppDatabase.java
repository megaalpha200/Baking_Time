package com.example.android.bakingtime.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.bakingtime.models.Ingredient;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Step;
import com.example.android.bakingtime.models.WidgetRecipe;


@Database(entities = {Recipe.class, Ingredient.class, Step.class, WidgetRecipe.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "bakingtime";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return sInstance;
    }

    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract StepDao stepDao();
    public abstract WidgetRecipeDao widgetRecipeDao();
}