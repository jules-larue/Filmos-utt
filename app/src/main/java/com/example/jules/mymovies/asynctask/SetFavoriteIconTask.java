package com.example.jules.mymovies.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.jules.mymovies.model.DaoSession;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.model.FilmDao;
import com.example.jules.mymovies.util.FilmsDatabase;

public abstract class SetFavoriteIconTask extends AsyncTask<Object, Void, Boolean> {

    /**
     * Reference to the 'favorite' menu item
     * to be able to change its icon.
     */
    private MenuItem mFavoriteMenuItem;

    /**
     * Reference to the 'favorite' image
     * button.
     */
    private ImageButton mFavoriteButton;

    /**
     * The film to check whether it is
     * in favorite or not.
     */
    private Film mFavoriteFilm;

    private Context mContext;

    public SetFavoriteIconTask(MenuItem favoriteMenuItem, Film favoriteFilm, Context context) {
        mFavoriteMenuItem = favoriteMenuItem;
        mFavoriteFilm = favoriteFilm;
        mContext = context;
    }

    public SetFavoriteIconTask(ImageButton favoriteButton, Film favoriteFilm, Context context) {
        mFavoriteButton = favoriteButton;
        mFavoriteFilm = favoriteFilm;
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Object[] args) {
        DaoSession daoSession = FilmsDatabase.getDaoSession(mContext);
        FilmDao filmDao = daoSession.getFilmDao();

        return filmDao.load(mFavoriteFilm.getId()) != null;
    }

    @Override
    protected void onPostExecute(Boolean isFilmSaved) {
        int newFavoriteItemIcon = getFavoriteIconIdToDisplay(isFilmSaved);

        if (mFavoriteButton != null) {
            mFavoriteButton.setImageResource(newFavoriteItemIcon);
        } else {
            mFavoriteMenuItem.setIcon(newFavoriteItemIcon);
        }
    }

    /**
     * Returns the correct resource id for the
     * favorite icon to display, depending on
     * whether the film that we display is saved
     * in the database or not.
     * @param isFilmSaved true if the film is saved
     *                    in the database, false otherwise.
     */
    protected abstract int getFavoriteIconIdToDisplay(boolean isFilmSaved);

}

