package com.example.jules.mymovies.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.jules.mymovies.R;

public class SnackbarUtils {

    /**
     * The unique Snackbar instance to display in the app.
     */
    private static Snackbar mSnackbar;

    private static Snackbar makeSnackbar(View container, int resId) {
        return Snackbar.make(container,
                resId,
                Snackbar.LENGTH_SHORT);
    }

    public static void snackbarFavoriteFilmAdded(View container) {
        // Make the Snackbar
        mSnackbar = makeSnackbar(container, R.string.snackbar_text_film_add_to_favorites);

        // Show the Snackbar
        showSnackbar();
    }

    public static void snackbarFavoriteFilmDeleted(View container) {
        // Make the Snackbar
        mSnackbar = makeSnackbar(container, R.string.snackbar_text_film_deleted_from_favorites);

        // Show the Snackbar
        showSnackbar();
    }

    private static void showSnackbar() {
        // Hide the Snackbar if it visible
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }

        mSnackbar.show();
    }
}
