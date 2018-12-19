package com.example.jules.mymovies.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    /**
     * Checks in the application preferences if the user
     * has given his consent to collect data.
     */
    public static boolean checkUserConsent(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_NAME,
                        Context.MODE_PRIVATE);

        return preferences.getBoolean(AppConstants.PREFERENCES_USER_CONSENT,
                false);
    }
}
