package com.example.jules.mymovies.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;

/**
 * This activity show the detailed information
 * of a film. It extends the {@link YouTubeBaseActivity}
 * in order to be able to display a YouTube video (for
 * the trailer).
 */
public class FilmDetailsActivity extends YouTubeBaseActivity {

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
    private YouTubePlayerView mTrailerPlayer;

    /**
     * The film from of which we display details
     */
    private Film mFilm;

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
        mTrailerPlayer = findViewById(R.id.activity_film_details_trailer);

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
                new FetchYouTubeKeyTask(mFilm, this);
        fetchYouTubeKeyTask.execute();

    }

    private class OnYouTubePlayerReady implements YouTubePlayer.OnInitializedListener {

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            Log.d(TAG, "YouTubePlayer => initialization success");
            youTubePlayer.cueVideo(mFilm.getYoutubeKey());
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            Log.d(TAG, "YouTubePlayer => initialization failure");
            Toast.makeText(FilmDetailsActivity.this, R.string.toast_cant_load_trailer, Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchYouTubeKeyTask extends AsyncTask<Void, Void, String> {

        private Film mFilm;
        private FilmDetailsActivity mParentActivity;

        public FetchYouTubeKeyTask(Film film, FilmDetailsActivity parentActivity) {
            mFilm = film;
            mParentActivity = parentActivity;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getYoutubeKey(mFilm.getTmdbId());
        }

        @Override
        protected void onPostExecute(String youtubeKeyFound) {
            if (youtubeKeyFound == null) {
                /*
                 No YouTube key found (so no trailer to show),
                 we hide the YouTube player
                  */
                Log.d(TAG, "No YouTube key found!");
                mParentActivity.mTrailerPlayer.setVisibility(View.GONE);
            } else {
                /*
                 Saves the key and initializes the YouTube player that plays the trailer.
                 The second argument is a listener that handles success and
                 failure of the player initialization.
                  */
                Log.d(TAG, "YouTube key found: " + youtubeKeyFound);
                mFilm.setYoutubeKey(youtubeKeyFound);
                mTrailerPlayer.initialize(AppConstants.YOUTUBE_API_KEY, new OnYouTubePlayerReady());
            }
        }

        /**
         * Tries to retrieve the YouTube key for the first
         * YouTube trailer found, amongst all the trailers
         * returned by the TMDB API for a specific movie.
         * If no YouTube trailer is found, null is returned.
         * @param tmdbFilmId the TMDB identifier of the film
         *                   we want to retrieve the trailer
         *                   YouTube key of.
         * @return the YouTube key (a String) of the first
         *         YouTube trailer found, or null if we
         *         don't find any YouTube trailer.
         */
        @Nullable
        private String getYoutubeKey(int tmdbFilmId) {
            TmdbApi api = new TmdbApi(AppConstants.TMDB_API_KEY);
            List<Video> trailers = api.getMovies().getVideos(tmdbFilmId, AppConstants.TMDB_PARAMETER_LANGUAGE_FRENCH);

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
}
