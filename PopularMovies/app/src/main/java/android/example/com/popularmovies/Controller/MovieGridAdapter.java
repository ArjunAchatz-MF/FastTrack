package android.example.com.popularmovies.Controller;

import android.content.Context;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;
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
    }

    @Override
    public int getItemCount() {
        Log.v("MOVIE LIST SIZE", mMovies.mMoviesList.size() + "");
        return mMovies.mMoviesList.size();
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

            Context context = mThumbnail.getContext();

            loadView(movie);

//            setOnClicks();

            Log.v("BINDING MOVIE", movie.mPosterPath);

        }

        private void loadView(final Movie movie) {

            String posterLocalPath = mContext.getFilesDir() + "/Posters" + movie.mPosterPath;
            File posterFile = new File(posterLocalPath);

            File posterDir = new File(mContext.getFilesDir() + "/Posters");
            if (!posterDir.exists()){
                posterDir.mkdir();
            }

            if(posterFile.exists()){

                Glide.with(mContext)
                        .load(posterLocalPath)
                        .crossFade()
                        .into(mThumbnail);

                Log.v("IMAGE LOADED", "LOCALLY");

            } else {

                //Build image request
                String moviePosterURL = MOVIE_POSTER_URL + movie.mPosterPath;

                ViewTarget viewTarget = new ViewTarget<ImageView, GlideBitmapDrawable>(mThumbnail) {

                    @Override
                    public void onResourceReady(GlideBitmapDrawable resource, GlideAnimation<? super GlideBitmapDrawable> glideAnimation) {
                       //Get bitmap
                        Bitmap posterBitmap = resource.getBitmap();

                        //Set the image
                        mThumbnail.setImageBitmap(posterBitmap);

                        //Lets save this file out
                        try {
                            File poster = new File(mContext.getFilesDir() + "/Posters" + movie.mPosterPath);
                            if(!poster.exists()) {
                                FileOutputStream fileOutputStream = new FileOutputStream(poster);
                                posterBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
                            }
                            Log.v("POSTER SAVED", "DONE");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.v("POSTER SAVED", "FAILED");
                        }
                    }
                };

                //Glide it in baby
                Glide.with(mContext)
                        .load(moviePosterURL)
                        .crossFade()
                        .into(viewTarget);

                Log.v("IMAGE LOADED", "FROM REMOTE");

                //update temp
                movie.mLocalPosterPath = mContext.getFilesDir() + "/Posters" + movie.mPosterPath;
                mMovies.mMoviesList.set(getAdapterPosition(), movie);

            }

        }

//        private void setOnClicks() {
//
//            mContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(
//                            mContext,
//                            mMovies.mMoviesList.get(getAdapterPosition()).mID + "",
//                            Toast.LENGTH_SHORT
//                    ).show();
//                }
//            });
//
//        }

    }

}
