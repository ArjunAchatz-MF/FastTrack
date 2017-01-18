package android.example.com.popularmovies.UI;

import android.content.Intent;
import android.example.com.popularmovies.Controller.NetworkUtils;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        String title = intent.getStringExtra(Movie.TITLE);
        String plot = intent.getStringExtra(Movie.PLOT);
        String releaseDate = intent.getStringExtra(Movie.RELEASE_DATE);
        String posterPath = intent.getStringExtra(Movie.POSTER_PATH);
        String avgRating = intent.getDoubleExtra(Movie.AVG_RATING, 0) + "";
        avgRating = avgRating.length() == 1 ? avgRating : avgRating.substring(0,3);

        ImageView poster = (ImageView)findViewById(R.id.detailsPosterImageView);
        Glide.with(this).load(NetworkUtils.MOVIE_POSTER_URL + posterPath).crossFade().into(poster);

        TextView titleTV = (TextView)findViewById(R.id.detailsMovieTitle);
        titleTV.setText(title);

        TextView plotTV = (TextView)findViewById(R.id.detailsPlotSynopsys);
        plotTV.setText(plot);

        TextView releaseDateTV = (TextView)findViewById(R.id.detailsYear);
        releaseDateTV.setText(releaseDate.substring(0,4));

        TextView avgRatingTV = (TextView)findViewById(R.id.detailsAvgRating);
        avgRatingTV.setText(avgRating + "/10");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
