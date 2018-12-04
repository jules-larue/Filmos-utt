package com.example.jules.mymovies.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.asynctask.HandleFavoriteItemClickTask;
import com.example.jules.mymovies.asynctask.SetFavoriteIconTask;
import com.example.jules.mymovies.model.Film;

import com.example.jules.mymovies.util.AppConstants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Video;


public class FilmDetailsActivity extends AppCompatActivity {

    /**
     * Rounded Image View for the poster
     */
    private RoundedImageView mRivPoster;

    /**
     * TextView for the title
     */
    private TextView mTvTitle;

    /**
     * TextView for the release date
     */
    private TextView mTvReleaseDate;

    /**
     * The YouTube player for the film trailer
     */
    private YouTubePlayerSupportFragment mYouTubePlayer;

    /**
     * Layout that acts as container for the YouTube player,
     * used to handle the player (fragment) visibility.
     */
    private LinearLayout mYouTubePlayerContainer;

    /**
     * The film from of which we display details
     */
    private Film mFilm;

    /**
     * The TextView with a message indicating
     * that no trailer is found for the movie
     * to display.
     */
    private TextView mTvMessageNoTrailer;

    /**
     * Date format to display for the release date
     * of the film
     */
    public static final String RELEASE_DATE_FORMAT = "dd MMMM yyyy";

    /**
     * Extra parameter for the film to display
     * the details of
     */
    public static final String EXTRA_FILM_JSON = "film";

    /**
     * Resource id of the icon to display when
     * the film is saved in local database.
     */
    public static final int ICON_ID_FILM_IN_FAVORITES = R.drawable.baseline_favorite_white_24;

    /**
     * Resource id of the icon to display when
     * the film is not saved in local database.
     */
    public static final int ICON_ID_FILM_NOT_IN_FAVORITES = R.drawable.baseline_favorite_border_white_24;

    /**
     * TAG for debug logs
     */
    public static final String TAG = "YouTubePlayerDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);

        mRivPoster = findViewById(R.id.activity_film_details_poster);
        mTvTitle = findViewById(R.id.activity_film_details_title);
        mTvReleaseDate = findViewById(R.id.activity_film_details_release_date);
        mYouTubePlayer = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        mYouTubePlayerContainer = findViewById(R.id.youtube_fragment_container);
        mTvMessageNoTrailer = findViewById(R.id.activity_film_details_tv_no_trailer);

        // Retrieve film from intent extras
        String jsonFilm = Objects.requireNonNull(getIntent()
                .getExtras())
                .getString(EXTRA_FILM_JSON);
        mFilm = new Gson().fromJson(jsonFilm, Film.class);

        // Bind data to view
        mTvTitle.setText(mFilm.getTitle());

        SimpleDateFormat releaseDateFormat =
                new SimpleDateFormat(RELEASE_DATE_FORMAT);
        String formattedReleaseDate = releaseDateFormat.format(mFilm.getReleaseDate());
        mTvReleaseDate.setText(formattedReleaseDate);

        String posterUrl = AppConstants.TMDB_POSTER_BASE_URL + mFilm.getPosterUrl();
        Picasso.get().load(posterUrl).into(mRivPoster);

        /*
         Try to retrieve the YouTube key for the trailer.
         The task hides the YouTube player if no key
         is found, initializes it otherwise.
          */
        FetchYouTubeKeyTask fetchYouTubeKeyTask =
                new FetchYouTubeKeyTask(mFilm);
        fetchYouTubeKeyTask.execute();

