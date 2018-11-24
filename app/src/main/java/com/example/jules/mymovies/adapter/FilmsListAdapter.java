package com.example.jules.mymovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.activity.FilmDetailsActivity;
import com.example.jules.mymovies.listener.OnLoadMoreListener;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.util.AppConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class FilmsListAdapter extends Adapter<RecyclerView.ViewHolder> {

    /**
     * The list of films to handle in this adapter
     */
    private ArrayList<Film> mFilms;

    /**
     * The context of the adapter
     */
    private Context mContext;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private RecyclerView mRecyclerView;

    /**
     * The date format to display for films release date.
     * Format is "dayNumber month year.
     * Example: "17 novembre 2018"
     */
    public static final String RELEASE_DATE_FORMAT = "dd MMMM yyyy";

    /**
     * View type for Film item.
     */
    private static final int VIEW_TYPE_FILM = 0;

    /**
     * View type for loading item.
     */
    private static final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    public static final String TAG = "FilmsListAdapter";


    public FilmsListAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mFilms = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public FilmsListAdapter(Context context, ArrayList<Film> films) {
        mContext = context;
        mFilms = films;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder itemViewHolder;
        if (viewType == VIEW_TYPE_FILM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.film_list_item, parent, false);
            itemViewHolder = new FilmViewHolder(view);
        } else { // VIEW_TYPE_LOADING
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_list_item, parent, false);
            itemViewHolder = new LoadingViewHolder(view);
        }

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilmViewHolder) {
            FilmViewHolder filmViewHolder = (FilmViewHolder) holder;
            Film film = mFilms.get(position);

            // Format release date
            SimpleDateFormat releaseDateFormat = new SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.FRENCH);
            String releaseDateFormatted = releaseDateFormat.format(film.getReleaseDate());

            // Set view data
            filmViewHolder.title.setText(film.getTitle());
            filmViewHolder.releaseDate.setText(releaseDateFormatted);
            String fullPosterUrl = AppConstants.TMDB_POSTER_BASE_URL + film.getPosterUrl();
            Picasso.get().load(fullPosterUrl).into(filmViewHolder.poster);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return (mFilms.get(position) == null) ? VIEW_TYPE_LOADING : VIEW_TYPE_FILM;
    }

    @Override
    public int getItemCount() {
        return mFilms.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * Adds new films to the list of data,
     * keeping the value as null for the LoadingView.
     * @param filmsToAdd films to add
     */
    public void addFilms(ArrayList<Film> filmsToAdd) {
        // Delete the null for Loading View
        mFilms.removeAll(Collections.singleton(null));

        // Add the new films
        mFilms.addAll(filmsToAdd);

        // Append a null for Loading View
        mFilms.add(null);
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

            itemView.setOnClickListener(this);
        }


        /**
         * Called when user clicks on film item.
         * WIll launch activity with detailed film info (to be coming)
         */
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Clicked film " + mFilms.get(getAdapterPosition()).getTitle());

            // Retrieve the film selected
            int positionClicked = getAdapterPosition();
            Film filmClicked = mFilms.get(positionClicked);

            Intent intentFilmDetails = new Intent(mContext, FilmDetailsActivity.class);

            // Transform the film object so that we can pass
            // it through intent
            String jsonFilm = new Gson().toJson(filmClicked);
            intentFilmDetails.putExtra(FilmDetailsActivity.EXTRA_FILM_JSON, jsonFilm);

            // Start activity to display details of the film
            mContext.startActivity(intentFilmDetails);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loading_list_item_progress_bar);
        }


    }
}
