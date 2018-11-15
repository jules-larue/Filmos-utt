package com.example.jules.mymovies.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.jules.mymovies.R;

import java.util.Objects;


/**
 * A fragment displayed in the
 * {@link com.example.jules.mymovies.activity.MainActivity}
 * that displays the list of most popular movies, and
 * allows to search movies by name.
 */
public class PopularFilmsFragment extends Fragment {

    /**
     * The list of the popular movies that we display.
     */
    private RecyclerView mFilmsList;

    /**
     * The search bar allowing to perform searches.
     */
    private FloatingSearchView mSearchBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_films, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFilmsList = Objects.requireNonNull(getView()).findViewById(R.id.popular_films_list);
        mSearchBar = getView().findViewById(R.id.search_bar);
    }

}
