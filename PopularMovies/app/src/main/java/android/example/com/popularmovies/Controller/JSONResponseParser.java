package android.example.com.popularmovies.Controller;

import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.MovieTrailers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arjunachatz on 2017-01-08.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class JSONResponseParser {

    public JSONResponseParser(){

    }

    public static Movie getMovie(JSONObject jsonObject){

        Movie movie = new Movie();

        try {

            movie.mID = jsonObject.getInt("id");
            movie.mTitle = jsonObject.getString("title");
            movie.mPlotSynopsys = jsonObject.getString("overview");
            movie.mReleaseDate = jsonObject.getString("release_date");
            movie.mPopularity = jsonObject.getDouble("popularity");
            movie.mAverageRating = jsonObject.getDouble("vote_average");
            movie.mPosterPath = jsonObject.getString("poster_path");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public static MovieTrailers getMovieTrailers(String jsonString){

        return null;
    }
}
