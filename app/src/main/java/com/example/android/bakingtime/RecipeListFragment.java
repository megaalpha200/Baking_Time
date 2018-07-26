package com.example.android.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.android.volley.Response;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.testing.TestActivity;
import com.example.android.bakingtime.utils.RecipeUtils;
import com.example.android.bakingtime.utils.VerticalSpaceItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class RecipeListFragment extends NetworkAwareFragment implements RecipeAdapter.OnRecipeClickedListener {

    private RecipeAdapter mAdapter;

    @BindView(R.id.main_recycler_view)
    RecyclerView RecipeRecyclerView;

    private boolean isWidgetSetupMode = false;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private CountingIdlingResource mIdlingResource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_list, container, false);

        ButterKnife.bind(this, view);
        Timber.plant(new Timber.DebugTree());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isWidgetSetupMode = savedInstanceState.getBoolean(getString(R.string.widget_setup_mode_key), false);
            mAppWidgetId = savedInstanceState.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        setUpRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            attachIdlingResourceFromActivity();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @VisibleForTesting
    private void attachIdlingResourceFromActivity() {
        mIdlingResource = ((TestActivity) mContext).getIdlingResource();
    }

    @VisibleForTesting
    private void incrementCountingIdlingResource() {
        if (mIdlingResource != null)
            mIdlingResource.increment();
    }

    @VisibleForTesting
    private void decrementCountingIdlingResource() {
        if (mIdlingResource != null)
            mIdlingResource.decrement();
    }

    @Override
    protected void refreshUIOnNetworkStateChange() {
        if (mAdapter == null) {
            setUpRecyclerView();
        }
    }

    @Override
    protected void loadNoConnectionUI() {

    }

    private void setUpRecyclerView() {

        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    List<Recipe> recipes = new ArrayList<>();

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject recipeData = array.getJSONObject(i);

                        Gson gson = new GsonBuilder().create();
                        Recipe recipe = gson.fromJson(String.valueOf(recipeData), Recipe.class);
                        recipes.add(recipe);
                    }

                    RecyclerView.LayoutManager layoutManager;

                    Configuration config = mContext.getResources().getConfiguration();
                    if (config.smallestScreenWidthDp >= 600) {
                        layoutManager = new GridLayoutManager(mContext, 3);
                    }
                    else {
                        layoutManager = new LinearLayoutManager(mContext);
                    }

                    mAdapter = new RecipeAdapter(mContext, recipes, RecipeListFragment.this);

                    RecipeRecyclerView.setLayoutManager(layoutManager);
                    RecipeRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(15));
                    RecipeRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    decrementCountingIdlingResource();
                }
            }
        };

        incrementCountingIdlingResource();
        RecipeUtils.getRecipeData(mContext, stringListener);
    }

    @Override
    public void onClick(View view, int position) {
        Recipe currRecipe = mAdapter.getRecipes().get(position);

        if (!isWidgetSetupMode) {
            Bundle extras = new Bundle();
            extras.putParcelable(getString(R.string.recipe_key), currRecipe);

            Intent intent = new Intent(mContext, RecipeStepsListActivity.class);
            intent.putExtras(extras);

            startActivity(intent);
        }
        else
            setUpForWidget(currRecipe);
    }

    private void setUpForWidget(Recipe currRecipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.recipe_ingredients_widget);

        views.setTextViewText(R.id.appwidget_recipe_name_text_view, currRecipe.getName());
        views.setTextViewText(R.id.appwidget_recipe_ingredients_text_view, RecipeUtils.createRecipeListString(currRecipe.getIngredients()));

        Intent intent = new Intent(mContext, RecipeStepsListActivity.class);
        intent.putExtra(getString(R.string.recipe_key), currRecipe);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, mAppWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_recipe_container, pendingIntent);

        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        AppCompatActivity currActivity = (AppCompatActivity) mContext;
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        currActivity.setResult(RESULT_OK, resultValue);
        currActivity.finish();
    }

    public void setWidgetSetupMode(boolean widgetSetupMode) {
        isWidgetSetupMode = widgetSetupMode;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.mAppWidgetId = appWidgetId;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.widget_setup_mode_key), isWidgetSetupMode);
    }
}