        // Show action to go to previous activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set action bar title
        getSupportActionBar().setTitle(R.string.activity_film_details_action_bar_title);
    }

    private class OnYouTubePlayerReady implements YouTubePlayer.OnInitializedListener {

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            Log.d(TAG, "YouTubePlayer => initialization success");
            // Show the player
            showTrailer();

            // Initialize the video
            youTubePlayer.cueVideo(mFilm.getYoutubeKey());
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            Log.d(TAG, "YouTubePlayer => initialization failure");
            Toast.makeText(FilmDetailsActivity.this, R.string.toast_cant_load_trailer, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays the YouTube player
     * and hides the message for 'no trailer'.
     */
    private void showTrailer() {
        mYouTubePlayerContainer.setVisibility(View.VISIBLE);
        mTvMessageNoTrailer.setVisibility(View.GONE);
    }

    /**
     * Displays the message for 'no trailer'
     * and hides the YouTube player.
     */
    private void showMessageNoTrailer() {
        mYouTubePlayerContainer.setVisibility(View.GONE);
        mTvMessageNoTrailer.setVisibility(View.VISIBLE);
    }

    private class FetchYouTubeKeyTask extends AsyncTask<Void, Void, String> {

        private Film mFilm;

        public FetchYouTubeKeyTask(Film film) {
            mFilm = film;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getYoutubeKey(Math.toIntExact(mFilm.getId()));
        }

        @Override
        protected void onPostExecute(String youtubeKeyFound) {
            if (youtubeKeyFound == null) {
                /*
                 No YouTube key found (so no trailer to show),
                 we show an informing message.
                  */
                Log.d(TAG, "No YouTube key found!");
                showMessageNoTrailer();
            } else {
                /*
                 Saves the key and initializes the YouTube player that plays the trailer.
                 The second argument is a listener that handles success and
                 failure of the player initialization.
                  */
                Log.d(TAG, "YouTube key found: " + youtubeKeyFound);
                mFilm.setYoutubeKey(youtubeKeyFound);
                mYouTubePlayer.initialize(AppConstants.YOUTUBE_API_KEY, new OnYouTubePlayerReady());
            }
        }

        /**
         * Tries to retrieve the YouTube key for the first
         * YouTube trailer found, amongst all the trailers
         * returned by the TMDB API for a specific movie.
         * If no YouTube trailer is found, null is returned.
         * @param filmId the id of the movie used by the
         *               API to find uniquely identify it.
         * @return the YouTube key (a String) of the first
         *         YouTube trailer found, or null if we
         *         don't find any YouTube trailer.
         */
        @Nullable
        private String getYoutubeKey(int filmId) {
            TmdbApi api = new TmdbApi(AppConstants.TMDB_API_KEY);
            List<Video> trailers = api.getMovies().getVideos(filmId,
                    AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH);

            for (Video trailer : trailers) {
                if (trailer.getSite().equalsIgnoreCase("youtube")) {
                    // First YouTube trailer found,
                    // return the YouTube key
                    return trailer.getKey();
                }
            }

            // No YouTube trailer found
            return null;
        }
    }

    /**
     * Callback called when user clicks
     * on the 'favorite' menu item.
     * If the film is in the user's favorite
     * movies database, delete it from it.
     * Otherwise, save it.
     * @param favoriteMenuItem a reference the favorite
     *                         menu item that user clicked.
     */
    @SuppressLint("StaticFieldLeak")
    private void handleFavoriteItemClick(MenuItem favoriteMenuItem) {
        new HandleFavoriteItemClickTask(favoriteMenuItem, mFilm, this) {
            @Override
            protected int getFavoriteIconIdToDisplay(boolean isFilmSaved) {
                return isFilmSaved ?
                        ICON_ID_FILM_IN_FAVORITES :
                        ICON_ID_FILM_NOT_IN_FAVORITES;
            }
        }.execute(favoriteMenuItem, mFilm, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.film_details_action_bar, menu);

        MenuItem favoriteItem = menu.findItem(R.id.item_favorite);

        // Init 'favorite' icon
        new SetFavoriteIconTask(favoriteItem, mFilm, this) {
            @Override
            protected int getFavoriteIconIdToDisplay(boolean isFilmSaved) {
                return isFilmSaved ?
                        ICON_ID_FILM_IN_FAVORITES :
                        ICON_ID_FILM_NOT_IN_FAVORITES;
            }
        }.execute(favoriteItem, mFilm, this);

        return true;
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

            case R.id.item_favorite:
                /*
                When favorite icon pressed, add
                or delete the film from favorite
                 */
                handleFavoriteItemClick(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
