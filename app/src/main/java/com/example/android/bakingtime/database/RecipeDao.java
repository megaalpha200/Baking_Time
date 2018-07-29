package com.example.android.bakingtime.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bakingtime.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    List<Recipe> loadAllRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(Recipe recipe);

    @Query("DELETE FROM recipe WHERE id=:recipeId")
    void deleteRecipeBasedOnRecipeId(int recipeId);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("SELECT * FROM recipe WHERE id=:id")
    Recipe loadRecipeById(int id);
}
