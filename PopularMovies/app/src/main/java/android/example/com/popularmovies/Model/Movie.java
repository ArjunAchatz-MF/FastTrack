package android.example.com.popularmovies.Model;

/**
 * Created by arjunachatz on 2017-01-07.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class Movie {

    public static final String ID = "ID";
    public int mID = -1;

    public static final String TITLE = "TITLE";
    public String mTitle = "";

    public static final String PLOT = "PLOT";
    public String mPlotSynopsys = "";

    public static final String RELEASE_DATE = "RELEASE_DATE";
    public String mReleaseDate = "";

    public static final String POPULARITY = "POPULARITY";
    public double mPopularity = 0;

    public static final String AVG_RATING = "AVG_RATING";
    public double mAverageRating = 0;

    public static final String POSTER_PATH = "POSTER_PATH";
    public String mPosterPath = "";

    public static final String LOCAL_POSTER_PATH = "ID";
    public String mLocalPosterPath = "";

    public static final String FAVOURITE = "FAVOURITE";
    public boolean mFavourite = false;

    public Movie(){}

}
