package com.example.jules.mymovies.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Represents the films that we
 * manipulate in the application.
 */
@Entity
public class Film {

    /**
     * Unique API identifier for the movie.
     * This attribute is also the primary key
     * for this table in local database.
     */
    @Id
    private int id;

    /**
     * Title of the film
     */
    @NotNull
    private String title;

    /**
     * Release date of the film
     */
    @NotNull
    private Date releaseDate;

    /**
     * The remote URL of the poster
     */
    @NotNull
    private String posterUrl;

    /**
     * The YouTube key for the trailer.
     * This value may be null if no YouTube key
     * is found in the TMDB API.
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

    public Film(int id, String title, Date releaseDate, String posterUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
    }

    @Generated(hash = 298382474)
    public Film(int id, @NotNull String title, @NotNull Date releaseDate,
            @NotNull String posterUrl, String youtubeKey) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.youtubeKey = youtubeKey;
    }

    @Generated(hash = 1658281933)
    public Film() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
