package com.example.jules.mymovies.util;

import com.example.jules.mymovies.model.Film;

import java.text.ParseException;
import java.util.ArrayList;

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
                // Create the film object
                //Log.d("FilmDate", "release date: " + movieDbResult.getReleaseDate());
                singleFilmObject = new Film(
                        Long.valueOf(movieDbResult.getId()),
                        movieDbResult.getTitle(),
                        movieDbResult.getReleaseDate(),
                        movieDbResult.getPosterPath());

                filmObjects.add(singleFilmObject);

            } catch (ParseException e) {
                /*
                   We go here if there is an error in the release
                   date format.
                   That may happen if teh TMDB API suddenly decides
                   to change the format of release date in their response...
                 */
                e.printStackTrace();
            }
        } // end for (MovieDb : results)

        // Return the list created
        return filmObjects;
    }
}
