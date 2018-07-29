package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.bakingtime.database.AppDatabase;
import com.example.android.bakingtime.models.AppExecutors;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.WidgetRecipe;
import com.example.android.bakingtime.utils.RecipeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DatabaseTesterActivity extends AppCompatActivity {
    @BindView(R.id.widget_id_edit_text_view) EditText WidgetIdEditTextView;
    @BindView(R.id.recipe_info_text_view) TextView RecipeInfoTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_tester);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.search_widget_info_btn)
    protected void onSearchBtnClicked() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int widgetId = Integer.parseInt(WidgetIdEditTextView.getText().toString());

                AppDatabase mDb = AppDatabase.getInstance(DatabaseTesterActivity.this);

                WidgetRecipe widgetRecipe = mDb.widgetRecipeDao().loadWidgetRecipeById(widgetId);

                if(widgetRecipe != null) {
                    Recipe currRecipe = mDb.recipeDao().loadRecipeById(widgetRecipe.getRecipeId());
                    currRecipe.setIngredients(mDb.ingredientDao().loadAllIngredientsForRecipe(currRecipe.getId()));
                    currRecipe.setSteps(mDb.stepDao().loadAllStepsForRecipe(currRecipe.getId()));

                    final String textViewSTR = "Recipe Name: " + currRecipe.getName() + "\n\n" +
                            RecipeUtils.createRecipeListAsString(currRecipe.getIngredients());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecipeInfoTextView.setText(textViewSTR);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecipeInfoTextView.setText("No Results!");
                        }
                    });
                }
            }
        });
    }
}
