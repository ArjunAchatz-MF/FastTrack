package android.example.com.popularmovies.Controller;

import android.content.Context;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static android.example.com.popularmovies.Controller.NetworkUtils.*;

/**
 * Created by arjunachatz on 2017-01-07.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieGridElementViewHolder> {

    private Movies mMovies;

    public MovieGridAdapter(Movies movies){
        mMovies = movies;
    }

    @Override
    public MovieGridElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View viewLayout = LayoutInflater.from(context).inflate(R.layout.gallery_grid_element, parent, false);
        return new MovieGridElementViewHolder(viewLayout, context);
    }

    @Override
    public void onBindViewHolder(MovieGridElementViewHolder holder, int position) {
        holder.onBind(mMovies.mMoviesList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.mMoviesList.size();
    }

    public class MovieGridElementViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private RelativeLayout mContainer;
        private ImageView mThumbnail;
        private Drawable mStarOn, mStarOff, mCurrentDrawable;

        public MovieGridElementViewHolder(View itemView, Context context) {
            super(itemView);

            mContainer = (RelativeLayout)itemView.findViewById(R.id.galleryElementContainer);
            mThumbnail = (ImageView)itemView.findViewById(R.id.movieThumbnailImageView);
            mContext = context;

            mStarOn = context.getResources().getDrawable(android.R.drawable.star_big_on);
            mStarOff = context.getResources().getDrawable(android.R.drawable.star_big_off);
        }

        public void onBind(Movie movie){

            Context context = mThumbnail.getContext();

            loadView(movie);

            setOnClicks();

        }

        private void loadView(Movie movie) {
            if(!movie.mLocalPosterPath.isEmpty()){

            } else if(!movie.mPosterPath.isEmpty()) {

                //Build image request
                String moviePosterURL = MOVIE_POSTER_URL + movie.mPosterPath;

                //Glide it in baby
                Glide.with(mContext)
                        .load(moviePosterURL)
                        .crossFade()
                        .into(mThumbnail);

            } else {

            }

        }

        private void setOnClicks() {

            final Movie selectedMovie = mMovies.mMoviesList.get(getAdapterPosition());

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(
                            mContext,
                            mMovies.mMoviesList.get(getAdapterPosition()).mID,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        }

    }

}
