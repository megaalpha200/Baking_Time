package com.example.android.bakingtime.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "widget_recipe")
public class WidgetRecipe {
    @PrimaryKey private int appWidgetId;
    private int recipeId;

    public WidgetRecipe(int appWidgetId, int recipeId) {
        this.appWidgetId = appWidgetId;
        this.recipeId = recipeId;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
