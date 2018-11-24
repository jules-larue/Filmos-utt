package com.example.jules.mymovies.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.adapter.FilmsListAdapter;
import com.example.jules.mymovies.listener.OnLoadMoreListener;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.MovieUtil;

import java.util.ArrayList;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class QueryResultsActivity extends AppCompatActivity {

    private RecyclerView mRvResults;

    private ProgressBar mProgressBar;

    /**
     * Listener when we reach the end of the films list
     * while scrolling.
     */
    private OnLoadMoreListener mOnLoadMoreResultsListener;

    /**
     * Adapter for the list of films.
     */
    private FilmsListAdapter mFilmsAdapter;

    /**
     * The last page of results fetched from the API.
     */
    private MovieResultsPage mLastPageFetched;

    /**
     * Extra intent parameter to pass the
     * user query to this activity.
     */
    public static final String EXTRA_QUERY = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_results);

        mRvResults = findViewById(R.id.activity_film_results_list);
        mProgressBar = findViewById(R.id.activity_film_results_progress_bar);

        // Get user query
        final String query = Objects.requireNonNull(getIntent().getExtras())
                .getString(EXTRA_QUERY);

        // Display back arrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set user query as action bar title
        getSupportActionBar().setTitle(query);

        mOnLoadMoreResultsListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                int nextPage = mLastPageFetched.getPage() + 1;
                int totalPages = mLastPageFetched.getTotalPages();

                // Check we are NOT at the last page
                if (nextPage <= totalPages) {
                    // Fetch the next page of films
                    FetchResultsTask fetchPopularFilmsTask = new FetchResultsTask();
                    fetchPopularFilmsTask.execute(query, nextPage);
                }
            }
        };

        /*
         Adapter initialization.
         /!\ Create instance of FilmsListAdapter after
         call to setLayoutManager(new LinearLayoutManager(getContext()))
          */
        mRvResults.setLayoutManager(new LinearLayoutManager(this));
        mFilmsAdapter = new FilmsListAdapter(this, mRvResults);
        mFilmsAdapter.setOnLoadMoreListener(mOnLoadMoreResultsListener);
        mRvResults.setAdapter(mFilmsAdapter);

        // Fetch first page of results
        FetchResultsTask fetchResultsTask = new FetchResultsTask();
        fetchResultsTask.execute(query, 1);
    }

    /**
     * This class fetches one specific page of the movie
     * results corresponding to the user query.
     */
    private class FetchResultsTask extends AsyncTask<Object, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Object... args) {
            // args = { query (String), pageNumber (int)}
            String query = (String) args[0];
            int pageNumber = (int) args[1];

            TmdbApi api = new TmdbApi(AppConstants.TMDB_API_KEY);

            // Current page is the first one for now
            mLastPageFetched = api.getSearch().searchMovie(query,
                    null, // year does not matter
                    AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH, // results in french
                    AppConstants.TMDB_PARAMETER_INCLUDE_ADULT_IN_SEARCH, // include adult movies ?
                    pageNumber);

            return mLastPageFetched;
        }

        @Override
        protected void onPostExecute(MovieResultsPage results) {
            onResultsFetched(results);
        }
    }

    /**
     * Shows that the activity is
     * fetching the results by displaying
     * the progress bar and hiding the
     * list of movies.
     */
    private void showProgress() {
        mRvResults.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Shows that results have been fetched by
     * displaying the list with the films and
     * hiding the progress bar.
     */
    private void hideProgress() {
        mRvResults.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Called once the results of the query
     * have been fetched.
     * @param resultsPage the page of results we fetched
     */
    public void onResultsFetched(MovieResultsPage resultsPage) {
        /*
         Create a list of 'Film'
         objects from the results
          */
        ArrayList<Film> films = MovieUtil.mapPageResultsToFilmsList(resultsPage);

        // Set the adapter for films RecyclerView
        mFilmsAdapter.addFilms(films);
        mFilmsAdapter.notifyDataSetChanged();
        mFilmsAdapter.setLoaded();

        // Hide progress (to show the list)
        hideProgress();
    }


    /**
     * This method is used to handle a click
     * on the "back arrow" in action bar, to
     * get back to the previous activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                /*
                When back button pressed, get
                back to previous activity (same
                action than onBackPressed())
                 */
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
