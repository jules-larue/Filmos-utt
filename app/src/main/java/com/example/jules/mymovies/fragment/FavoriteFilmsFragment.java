package com.example.jules.mymovies.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jules.mymovies.R;

import java.util.Objects;


public class FavoriteFilmsFragment extends Fragment {

    /**
     * The list of user's favorite films
     */
    private RecyclerView mFavoriteFilmsList;

    /**
     * The TextView displayed when no favorite film is saved
     */
    private TextView mNoFavoriteFilmsText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_films, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFavoriteFilmsList = Objects.requireNonNull(getView()).findViewById(R.id.favorite_films_fragment_list);
        mNoFavoriteFilmsText = getView().findViewById(R.id.tv_no_favorite_movies);
    }

    /**
     * Make the list of films visible,
     * and hide the message for no films.
     */
    private void showFilms() {
        mFavoriteFilmsList.setVisibility(View.VISIBLE);
        mNoFavoriteFilmsText.setVisibility(View.GONE);
    }

    /**
     * Make the message for no films  visible,
     * and hide the list of films.
     */
    private void showNoFilmsMessage() {
        mFavoriteFilmsList.setVisibility(View.GONE);
        mNoFavoriteFilmsText.setVisibility(View.VISIBLE);
    }
}
