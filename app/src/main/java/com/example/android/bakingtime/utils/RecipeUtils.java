package com.example.android.bakingtime.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.bakingtime.models.Ingredient;
import com.example.android.bakingtime.R;

import java.util.List;

public class RecipeUtils {
    public static void getRecipeData(Context mContext, Response.Listener<String> listener) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.GET, mContext.getString(R.string.recipe_url),
               listener , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    public static String createRecipeListString(List<Ingredient> ingredients) {
        StringBuilder ingredientsFormattedString = new StringBuilder();
        ingredientsFormattedString.append("Ingredients:\n");

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ingredientsFormattedString.append("\t\t-" + String.valueOf(ingredient.getQuantity()) + " " + ingredient.getMeasure() + " of " + ingredient.getIngredientName() + "\n");
        }

        return ingredientsFormattedString.toString();
    }
}
