package com.example.jules.mymovies.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.jules.mymovies.R;
import com.example.jules.mymovies.activity.QueryResultsActivity;
import com.example.jules.mymovies.adapter.FilmsListAdapter;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.MovieUtil;

import java.util.ArrayList;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.MovieResultsPage;


/**
 * A fragment displayed in the
 * {@link com.example.jules.mymovies.activity.MainActivity}
 * that displays the list of most popular movies, and
 * allows to search movies by name.
 */
public class PopularFilmsFragment extends Fragment {

    /**
     * The list of the popular movies that we display.
     */
    private RecyclerView mFilmsList;

    /**
     * The search bar allowing to perform searches.
     */
    private FloatingSearchView mSearchBar;

    /**
     * The progress bar when films are being fetched
     */
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_films, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFilmsList = Objects.requireNonNull(getView()).findViewById(R.id.popular_films_list);
        mSearchBar = getView().findViewById(R.id.search_bar);
        mProgressBar = getView().findViewById(R.id.popular_films_progress_bar);

        // Set search bar to appear over any other view
        mSearchBar.bringToFront();
        mSearchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                /*
                Nothing for now. May be implemented in the future
                if we get time to handle suggestions.
                 */
            }

            @Override
            public void onSearchAction(String currentQuery) {
                String query = mSearchBar.getQuery()
                        .trim(); // remove leading and trailing spaces

                // Set proper search bar text
                mSearchBar.setSearchText(query);

                if (query.isEmpty()) {
                    // No query
                    Toast.makeText(getActivity(), R.string.toast_please_input_search, Toast.LENGTH_SHORT).show();
                } else {
                    // User has typed a valid query
                    Intent queryResultsIntent = new Intent(getContext(), QueryResultsActivity.class);
                    queryResultsIntent.putExtra(QueryResultsActivity.EXTRA_QUERY, query);

                    // Start results activity
                    Objects.requireNonNull(getContext()).startActivity(queryResultsIntent);
                }
            }
        });

        // Fetch the most popular films
        FetchPopularFilmsTask popularFilmsTask = new FetchPopularFilmsTask(this);
        popularFilmsTask.execute();
    }

    private static class FetchPopularFilmsTask extends AsyncTask<Void, Void, MovieResultsPage> {

        /**
         * This task is launched from that fragment
         */
        private PopularFilmsFragment mParentFragment;

        public FetchPopularFilmsTask(PopularFilmsFragment parentFragment) {
            mParentFragment = parentFragment;
        }

        @Override
        protected void onPreExecute() {
            // We show that films are being fetched...
            mParentFragment.showLoadingWidget();
        }

        @Override
        protected MovieResultsPage doInBackground(Void... voids) {
            // Create API client instance
            TmdbApi api = new TmdbApi(AppConstants.TMDB_API_KEY);

            /*
             Get the popular movies from API.
             Here we get movies in french, and first page only (20 results)
              */
            return api.getMovies()
                    .getPopularMovies(AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH, 1);
        }

        @Override
        protected void onPostExecute(MovieResultsPage movieDbs) {
            mParentFragment.onFilmsFetched(movieDbs);
        }
    }

    public void showLoadingWidget() {
        mProgressBar.setVisibility(View.VISIBLE);
        mFilmsList.setVisibility(View.GONE);
    }

    public void hideLoadingWidget() {
        mProgressBar.setVisibility(View.GONE);
        mFilmsList.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called once all the
     * movie have been fetched.
     * We hide the progress bar as no more fetch
     * work is in progress and we update our adapter,
     * so our films will be shown.
     * @param resultsFetched the results that have just been fetched
     */
    public void onFilmsFetched(MovieResultsPage resultsFetched) {
        hideLoadingWidget();

        /*
         We build a list of Film objects from
         the results (movies) fetched
          */
        ArrayList<Film> filmsToDisplay = MovieUtil.mapPageResultsToFilmsList(resultsFetched);

        // Create the adapter
        FilmsListAdapter filmsListAdapter =
                new FilmsListAdapter(getContext(), filmsToDisplay);
        mFilmsList.setAdapter(filmsListAdapter);
        mFilmsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
