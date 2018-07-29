package com.example.android.bakingtime.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bakingtime.models.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {

    @Query("SELECT * from ingredient WHERE recipeId=:recipeId")
    List<Ingredient> loadAllIngredientsForRecipe(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<Ingredient> ingredients);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredient WHERE recipeId=:recipeId")
    void deleteIngredientsBasedOnRecipeId(int recipeId);

    @Delete
    void deleteIngredient(Ingredient ingredient);

    @Query("SELECT * FROM ingredient WHERE recipeId=:recipeId AND id=:ingredientId")
    Ingredient loadIngredientById(int recipeId, int ingredientId);
}
