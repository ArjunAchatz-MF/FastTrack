package android.example.com.popularmovies.Model;

import java.util.ArrayList;

/**
 * Created by arjunachatz on 2017-02-28.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class Reviews {

    public ArrayList<Review> mReviews;

    public Reviews(){
        mReviews = new ArrayList<>();
    }

    public void addReview(Review review) {
        mReviews.add(review);
    }
}
