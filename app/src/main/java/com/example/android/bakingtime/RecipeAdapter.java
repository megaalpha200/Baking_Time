package com.example.android.bakingtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;
    private OnRecipeClickedListener RecipeClickedListener;

    public RecipeAdapter(Context context, List<Recipe> recipes, OnRecipeClickedListener listener) {
        this.mContext = context;
        this.recipes = recipes;
        this.RecipeClickedListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipie_item_view, parent, false);

        return new RecipeViewHolder(view, RecipeClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        if (recipes != null)
            return recipes.size();
        else
            return 0;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name_text_view) TextView recipeNameTextView;
        @BindView(R.id.recipe_image_view) ImageView recipeImageView;
        @BindView(R.id.recipe_card_view) View recipeItemView;

        OnRecipeClickedListener mListener;

        RecipeViewHolder(View view, OnRecipeClickedListener listener) {
            super(view);
            this.mListener = listener;
            ButterKnife.bind(this, view);

            recipeItemView.setOnClickListener(this);
        }

        void bind(Recipe recipe) {
            recipeNameTextView.setText(recipe.getName());

            if (recipe.getImageURL() != null && !recipe.getImageURL().equals("")) {
                Picasso.with(mContext)
                        .load(recipe.getImageURL())
                        .placeholder(R.drawable.ic_placeholder_recipe_image)
                        .into(recipeImageView);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public interface OnRecipeClickedListener {
        void onClick(View view, int position);
    }
}
