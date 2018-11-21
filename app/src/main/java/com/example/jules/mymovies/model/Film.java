package com.example.jules.mymovies.model;

import java.util.Date;

/**
 * Represents the films that we
 * manipulate in the application.
 */
public class Film {

    /**
     * Unique identifier of the film
     */
    private int tmdbId;

    /**
     * Title of the film
     */
    private String title;

    /**
     * Release date of the film
     */

    private Date releaseDate;

    /**
     * The remote URL of the poster
     */
    private String posterUrl;

    /**
     * The YouTube key for the trailer.
     * This value may be null if no YouTube key
     *
     */
    private String youtubeKey;

    /**
     * Builds a film from its title, release date and poster url.
     * @param title the title of the film
     * @param releaseDate the release date of the film
     * @param posterUrl the url of the film poster
     */
    public Film(String title, Date releaseDate, String posterUrl) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
    }

    /**
     * Builds a film from its title, release date, poster url
     * and YouTube trailer key.
     * @param title the title of the film
     * @param releaseDate the release date of the film
     * @param posterUrl the url of the film poster
     * @param youtubeKey the youtube key of the film trailer
     */
    public Film(String title, Date releaseDate, String posterUrl, String youtubeKey) {
        this(title, releaseDate, posterUrl);
        this.youtubeKey = youtubeKey;
    }

    public Film(int tmdbId, String title, Date releaseDate, String posterUrl) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date newReleaseDate) {
        releaseDate = newReleaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String newPosterUrl) {
        posterUrl = newPosterUrl;
    }

    public String getYoutubeKey() {
        return youtubeKey;
    }

    public void setYoutubeKey(String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }
}
