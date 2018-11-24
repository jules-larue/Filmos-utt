package com.example.jules.mymovies.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.jules.mymovies.listener.OnLoadMoreListener;
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

    /**
     * Listener to handle endless scrolling
     * for the films RecyclerView.
     */
    private OnLoadMoreListener mOnLoadMoreFilmsListener;

    /**
     * The last page of films fetched from the API.
     */
    private MovieResultsPage mLastPageDisplayed;

    /**
     * Adapter for the films to display
     */
    private FilmsListAdapter mFilmsAdapter;

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

        mOnLoadMoreFilmsListener = new OnLoadMoreListener() {

            /**
             * Called when we reach the end of the
             * RecyclerView when scrolling. We fetch
             * the next page of popular films from the API.
             */
            @Override
            public void onLoadMore() {
                int nextPage = mLastPageDisplayed.getPage() + 1;
                int totalPages = mLastPageDisplayed.getTotalPages();

                // Check we are NOT at the last page
                if (nextPage <= totalPages) {
                    // Fetch the next page of films
                    FetchPopularFilmsTask fetchPopularFilmsTask = new FetchPopularFilmsTask((PopularFilmsFragment.this));
                    fetchPopularFilmsTask.execute(nextPage);
                }
            }
        };

        /*
         Adapter initialization.
         /!\ Create instance of FilmsListAdapter after
         call to setLayoutManager(new LinearLayoutManager(getContext()))
          */
        mFilmsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFilmsAdapter = new FilmsListAdapter(getContext(), mFilmsList);
        mFilmsAdapter.setOnLoadMoreListener(mOnLoadMoreFilmsListener);
        mFilmsList.setAdapter(mFilmsAdapter);

        // Fetch the first page of most popular films
        FetchPopularFilmsTask fetchPopularFilmsTask = new FetchPopularFilmsTask(this);
        fetchPopularFilmsTask.execute(1);
    }

    private static class FetchPopularFilmsTask extends AsyncTask<Integer, Void, MovieResultsPage> {

        /**
         * This task is launched from that fragment
         */
        private PopularFilmsFragment mParentFragment;

        /**
         * TMDB API client instance
         */
        private TmdbApi mApi;

        FetchPopularFilmsTask(PopularFilmsFragment parentFragment) {
            mParentFragment = parentFragment;
        }

        @Override
        protected MovieResultsPage doInBackground(Integer... args) {
            // Get the page number to fetch
            int pageNumber = args[0];

            if (mApi == null) {
                /*
                Not in constructor because it uses network
                so we must place this statement in doInBackground
                which is not run on Main UI Thread.
                 */
                mApi = new TmdbApi(AppConstants.TMDB_API_KEY);
            }

            /*
             Get the popular movies from API. Here we get movies in french.
             The result is stored as the last page of results fetched.
              */
            mParentFragment.mLastPageDisplayed = mApi.getMovies()
                    .getPopularMovies(AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH, pageNumber);

            return mParentFragment.mLastPageDisplayed;
        }

        @Override
        protected void onPostExecute(MovieResultsPage movieDbs) {
            onFilmsFetched(movieDbs);
        }

        /**
         * This method is called once all the
         * movie have been fetched.
         * Basically, it converts the results into a list
         * of {@link Film} objects and updates the RecyclerView adapter.
         * @param resultsFetched the results that have just been fetched
         */
        private void onFilmsFetched(MovieResultsPage resultsFetched) {
            mParentFragment.hideLoadingWidget();

            /*
             We build a list of Film objects from
             the results (movies) fetched
            */
            ArrayList<Film> filmsFetched = MovieUtil.mapPageResultsToFilmsList(resultsFetched);

            // Update the adapter
            mParentFragment.mFilmsAdapter.addFilms(filmsFetched);
            mParentFragment.mFilmsAdapter.notifyDataSetChanged();
            mParentFragment.mFilmsAdapter.setLoaded();
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


}
