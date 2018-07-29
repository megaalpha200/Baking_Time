package com.example.android.bakingtime.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bakingtime.models.WidgetRecipe;

@Dao
public interface WidgetRecipeDao {
    @Insert
    void insertWidgetRecipe(WidgetRecipe widgetRecipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWidgetRecipe(WidgetRecipe widgetRecipe);

    @Delete
    void deleteWidgetRecipe(WidgetRecipe widgetRecipe);

    @Query("SELECT * FROM widget_recipe WHERE appWidgetId=:appWidgetId")
    WidgetRecipe loadWidgetRecipeById(int appWidgetId);
}
