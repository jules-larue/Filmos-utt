package com.example.jules.mymovies.util;

public class AppConstants {

    /**
     * YouTube API key
     */
    public static final String YOUTUBE_API_KEY = "AIzaSyAVU1YLHDEGhVBREfIXQ6w5Eg7YFk02Qsc";

    public static final String TMDB_API_KEY = "4be036249e60cb50f0fbafd6949f692b";


    /**
     * The TMDB language parameter value for "French"
     */
    public static final String TMDB_PARAMETER_LANGUAGE_FRENCH = "fr";

    /**
     * The TMDB paramter to whether or not
     * include adult movies in search results
     */
    public static final boolean TMDB_PARAMETER_INCLUDE_ADULT_IN_SEARCH = false;

    /**
     * Format of release date in TMDB responses
     */
    public static final String TMDB_RELEASE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Base URL for poster paths
     */
    public static final String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    /**
     * Defines constants related to the application
     * analytics, such as tags.
     */
    public class Analytics {

        public static final String POPULAR_MOVIES = "PopularMovies";
        public static final String SEARCH_MOVIES = "SearchMovies";
        public static final String MOVIE_DETAILS = "MovieDetails";
        public static final String FAVORITE_MOVIES = "FavoriteMovies";
    }
}
