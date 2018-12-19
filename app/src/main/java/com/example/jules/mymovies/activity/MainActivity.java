package com.example.jules.mymovies.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.fragment.FavoriteFilmsFragment;
import com.example.jules.mymovies.fragment.PopularFilmsFragment;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.PreferenceUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    /**
     * The currently selected fragment
     */
    private Fragment mCurrentFragment;

    /**
     * The manager used to handle fragment transactions
     */
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {

                case R.id.navigation_popular_films:
                    mCurrentFragment = new PopularFilmsFragment();
                    break;

                case R.id.navigation_favorite:
                    mCurrentFragment = new FavoriteFilmsFragment();
                    break;
            }

            // Change current fragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, mCurrentFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // By default, "popular" section is selected
        navigation.setSelectedItemId(R.id.navigation_popular_films);

        // Hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Ask user consent if necessary
        final SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCES_NAME, MODE_PRIVATE);

        boolean hasUserConsent = preferences.getBoolean(AppConstants.PREFERENCES_USER_CONSENT, false);
        if (!PreferenceUtils.checkUserConsent(this)) {
            // No user consent, we ask him if he accepts
            // that we collect data
            AlertDialog.Builder consentDialogBuilder = new AlertDialog.Builder(this);
            consentDialogBuilder.setTitle(R.string.user_consent_dialog_title);
            consentDialogBuilder.setMessage(R.string.user_consent_dialog_message);
            consentDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save user preference
                    preferences.edit()
                            .putBoolean(AppConstants.PREFERENCES_USER_CONSENT, true)
                            .apply();
                    dialog.dismiss();
                }
            });
            consentDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save user preference
                    preferences.edit()
                            .putBoolean(AppConstants.PREFERENCES_USER_CONSENT, false)
                            .apply();
                    dialog.dismiss();
                }
            });
            consentDialogBuilder.create()
                    .show();
        }

    }

}