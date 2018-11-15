package com.example.jules.mymovies.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.fragment.FavoriteFilmsFragment;
import com.example.jules.mymovies.fragment.PopularFilmsFragment;

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

    }

}