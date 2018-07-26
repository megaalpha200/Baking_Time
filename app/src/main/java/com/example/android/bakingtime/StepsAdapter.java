package com.example.android.bakingtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.models.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private Context mContext;
    private List<Step> steps;
    private OnStepClickedListener StepClickedListener;
    private int currHighlightedIndex = -1;

    public StepsAdapter(Context context, List<Step> steps, OnStepClickedListener stepClickedListener) {
        this.mContext = context;
        this.steps = steps;
        StepClickedListener = stepClickedListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.step_item_view, parent, false);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        return new StepViewHolder(view, StepClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(steps.get(position), position == currHighlightedIndex);
    }

    @Override
    public int getItemCount() {
        if (steps != null)
            return steps.size();
        else
            return 0;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setCurrHighlightedIndex(int currHighlightedIndex) {
        this.currHighlightedIndex = currHighlightedIndex;
    }

    public int getCurrHighlightedIndex() {
        return currHighlightedIndex;
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_step_view) View RecipeStepItemView;
        @BindView(R.id.step_name_text_view) TextView StepNameTextView;

        OnStepClickedListener mListener;

        StepViewHolder(View view, OnStepClickedListener listener) {
            super(view);
            this.mListener = listener;
            ButterKnife.bind(this, view);

            RecipeStepItemView.setOnClickListener(this);
        }

        void bind(Step step, Boolean isHighlighted) {
            StepNameTextView.setText(step.getShortDescription());

            if(isHighlighted)
                RecipeStepItemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            else
                RecipeStepItemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(RecipeStepItemView, getAdapterPosition());
        }
    }

    public interface OnStepClickedListener {
        void onClick(View view, int position);
    }
}
