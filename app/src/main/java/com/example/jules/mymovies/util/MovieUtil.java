package com.example.jules.mymovies.util;

import com.example.jules.mymovies.model.Film;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MovieUtil {

    /**
     * Creates a list of 'Film' objects from
     * a specific 'MovieResultsPage' instance.
     * @param results the list of movies to map
     * @return an ArrayList of 'Film' objects corresponding
     * to the movies in the 'results' parameter
     */
    public static ArrayList<Film> mapPageResultsToFilmsList(MovieResultsPage results) {
        // Create a list of 'Film' objects
        // from the results
        ArrayList<Film> filmObjects = new ArrayList<>();

        Film singleFilmObject;
        for (MovieDb movieDbResult : results) {
            try {
                // Release date
                SimpleDateFormat releaseDateFormat =
                        new SimpleDateFormat(AppConstants.TMDB_RELEASE_DATE_FORMAT);
                Date releaseDate = releaseDateFormat
                        .parse(movieDbResult.getReleaseDate());

                // Create the film object
                singleFilmObject = new Film(
                        movieDbResult.getId(),
                        movieDbResult.getTitle(),
                        releaseDate, // date to convert
                        movieDbResult.getPosterPath());

                filmObjects.add(singleFilmObject);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } // end for (MovieDb : results)

        // Return the list created
        return filmObjects;
    }
}
