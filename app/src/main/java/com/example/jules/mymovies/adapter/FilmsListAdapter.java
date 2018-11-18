package com.example.jules.mymovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FilmsListAdapter extends Adapter<FilmsListAdapter.FilmViewHolder> {

    /**
     * The list of films to handle in this adapter
     */
    private ArrayList<Film> mFilms;

    /**
     * The context of the adapter
     */
    private Context mContext;

    /**
     * The date format to display for films release date.
     * Format is "dayNumber month year.
     * Example: "17 novembre 2018"
     */
    public static final String RELEASE_DATE_FORMAT = "dd MMMM yyyy";

    /**
     * Builds a FilmListAdapter with a context and
     * an initial list of films to display.
     * @param context the context in which the adapter is run
     */
    public FilmsListAdapter(Context context, ArrayList<Film> films) {
        mContext = context;
        mFilms = films;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.film_list_item, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = mFilms.get(position);

        // Format release date
        SimpleDateFormat releaseDateFormat = new SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.FRENCH);
        String releaseDateFormatted = releaseDateFormat.format(film.getReleaseDate());

        // Set view data
        holder.title.setText(film.getTitle());
        holder.releaseDate.setText(releaseDateFormatted);
        String fullPosterUrl = AppConstants.TMDB_POSTER_BASE_URL + film.getPosterUrl();
        Picasso.get().load(fullPosterUrl).into(holder.poster);

    }

    @Override
    public int getItemCount() {
        return mFilms.size();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView poster;
        public TextView title;
        public TextView releaseDate;

        public FilmViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.film_item_poster);
            title = itemView.findViewById(R.id.film_item_title);
            releaseDate = itemView.findViewById(R.id.film_item_date);
        }


        /**
         * Called when user clicks on film item.
         * WIll launch activity with detailed film info (to be coming)
         * @param v this view
         */
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "Clicked movie " + title.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
