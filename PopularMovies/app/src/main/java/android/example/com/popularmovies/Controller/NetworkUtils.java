package android.example.com.popularmovies.Controller;

import android.content.Context;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by arjunachatz on 2017-01-07.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class NetworkUtils {

    public static final String API_POINTER = "https://api.themoviedb.org/3";
    public static final String POPULAR_MOVIE_REQUEST = "/movie/popular";
    public static final String TOP_RATED_MOVIE_REQUEST = "/movie/top_rated";
    public static final String TRAILERS_REQUEST = "/movie/top_rated";
    public static final String USER_COMMENTS_REQUEST = "/movie/top_rated";
    public static final String API_KEY_QUERY_PARAMETER_KEY = "?api_key=";
    public static final String MOVIE_POSTER_URL = "https://image.tmdb.org/t/p/w185";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Movies getPopularMovies(Context context){

        Movies movies = new Movies();

        String apiKey = context.getString(R.string.the_movieDB_v3_auth_api_key);

        if(!apiKey.isEmpty()) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(API_POINTER + POPULAR_MOVIE_REQUEST + API_KEY_QUERY_PARAMETER_KEY + apiKey)
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                if(response.isSuccessful()){

                    //Response is a json array full of movies waiting to be parsed
                    String jsonResponse = response.body().string();

                    //Loop through the list and parse
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonMoviesArray = jsonObject.getJSONArray("results");

                    for (int index = 0; index < jsonMoviesArray.length(); index++) {
                        Movie movie = JSONResponseParser.getMovie(jsonMoviesArray.getJSONObject(index));
                        if(movie.mID != -1){
                            movies.addMovie(movie);
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return movies;
    }

    public static Movies getTopRatedMovies(Context context){
        Movies movies = new Movies();

        String apiKey = context.getString(R.string.the_movieDB_v3_auth_api_key);

        if(!apiKey.isEmpty()) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(API_POINTER + TOP_RATED_MOVIE_REQUEST + API_KEY_QUERY_PARAMETER_KEY + apiKey)
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                if(response.isSuccessful()){

                    //Response is a json array full of movies waiting to be parsed
                    String jsonResponse = response.body().string();

                    //Loop through the list and parse
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonMoviesArray = jsonObject.getJSONArray("results");

                    for (int index = 0; index < jsonMoviesArray.length(); index++) {
                        Movie movie = JSONResponseParser.getMovie(jsonMoviesArray.getJSONObject(index));
                        if(movie.mID != -1){
                            movies.addMovie(movie);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return movies;
    }

}
