package android.example.com.popularmovies.Controller;

import android.content.Context;
import android.content.Intent;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;
import android.example.com.popularmovies.UI.DetailsActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
        Log.v("BOUND POSITION", position + "");
    }

    @Override
    public int getItemCount() {
        return mMovies.mMoviesList.size();
    }

    public void updateDataSet(Movies movies){
        mMovies = movies;
        notifyDataSetChanged();
    }

    public class MovieGridElementViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private RelativeLayout mContainer;
        private ImageView mThumbnail;

        public MovieGridElementViewHolder(View itemView, Context context) {
            super(itemView);

            mContainer = (RelativeLayout)itemView.findViewById(R.id.galleryElementContainer);
            mThumbnail = (ImageView)itemView.findViewById(R.id.movieThumbnailImageView);
            mContext = context;

        }

        public void onBind(Movie movie){

            loadView(movie);

            setOnClicks(movie);

        }

        private void loadView(Movie movie) {

            String moviePosterURL = MOVIE_POSTER_URL + movie.mPosterPath;

            //Glide it in baby
            Glide.with(mContext)
                    .load(moviePosterURL)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mThumbnail);

             Log.v("LOADING", "FROM GLIDE");

        }

        private void setOnClicks(final Movie movie) {

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra(Movie.TITLE, movie.mTitle);
                    intent.putExtra(Movie.PLOT, movie.mPlotSynopsys);
                    intent.putExtra(Movie.RELEASE_DATE, movie.mReleaseDate);
                    intent.putExtra(Movie.AVG_RATING, movie.mAverageRating);
                    intent.putExtra(Movie.POSTER_PATH, movie.mPosterPath);
                    mContext.startActivity(intent);
                }
            });

        }

    }

}
