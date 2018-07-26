package com.example.android.bakingtime.testing;

import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeListActivity;

public class TestActivity extends AppCompatActivity {

    private CountingIdlingResource idlingResource = new CountingIdlingResource(RecipeListActivity.class.getSimpleName() + "_IDLING_RES");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @VisibleForTesting
    public CountingIdlingResource getIdlingResource() {
        return idlingResource;
    }
}
