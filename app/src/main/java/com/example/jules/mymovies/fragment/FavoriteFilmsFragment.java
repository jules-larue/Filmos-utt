package com.example.jules.mymovies.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jules.mymovies.AnalyticsApplication;
import com.example.jules.mymovies.R;
import com.example.jules.mymovies.adapter.FavoriteFilmsAdapter;
import com.example.jules.mymovies.model.DaoSession;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.model.FilmDao;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.FilmsDatabase;
import com.example.jules.mymovies.util.PreferenceUtils;

import java.util.List;
import java.util.Objects;


public class FavoriteFilmsFragment extends Fragment {

    /**
     * The list of user's favorite films
     */
    private RecyclerView mFavoriteFilmsList;

    /**
     * The TextView displayed when no favorite film is saved
     */
    private TextView mNoFavoriteFilmsText;

    /**
     * Adapter to manager and display the list of
     * favorite films.
     */
    private FavoriteFilmsAdapter mFilmsAdapter;

    /**
     * Progress bar shown when favorite  films are being
     * fetched from local database.
     */
    private ProgressBar mProgress;

    private static final int NUMBER_COLUMNS = 2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_films, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFavoriteFilmsList = Objects.requireNonNull(getView()).findViewById(R.id.favorite_films_fragment_list);
        mNoFavoriteFilmsText = getView().findViewById(R.id.tv_no_favorite_movies);
        mProgress = getView().findViewById(R.id.favorite_films_fragment_progress);

        // We display the in a Grid Layout with 2 columns
        GridLayoutManager listLayoutManager = new GridLayoutManager(getActivity(), NUMBER_COLUMNS);
        mFavoriteFilmsList.setLayoutManager(listLayoutManager);
    }

    private class LoadFavoriteFilmsTask extends AsyncTask<Object, Void, List<Film>> {

        @Override
        protected void onPreExecute() {
            // We show progress before any task starts
            showProgress();
        }

        @Override
        protected List<Film> doInBackground(Object... args) {
            Context context = (Context) args[0];

            // Get connection to database
            DaoSession daoSession = FilmsDatabase.getDaoSession(context);
            FilmDao filmDao = daoSession.getFilmDao();

            // Load all films from database
            return filmDao.loadAll();
        }

        @Override
        protected void onPostExecute(List<Film> favoriteFilms) {
            if (favoriteFilms.isEmpty()) {
                // No favorite films
                showNoFilmsMessage();
            } else {
                // At least one favorite film found
                showFilms();
                initAdapter(favoriteFilms);
            }
        }
    }

    /**
     * Initializes the films adapter with a list of films,
     * and displays them on the fragment.
     * @param favoriteFilms the list of films to set in the adapter
     */
    private void initAdapter(List<Film> favoriteFilms) {
        // Create the adapter with the films
        mFilmsAdapter = new FavoriteFilmsAdapter(getActivity(), favoriteFilms);
        mFavoriteFilmsList.setAdapter(mFilmsAdapter);
    }

    /**
     * Make the list of films visible,
     * and hide the message for no films.
     */
    private void showFilms() {
        mProgress.setVisibility(View.GONE);
        mFavoriteFilmsList.setVisibility(View.VISIBLE);
        mNoFavoriteFilmsText.setVisibility(View.GONE);
    }

    /**
     * Make the message for no films  visible,
     * and hide the list of films.
     */
    private void showNoFilmsMessage() {
        mProgress.setVisibility(View.GONE);
        mFavoriteFilmsList.setVisibility(View.GONE);
        mNoFavoriteFilmsText.setVisibility(View.VISIBLE);
    }

    /**
     * Shows progress bar and hides any other view.
     */
    private void showProgress() {
        // Show progress
        mProgress.setVisibility(View.VISIBLE);

        // Hide other view
        mFavoriteFilmsList.setVisibility(View.GONE);
        mNoFavoriteFilmsText.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadFavoriteFilmsTask().execute(getContext());
        if (PreferenceUtils.checkUserConsent(Objects.requireNonNull(getContext()))) {
            track();
        }
    }

    private void track() {
        AnalyticsApplication application = (AnalyticsApplication) Objects.requireNonNull(getActivity()).getApplication();
        application.sendSreenTracking(AppConstants.Analytics.FAVORITE_MOVIES);
    }
}
