package com.example.jules.mymovies.fragment;

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

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.jules.mymovies.R;
import com.example.jules.mymovies.adapter.FilmsListAdapter;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
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
        ArrayList<Film> filmsToDisplay = new ArrayList<>();

        Film filmToAdd;
        for (MovieDb movie : resultsFetched) {

            // Create a Date object for the film release date
            SimpleDateFormat releaseDateFormat =
                    new SimpleDateFormat(AppConstants.TMDB_RELEASE_DATE_FORMAT);
            try {
                filmToAdd = new Film(movie.getTitle(),
                        releaseDateFormat.parse(movie.getReleaseDate()),
                        movie.getPosterPath());

                // Add the film
                filmsToDisplay.add(filmToAdd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        FilmsListAdapter filmsListAdapter =
                new FilmsListAdapter(getContext(), filmsToDisplay);
        mFilmsList.setAdapter(filmsListAdapter);
        mFilmsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
