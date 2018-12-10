package com.example.jules.mymovies.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jules.mymovies.R;
import com.example.jules.mymovies.activity.FilmDetailsActivity;
import com.example.jules.mymovies.asynctask.HandleFavoriteItemClickTask;
import com.example.jules.mymovies.asynctask.SetFavoriteIconTask;
import com.example.jules.mymovies.listener.OnLoadMoreListener;
import com.example.jules.mymovies.model.DaoSession;
import com.example.jules.mymovies.model.Film;
import com.example.jules.mymovies.model.FilmDao;
import com.example.jules.mymovies.util.AppConstants;
import com.example.jules.mymovies.util.FilmsDatabase;
import com.example.jules.mymovies.util.MeasuresConverter;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
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
     * The activity that uses this adapter.
     */
    private Activity mParentActivity;

    private boolean isLoading;

    private int visibleThreshold = 5;

    private int lastVisibleItem;

    private int totalItemCount;

    private RecyclerView mRecyclerView;

    /**
     * Top padding value for the first item
     * of the list (in dp).
     * Default value is 0dp.
     */
    private int mFirstItemTopPadding = 0;

    /**
     * The date format to display for films release date.
     * Format is "dayNumber month year.
     * Example: "17 novembre 2018"
     */
    public static final String RELEASE_DATE_FORMAT = "dd MMMM yyyy";

    /**
     * View type for regular Film item.
     */
    private static final int VIEW_TYPE_STANDARD_FILM = 0;

    /**
     * View type for the first item of the list
     */
    private static final int VIEW_TYPE_FIRST_FILM = 1;

    /**
     * View type for loading item.
     */
    private static final int VIEW_TYPE_LOADING = 2;

    private static final int ICON_FILM_IN_FAVORITES = R.drawable.baseline_favorite_black_24;


    private static final int ICON_FILM_NOT_IN_FAVORITES = R.drawable.baseline_favorite_border_black_24;

    private OnLoadMoreListener mOnLoadMoreListener;

    public static final String TAG = "FilmsListAdapter";


    public FilmsListAdapter(Activity parentActivity, RecyclerView recyclerView) {
        mParentActivity = parentActivity;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder itemViewHolder;

        if (viewType == VIEW_TYPE_FIRST_FILM || viewType == VIEW_TYPE_STANDARD_FILM) {
            // Film item
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.film_list_item, parent, false);

            if (viewType == VIEW_TYPE_FIRST_FILM) {
                // First film only, specific top padding
                int firstItemTopPadding = MeasuresConverter.dpToPx(mParentActivity.getResources(), mFirstItemTopPadding);
                view.setPadding(view.getPaddingLeft(),
                        firstItemTopPadding,
                        view.getPaddingRight(),
                        view.getPaddingBottom());
            }
            itemViewHolder = new FilmViewHolder(view);

        } else { // VIEW_TYPE_LOADING
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_list_item, parent, false);
            itemViewHolder = new LoadingViewHolder(view);
        }

        return itemViewHolder;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilmViewHolder) {
            FilmViewHolder filmViewHolder = (FilmViewHolder) holder;
            Film film = mFilms.get(position);

            // Format release date
            SimpleDateFormat releaseDateFormat = new SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.FRENCH);
            String releaseDateFormatted = "";
            try {
                releaseDateFormatted = releaseDateFormat.format(film.getReleaseDateAsDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Set view data
            filmViewHolder.title.setText(film.getTitle());
            filmViewHolder.releaseDate.setText(releaseDateFormatted);
            String fullPosterUrl = AppConstants.TMDB_POSTER_BASE_URL + film.getPosterUrl();
            Picasso.get().load(fullPosterUrl).into(filmViewHolder.poster);

            // Init the 'favorite' icon
            new SetFavoriteIconTask(filmViewHolder.addFavorite, film, mParentActivity) {
                @Override
                protected int getFavoriteIconIdToDisplay(boolean isFilmSaved) {
                    return isFilmSaved ?
                            ICON_FILM_IN_FAVORITES :
                            ICON_FILM_NOT_IN_FAVORITES;
                }
            }.execute(filmViewHolder.addFavorite, film, mParentActivity);

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
        if (position == 0) {
            // First item may have specific padding
            return VIEW_TYPE_FIRST_FILM;
        } else if (position == mFilms.size() - 1) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_STANDARD_FILM;
        }
    }

    @Override
    public int getItemCount() {
        return mFilms.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setFirstItemTopPadding(int dpValue) {
        mFirstItemTopPadding = dpValue;
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

    /**
     * Deletes all the films in the list of films.
     */
    public void clearAllFilms() {
        mFilms.clear();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView poster;
        public TextView title;
        public TextView releaseDate;
        private ImageButton addFavorite;

        public FilmViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.film_item_poster);
            title = itemView.findViewById(R.id.film_item_title);
            releaseDate = itemView.findViewById(R.id.film_item_date);
            addFavorite = itemView.findViewById(R.id.icon_add_to_favorite);


            itemView.setOnClickListener(this);

            /*
            Set OnClickListener for the "add to favorite" icon.

            The movie clicked is added to the favorites movies in
            local database if it is not saved in the db, and deletes
            it from the db if it already exists in it.
             */
            addFavorite.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick addFavorite");

                    Film filmClicked = mFilms.get(getAdapterPosition());
                    new HandleFavoriteItemClickTask(addFavorite, filmClicked, mParentActivity) {
                        @Override
                        protected int getFavoriteIconIdToDisplay(boolean isFilmSaved) {
                            return isFilmSaved ?
                                    ICON_FILM_IN_FAVORITES :
                                    ICON_FILM_NOT_IN_FAVORITES;
                        }
                    }.execute(addFavorite, filmClicked, mParentActivity);
                }
            });
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
            final Film filmClicked = mFilms.get(positionClicked);

            final Intent intentFilmDetails = new Intent(mParentActivity, FilmDetailsActivity.class);

            // Transform the film object so that we can pass
            // it through intent
            String jsonFilm = new Gson().toJson(filmClicked);
            intentFilmDetails.putExtra(FilmDetailsActivity.EXTRA_FILM_JSON, jsonFilm);
            intentFilmDetails.putExtra(FilmDetailsActivity.EXTRA_IS_IN_FAVORITES, false);

            // Check in database if the film is in favorite
            // We do the job in a background thread to not affect UI performance
            Thread checkFavoriteTask = new Thread(new Runnable() {
                @Override
                public void run() {
                    DaoSession daoSession = FilmsDatabase.getDaoSession(mParentActivity);
                    FilmDao filmDao = daoSession.getFilmDao();
                    Film queryResult = filmDao.load(filmClicked.getId());

                    // Film is in favorite if query result is NOT null
                    boolean isFavorite = !(queryResult == null);
                    intentFilmDetails.putExtra(FilmDetailsActivity.EXTRA_IS_IN_FAVORITES, isFavorite);
                }
            });
            checkFavoriteTask.start();
            try {
                // Wait that task is finished
                checkFavoriteTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Start activity to display details of the film
            mParentActivity.startActivity(intentFilmDetails);
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
