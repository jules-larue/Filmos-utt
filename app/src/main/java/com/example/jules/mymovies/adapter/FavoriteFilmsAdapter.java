package com.example.jules.mymovies.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.activity.FilmDetailsActivity;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class FavoriteFilmsAdapter extends RecyclerView.Adapter<FavoriteFilmsAdapter.FavoriteFilmViewHolder> {

    /**
     * The activity that uses this adapter.
     */
    private Activity mFavoriteFilmsActivity;

    /**
     * The list of favorite films stored
     * in the local application database.
     */
    private List<Film> mFavoriteFilms;

    /**
     * Date format to display the release date
     * of the film.
     * Currently, we only display the year.
     */
    public static final String RELEASE_DATE_FORMAT = "yyyy";

    public FavoriteFilmsAdapter(Activity parentActivity, List<Film> favoriteFilms) {
        mFavoriteFilmsActivity = parentActivity;
        mFavoriteFilms = favoriteFilms;
    }

    @NonNull
    @Override
    public FavoriteFilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favorite_film_item, parent, false);
        return new FavoriteFilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFilmViewHolder holder, int position) {
        Film filmClicked = mFavoriteFilms.get(position);

        // Set poster
        String posterUrl = AppConstants.TMDB_POSTER_BASE_URL + filmClicked.getPosterUrl();
        Picasso.get().load(posterUrl).into(holder.poster);

        // Set other data
        holder.title.setText(filmClicked.getTitle());

        DateFormat releaseDateFormat = new SimpleDateFormat(RELEASE_DATE_FORMAT);
        String formattedReleaseDate = null;
        try {
            formattedReleaseDate = releaseDateFormat.format(filmClicked.getReleaseDateAsDate());
            holder.releaseDate.setText(formattedReleaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mFavoriteFilms.size();
    }


    class FavoriteFilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView poster;
        private TextView title;
        private TextView releaseDate;

        public FavoriteFilmViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.favorite_film_item_poster);
            title = itemView.findViewById(R.id.favorite_film_item_title);
            releaseDate = itemView.findViewById(R.id.favorite_film_item_release_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*
            Show the activity to display
            the film details.
             */
            Intent filmDetailsIntent = new Intent(mFavoriteFilmsActivity, FilmDetailsActivity.class);

            Film filmClicked = mFavoriteFilms.get(getAdapterPosition());
            String jsonFilm = new Gson().toJson(filmClicked);
            filmDetailsIntent.putExtra(
                    FilmDetailsActivity.EXTRA_FILM_JSON,
                    jsonFilm
            );

            // Film from this adapter arr all in favorites local database
            filmDetailsIntent.putExtra(
                    FilmDetailsActivity.EXTRA_IS_IN_FAVORITES,
                    true
            );

            // Start activity
            mFavoriteFilmsActivity.startActivity(filmDetailsIntent);
        }
    }
}