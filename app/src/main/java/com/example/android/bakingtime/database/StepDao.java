package com.example.android.bakingtime.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bakingtime.models.Step;

import java.util.List;

@Dao
public interface StepDao {

    @Query("SELECT * FROM step WHERE recipeId=:recipeId")
    List<Step> loadAllStepsForRecipe(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertSteps(List<Step> steps);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStep(Step step);

    @Query("DELETE FROM step WHERE recipeId=:recipeId")
    void deleteStepsBasedOnRecipeId(int recipeId);

    @Delete
    void deleteStep(Step step);

    @Query("SELECT * FROM step WHERE recipeId=:recipeId AND id=:stepId")
    Step loadStepById(int recipeId, int stepId);
}
