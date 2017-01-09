package android.example.com.popularmovies.Model;

import java.util.ArrayList;

/**
 * Created by arjunachatz on 2017-01-07.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class Movies {

    public ArrayList<Movie> mMoviesList;

    public Movies(){

        mMoviesList = new ArrayList<>();

    }

    public void getMoviesByReleaseDate(){

    }

    public void getMoviesByPopularity(){

    }

    public void addMovie(Movie movie) {
        mMoviesList.add(movie);
    }
}
