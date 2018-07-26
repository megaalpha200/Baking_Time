package com.example.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.models.Recipe;


public class RecipeStepsListActivity extends AppCompatActivity implements RecipeStepsListFragment.OnStepSelectedListener, StepDetailsFragment.OnStepNavigationListener {

    public boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps_list);

        if (findViewById(R.id.two_pane_recipe_steps_linear_layout) != null)
            isTwoPane = true;

        if (savedInstanceState == null) {
            if (isTwoPane) {
                StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.step_details_list_container, stepDetailsFragment)
                        .commit();
            }

            RecipeStepsListFragment recipeStepsListFragment = new RecipeStepsListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_steps_list_container, recipeStepsListFragment)
                    .commit();
        }
    }

    @Override
    public void onStepSelected(Recipe recipe, int position) {
        if (isTwoPane) {
            ((StepDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.step_details_list_container)).navigateToStepByIndex(position);
        }
        else {
            Bundle extras = new Bundle();
            extras.putParcelable(getString(R.string.recipe_key), recipe);
            extras.putInt(getString(R.string.step_index_key), position);

            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtras(extras);

            startActivity(intent);
        }
    }

    @Override
    public void onStepNavigation(int index) {
        if(isTwoPane) {
            ((RecipeStepsListFragment) getSupportFragmentManager().findFragmentById(R.id.recipe_steps_list_container)).setHighlightedStepItem(index);
        }
    }
}
