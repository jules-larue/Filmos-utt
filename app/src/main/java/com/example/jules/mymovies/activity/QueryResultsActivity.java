package com.example.jules.mymovies.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.adapter.FilmsListAdapter;
import com.example.jules.mymovies.dialog.ConnectionProblemDialog;
import com.example.jules.mymovies.listener.OnLoadMoreListener;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.MovieUtil;

import java.util.ArrayList;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.tools.MovieDbException;

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
     * Button to retry loading movies
     */
    private Button mBtnRetry;

    /**
     * Top padding value (in dp) for the first films RecyclerView
     * item to be displayed under the FloatingSearchView nicely.
     */
    public static final int FIRST_LIST_ITEM_TOP_PADDING = 16;

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
        mBtnRetry = findViewById(R.id.films_results_btn_retry);

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
                    FetchResultsTask fetchPopularFilmsTask = new FetchResultsTask(false);
                    fetchPopularFilmsTask.execute(query, nextPage);
                }
            }
        };

        // Init retry button
        mBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPage;
                if (mLastPageFetched == null) {
                    // Nothing fetched yet so we want the first page
                    nextPage = 1;
                } else {
                    nextPage = mLastPageFetched.getPage() + 1;
                }
                new FetchResultsTask(true)
                        .execute(query, nextPage);
            }
        });

        /*
         Adapter initialization.
         /!\ Create instance of FilmsListAdapter after
         call to setLayoutManager(new LinearLayoutManager(getContext()))
          */
        mRvResults.setLayoutManager(new LinearLayoutManager(this));
        mFilmsAdapter = new FilmsListAdapter(this, mRvResults);
        mFilmsAdapter.setOnLoadMoreListener(mOnLoadMoreResultsListener);
        mRvResults.setAdapter(mFilmsAdapter);

        // Add top padding
        mFilmsAdapter.setFirstItemTopPadding(FIRST_LIST_ITEM_TOP_PADDING);

        // Fetch first page of results
        FetchResultsTask fetchResultsTask = new FetchResultsTask(true);
        fetchResultsTask.execute(query, 1);
    }

    /**
     * This class fetches one specific page of the movie
     * results corresponding to the user query.
     */
    private class FetchResultsTask extends AsyncTask<Object, Void, MovieResultsPage> {

        /**
         * Whether or not we whould show the progress
         * bar in the center of the screen.
         * It should be false when when scroll to load more,
         * in order to not hide the list while scrolling.
         */
        private boolean mShowProgress;

        public FetchResultsTask(boolean showProgress) {
            mShowProgress = showProgress;
        }

        @Override
        protected void onPreExecute() {
            if (mShowProgress) {
                showProgress();
            }
        }

        @Override
        protected MovieResultsPage doInBackground(Object... args) {
            // args = { query (String), pageNumber (int)}
            String query = (String) args[0];
            int pageNumber = (int) args[1];

            try {
                TmdbApi api = new TmdbApi(AppConstants.TMDB_API_KEY);

                // Current page is the first one for now
                mLastPageFetched = api.getSearch().searchMovie(query,
                        null, // year does not matter
                        AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH, // results in french
                        AppConstants.TMDB_PARAMETER_INCLUDE_ADULT_IN_SEARCH, // include adult movies ?
                        pageNumber);

                return mLastPageFetched;
            } catch(MovieDbException e) {
                // Problem while fetching movies
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieResultsPage results) {
            if (results == null) {
                // Problem while fetching results
                onFetchingResultsProblem();
            } else {
                onResultsFetched(results);
            }
        }
    }

    /**
     * Callback invoked when there is a problem while
     * trying to fetch the results from the API.
     */
    private void onFetchingResultsProblem() {
        showRetry();

        // Show information dialog
        new ConnectionProblemDialog(this).show();
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
        mBtnRetry.setVisibility(View.GONE);
    }

    /**
     * Shows that results have been fetched by
     * displaying the list with the films and
     * hiding the progress bar.
     */
    private void showResults() {
        mRvResults.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mBtnRetry.setVisibility(View.GONE);
    }

    /**
     * Shows the retry button and hides
     * any other view. After that the user
     * can only retry to fetch the movies
     * because he will only see the 'retry'
     * button.
     */
    private void showRetry() {
        mRvResults.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mBtnRetry.setVisibility(View.VISIBLE);
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
        showResults();
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
