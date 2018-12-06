package com.example.jules.mymovies.model;

import com.example.jules.mymovies.util.AppConstants;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Property(nameInDb = "_id")
    private Long id;

    /**
     * Title of the film
     */
    @NotNull
    private String title;

    /**
     * Release date of the film
     */
    @NotNull
    private String releaseDate;

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
     * @param releaseDate the release date of the film, formatted
     *                    with the {@link Film} date format specified
     *                    in this class.
     * @param posterUrl the url of the film poster
     * @throws ParseException if the releaseDate format doesn't match
     *                        the release date format specified in
     *                        this class (see RELEASE_DATE_FORMAT attribute).
     */
    public Film(String title, String releaseDate, String posterUrl) throws ParseException {
        this.title = title;
        this.posterUrl = posterUrl;

        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.TMDB_RELEASE_DATE_FORMAT);
        // Throws a ParseException if date format is incorrect
        dateFormat.parse(releaseDate);

        this.releaseDate = releaseDate;
    }

    /**
     * Builds a film from its title, release date, poster url
     * and YouTube trailer key.
     * @param title the title of the film
     * @param releaseDate the release date of the film
     * @param posterUrl the url of the film poster
     * @param youtubeKey the youtube key of the film trailer
     * @throws ParseException if the releaseDate format doesn't match
     *                        the release date format specified in
     *                        this class (see AppConstants.TMDB_RELEASE_DATE_FORMAT
     *                        attribute).
     */
    public Film(String title, String releaseDate, String posterUrl, String youtubeKey) throws ParseException {
        this(title, releaseDate, posterUrl);
        this.youtubeKey = youtubeKey;
    }

    public Film(Long id, String title, String releaseDate, String posterUrl) throws ParseException {
        this(title, releaseDate, posterUrl);
        this.id = id;
    }

    @Generated(hash = 1452500125)
    public Film(Long id, @NotNull String title, @NotNull String releaseDate, @NotNull String posterUrl,
            String youtubeKey) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.youtubeKey = youtubeKey;
    }

    @Generated(hash = 1658281933)
    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String newReleaseDate) {
        releaseDate = newReleaseDate;
    }

    /**
     * Returns the release date of the film as a {@link Date} object.
     * @throws ParseException if the date format of the release date attribute
     * does not match the TMDB release date format stored in {@link AppConstants}.
     */
    public Date getReleaseDateAsDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.TMDB_RELEASE_DATE_FORMAT);
        return dateFormat.parse(releaseDate);
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
