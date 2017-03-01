package android.example.com.popularmovies.Model;

import java.util.ArrayList;

/**
 * Created by arjunachatz on 2017-01-07.
 * Copyright © 2016 Matter and Form. All rights reserved.
 */

public class Videos {
    public ArrayList<Video> mVideos;

    public Videos(){
        mVideos = new ArrayList<>();
    }

    public void addVideo(Video video){
        mVideos.add(video);
    }
}
