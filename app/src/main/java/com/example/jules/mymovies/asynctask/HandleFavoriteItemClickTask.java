package com.example.jules.mymovies.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.jules.mymovies.model.DaoSession;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.model.FilmDao;
import com.example.jules.mymovies.util.FilmsDatabase;
import com.example.jules.mymovies.util.SnackbarUtils;

public abstract class HandleFavoriteItemClickTask extends AsyncTask<Object, Void, Boolean> {

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
     * The film we will add to database
     * or delete from it.
     */
    private Film mFilmToAddToFavorite;

    private Activity mParentActivity;

    public HandleFavoriteItemClickTask(MenuItem favoriteMenuItem, Film filmToAddToFavorite, Activity parentActivity) {
        mFavoriteMenuItem = favoriteMenuItem;
        mFilmToAddToFavorite = filmToAddToFavorite;
        mParentActivity = parentActivity;
    }

    public HandleFavoriteItemClickTask(ImageButton favoriteButton, Film filmToAddToFavorite, Activity parentActivity) {
        mFavoriteButton = favoriteButton;
        mFilmToAddToFavorite = filmToAddToFavorite;
        mParentActivity = parentActivity;
    }

    @Override
    protected Boolean doInBackground(Object[] args) {
        DaoSession daoSession = FilmsDatabase.getDaoSession(mParentActivity);
        FilmDao filmDao = daoSession.getFilmDao();

        Film queryResult = filmDao.load(mFilmToAddToFavorite.getId());

        boolean isFilmSaved = queryResult != null;

        // The root view of activity to show the Snackbar
        View mainView = ((ViewGroup) mParentActivity.getWindow().getDecorView()
                .findViewById(android.R.id.content)).getChildAt(0);

        if (isFilmSaved) {
            // Film already saved, delete it from database.
            filmDao.deleteByKey(queryResult.getId());

            // Show Snackbar
            SnackbarUtils.snackbarFavoriteFilmDeleted(mainView);
        } else {
            // Film not in database, insert it.
            filmDao.insert(mFilmToAddToFavorite);

            // Show Snackbar
            SnackbarUtils.snackbarFavoriteFilmAdded(mainView);
        }

        return !isFilmSaved;
    }

    @Override
    protected void onPostExecute(Boolean isFilmSaved) {
        int newIconId = getFavoriteIconIdToDisplay(isFilmSaved);
        if (mFavoriteButton != null) {
            mFavoriteButton.setImageResource(newIconId);
        } else {
            mFavoriteMenuItem.setIcon(newIconId);
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

