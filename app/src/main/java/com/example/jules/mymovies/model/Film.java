package com.example.jules.mymovies.model;

import java.util.Date;

/**
 * Represents the films that we
 * manipulate in the application.
 */
public class Film {

    /**
     * Title of the film
     */
    private String mTitle;

    /**
     * Release date of the film
     */
    private Date mReleaseDate;

    /**
     * The remote URL of the poster
     */
    private String mPosterUrl;

    /**
     * Builds a film from its title, release date and poster url
     * @param title the title of the film
     * @param releaseDate the release date of the film
     * @param posterUrl the url of the film poster
     */
    public Film(String title, Date releaseDate, String posterUrl) {
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterUrl = posterUrl;
    }

    public String getTitle () {
        return mTitle;
    }

    public void setTitle(String newTitle) {
        mTitle = newTitle;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(Date newReleaseDate) {
        mReleaseDate = newReleaseDate;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String newPosterUrl) {
        mPosterUrl = newPosterUrl;
    }

}
