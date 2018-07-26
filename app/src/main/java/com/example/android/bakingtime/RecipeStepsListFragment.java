package com.example.android.bakingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepsListFragment extends NetworkAwareFragment implements StepsAdapter.OnStepClickedListener {
    private StepsAdapter mAdapter;

    @BindView(R.id.main_recycler_view)
    RecyclerView RecipeStepsRecyclerView;

    private Recipe mRecipe;
    private Boolean isTwoPane = false;
    private OnStepSelectedListener stepSelectedCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_list, container, false);

        ButterKnife.bind(this, view);
        Timber.plant(new Timber.DebugTree());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            stepSelectedCallback = (OnStepSelectedListener) mContext;
        }
        catch (Exception ex) {
            Timber.d("Please implement the OnStepSelectedListener interface to the Activity!");
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            int highlightedStepIndex = 0;

            if (((AppCompatActivity)mContext).findViewById(R.id.two_pane_recipe_steps_linear_layout) != null)
                isTwoPane = true;

            if (savedInstanceState == null) {
                Bundle extras = getActivity().getIntent().getExtras();

                if (extras != null) {
                    mRecipe = extras.getParcelable(getString(R.string.recipe_key));
                }
                else {
                    mRecipe = new Recipe();
                    mRecipe.setName(getString(R.string.recipe_name_placeholder));
                }
            }
            else {
                mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_key));
                highlightedStepIndex = savedInstanceState.getInt(getString(R.string.step_highlighted_index_key), 0);
            }

            ((AppCompatActivity)mContext).getSupportActionBar().setTitle(mRecipe.getName());
            setUpRecyclerView(mRecipe.getSteps(), highlightedStepIndex);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void refreshUIOnNetworkStateChange() {

    }

    @Override
    protected void loadNoConnectionUI() {

    }

    private void setUpRecyclerView(List<Step> steps, int highlightedIndex) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new StepsAdapter(mContext, steps, this);

        RecipeStepsRecyclerView.setLayoutManager(linearLayoutManager);
        RecipeStepsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));


        RecipeStepsRecyclerView.setAdapter(mAdapter);

        if(isTwoPane) {
            mAdapter.setCurrHighlightedIndex(highlightedIndex);
            onClick(null, highlightedIndex);
        }
    }

    public void setHighlightedStepItem(int index) {
        if(isTwoPane) {
            int prevHighlightedIndex = mAdapter.getCurrHighlightedIndex();
            mAdapter.setCurrHighlightedIndex(index);
            mAdapter.notifyItemChanged(prevHighlightedIndex);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onClick(View view, int position) {
        try {
            stepSelectedCallback.onStepSelected(mRecipe, position);
            setHighlightedStepItem(position);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_key), mRecipe);
        outState.putInt(getString(R.string.step_highlighted_index_key), mAdapter.getCurrHighlightedIndex());
    }

    public interface OnStepSelectedListener {
        void onStepSelected(Recipe recipe, int position);
    }
}
