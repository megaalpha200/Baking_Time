package com.example.android.bakingtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Step;
import com.example.android.bakingtime.utils.RecipeUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import android.support.v4.media.session.MediaButtonReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class StepDetailsFragment extends NetworkAwareFragment implements ExoPlayer.EventListener {
    @BindView(R.id.step_short_description_title) TextView StepShortDescriptionTitleTextView;
    @BindView(R.id.step_description_text_view) TextView StepDescriptionTextView;
    @BindView(R.id.prev_step_btn) Button PrevStepNavigationButton;
    @BindView(R.id.next_step_btn) Button NextStepNavigationButton;
    @BindView(R.id.simpleExoPlayerView) SimpleExoPlayerView StepExoPlayerView;
    @BindView(R.id.step_description_scroll_view) ScrollView StepDescriptionScrollView;
    @BindView(R.id.step_details_constraint_layout) ConstraintLayout StepDescriptionConstraintLayout;
    @BindView(R.id.nav_btns_container) RelativeLayout NavigationButtonsContainer;

    private final String TAG = StepDetailsFragment.class.getSimpleName();
    private Recipe mRecipe;
    private int stepIndex;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private OnStepNavigationListener stepNavigationCallback;

    private boolean shouldExoPlayerResume = false;
    private long lastStoredExoPlayerPos = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_details, container, false);

        ButterKnife.bind(this, view);
        Timber.plant(new Timber.DebugTree());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            stepNavigationCallback = (OnStepNavigationListener) mContext;
        }
        catch (Exception ex) {
            Timber.d("Please implement the OnStepNavigationListener interface to the Activity!");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getActivity().getIntent().getExtras();

            if (extras != null) {
                mRecipe = extras.getParcelable(getString(R.string.recipe_key));
                stepIndex = extras.getInt(getString(R.string.step_index_key), 0);
            }
            else {
                mRecipe = new Recipe();
                mRecipe.setName(getString(R.string.recipe_name_placeholder));

                stepIndex = -1;
            }
        }
        else {
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_key));
            stepIndex = savedInstanceState.getInt(getString(R.string.step_index_key), 0);
            shouldExoPlayerResume = savedInstanceState.getBoolean(mContext.getString(R.string.exo_player_playback_key), false);
            lastStoredExoPlayerPos = savedInstanceState.getLong(mContext.getString(R.string.exo_player_pos_key), 0);
        }

        setUpStepInformation();
    }

    //Source: https://developer.android.com/training/system-ui/immersive
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void refreshUIOnNetworkStateChange() {
        releasePlayer();
        setUpExoPlayerView();
    }

    @Override
    protected void loadNoConnectionUI() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_key), mRecipe);
        outState.putInt(getString(R.string.step_index_key), stepIndex);

        if (mExoPlayer != null) {
            outState.putBoolean(mContext.getString(R.string.exo_player_playback_key), mExoPlayer.getPlayWhenReady());
            outState.putLong(mContext.getString(R.string.exo_player_pos_key), mExoPlayer.getCurrentPosition());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            setUpExoPlayerView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            setUpExoPlayerView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaSession.setActive(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayer != null) {
            shouldExoPlayerResume = mExoPlayer.getPlayWhenReady();
            lastStoredExoPlayerPos = mExoPlayer.getCurrentPosition();
        }

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    public void setUpStepInformation() {
        if (stepIndex != -1) {
            Step currStep = mRecipe.getSteps().get(stepIndex);

            ((AppCompatActivity)mContext).getSupportActionBar().setTitle(mRecipe.getName());
            StepShortDescriptionTitleTextView.setText(currStep.getShortDescription());

            if (stepIndex == 0) {
                StepDescriptionTextView.setText(RecipeUtils.createRecipeListAsString(mRecipe.getIngredients()));
            }
            else {
                StepDescriptionTextView.setText(currStep.getDescription());
            }
        }

        setUpNavigationButtons();
        setUpExoPlayerView();
    }

    private void setUpNavigationButtons() {
        if (stepIndex == 0) {
            PrevStepNavigationButton.setVisibility(View.GONE);
            NextStepNavigationButton.setVisibility(View.VISIBLE);
        }
        else if(stepIndex == mRecipe.getSteps().size() - 1) {
            PrevStepNavigationButton.setVisibility(View.VISIBLE);
            NextStepNavigationButton.setVisibility(View.GONE);
        }
        else {
            PrevStepNavigationButton.setVisibility(View.VISIBLE);
            NextStepNavigationButton.setVisibility(View.VISIBLE);
        }
    }

    private void setUpExoPlayerView() {
        try {
            Step currStep = mRecipe.getSteps().get(stepIndex);

            if (!currStep.getVideoURL().equals("")) {
                boolean isTwoPane = ((AppCompatActivity)mContext).findViewById(R.id.two_pane_recipe_steps_linear_layout) != null;

                if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE && !isTwoPane)
                    hideSystemUI();
                else
                    restructureConstraintsBasedOnPlayerAbsence(false);

                StepExoPlayerView.setVisibility(View.VISIBLE);
                initializeMediaSession();
                initializePlayer(Uri.parse(currStep.getVideoURL()));
            }
            else {
                StepExoPlayerView.setVisibility(View.GONE);
                setVisibilityForEssentialUIViews(View.VISIBLE);
                restructureConstraintsBasedOnPlayerAbsence(true);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            StepExoPlayerView.setVisibility(View.GONE);
            setVisibilityForEssentialUIViews(View.VISIBLE);
            restructureConstraintsBasedOnPlayerAbsence(true);
            mExoPlayer = null;
        }
    }

    private void restructureConstraintsBasedOnPlayerAbsence(boolean isPlayerAbsent) {
        //Sources: https://developer.android.com/reference/android/support/constraint/ConstraintSet#connect(int,%20int,%20int,%20int,%20int)
        //https://stackoverflow.com/questions/45263159/constraintlayout-change-constraints-programmatically

        if (isPlayerAbsent) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(StepDescriptionConstraintLayout);
            constraintSet.connect(R.id.step_description_scroll_view, ConstraintSet.TOP, R.id.step_short_description_title, ConstraintSet.BOTTOM, 8);
            constraintSet.applyTo(StepDescriptionConstraintLayout);
        }
        else {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(StepDescriptionConstraintLayout);
            constraintSet.connect(R.id.step_description_scroll_view, ConstraintSet.TOP, R.id.simpleExoPlayerView, ConstraintSet.BOTTOM, 8);
            constraintSet.connect(R.id.simpleExoPlayerView, ConstraintSet.TOP, R.id.step_short_description_title, ConstraintSet.BOTTOM, 8);
            constraintSet.applyTo(StepDescriptionConstraintLayout);
        }
    }

    private void setVisibilityForEssentialUIViews(int visibilityOption) {
        StepShortDescriptionTitleTextView.setVisibility(visibilityOption);
        NavigationButtonsContainer.setVisibility(visibilityOption);
        StepDescriptionScrollView.setVisibility(visibilityOption);
    }

    @OnClick(R.id.prev_step_btn)
    protected void NavigateToPreviousStep() {
        navigateToStepByIndex(stepIndex - 1);
    }

    @OnClick(R.id.next_step_btn)
    protected void NavigateToNextStep() {
        navigateToStepByIndex(stepIndex + 1);
    }

    public void navigateToStepByIndex(int index) {

        try {
            stepNavigationCallback.onStepNavigation(index);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        shouldExoPlayerResume = false;
        lastStoredExoPlayerPos = 0;
        releasePlayer();
        this.stepIndex = index;
        setUpStepInformation();
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new StepMediaSessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            StepExoPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mContext, "BakingTime");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    mContext, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
        }

        mExoPlayer.seekTo(lastStoredExoPlayerPos);
        mExoPlayer.setPlayWhenReady(shouldExoPlayerResume);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    public interface OnStepNavigationListener {
        void onStepNavigation(int index);
    }

    private class StepMediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
