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
     * The page of results that is currently
     * displayed.
     */
    private MovieResultsPage mCurrentPage;

    /**
     * A reference to the TMDB API
     * to perform requests.
     */
    private TmdbApi mApi;

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
        String query = Objects.requireNonNull(getIntent().getExtras())
                .getString(EXTRA_QUERY);

        // Display back arrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set user query as action bar title
        getSupportActionBar().setTitle(query);

        // Fetch results
        FetchResultsTask fetchResultsTask = new FetchResultsTask();
        fetchResultsTask.execute(query);
    }

    /**
     * This class fetches the movie results corresponding
     * to the user query, and updates the UI to show the
     * progress of the task.
     */
    private class FetchResultsTask extends AsyncTask<String, Void, MovieResultsPage> {

        @Override
        protected void onPreExecute() {
            // Show that task is in progress
            showProgress();
        }

        @Override
        protected MovieResultsPage doInBackground(String... args) {
            // Get the query from the arguments
            String query = args[0];

            mApi = new TmdbApi(AppConstants.TMDB_API_KEY);

            // Current page is the first one for now
            mCurrentPage = mApi.getSearch().searchMovie(query,
                    null, // year does not matter
                    AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH, // results in french
                    AppConstants.TMDB_PARAMETER_INCLUDE_ADULT_IN_SEARCH, // include adult movies ?
                    1); // 1st page

            return mCurrentPage;
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
        FilmsListAdapter filmsAdapter = new FilmsListAdapter(this, films);
        mRvResults.setAdapter(filmsAdapter);
        mRvResults.setLayoutManager(new LinearLayoutManager(this));

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
