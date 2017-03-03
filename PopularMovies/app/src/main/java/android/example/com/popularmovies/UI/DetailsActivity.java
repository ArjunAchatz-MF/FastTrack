package android.example.com.popularmovies.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.example.com.popularmovies.Controller.FavouritesContentProvider;
import android.example.com.popularmovies.Controller.NetworkUtils;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Review;
import android.example.com.popularmovies.Model.Reviews;
import android.example.com.popularmovies.Model.Video;
import android.example.com.popularmovies.Model.Videos;
import android.example.com.popularmovies.R;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    public static final int VIDEOS_REQUEST_CODE = 1234;
    public static final int REVIEWS_REQUEST_CODE = 4321;

    RecyclerView mReviewsRecyclerView, mVideosRecyclerView;
    VideosAdapter mVideosAdapter;
    ReviewsAdapter mReviewsAdapter;
    Reviews mReviews;
    Videos mVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get views
        mReviewsRecyclerView = (RecyclerView)findViewById(R.id.reviewsRecyclerView);
        mVideosRecyclerView = (RecyclerView)findViewById(R.id.videoRecyclerView);

        //Use intent sent over
        Intent intent = getIntent();

        //Use intent data to load views with model
        final int movieID = setViewWith(intent);

        //Set up favourite button
        setupFavouriteButton(movieID + "");

        //Set layout managers and Adapters
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager videosLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);

        mVideosAdapter = new VideosAdapter(new Videos());
        mReviewsAdapter = new ReviewsAdapter(new Reviews());
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mVideosRecyclerView.setAdapter(mVideosAdapter);

        //Set action bar back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load trailers
        getSupportLoaderManager().initLoader(VIDEOS_REQUEST_CODE, null, new LoaderManager.LoaderCallbacks<Videos>() {

            @Override
            public Loader<Videos> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Videos>(DetailsActivity.this) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Videos loadInBackground() {
                        if(movieID < 0){
                            return null;
                        }

                        return NetworkUtils.getRelatedVideos(DetailsActivity.this, movieID + "");
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Videos> loader, Videos data) {
                if(data != null) {
                    mVideosAdapter = new VideosAdapter(data);
                    mVideosRecyclerView.setAdapter(mVideosAdapter);
                    Log.v("VIDEOS SIZE", data == null ? "0" : "" + data.mVideos.size());
                }
            }

            @Override
            public void onLoaderReset(Loader<Videos> loader) {

            }
        });

        //Load reviews
        getSupportLoaderManager().initLoader(REVIEWS_REQUEST_CODE, null, new LoaderManager.LoaderCallbacks<Reviews>() {
            @Override
            public Loader<Reviews> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Reviews>(DetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Reviews loadInBackground() {
                        if(movieID < 0){
                            return null;
                        }

                        return NetworkUtils.getReviews(DetailsActivity.this, movieID + "");
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Reviews> loader, Reviews data) {
                if(data != null){
                    mReviewsAdapter = new ReviewsAdapter(data);
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                    Log.v("REVIEW SIZE", data == null ? "0" : "" + data.mReviews.size());
                }
            }

            @Override
            public void onLoaderReset(Loader<Reviews> loader) {

            }
        });

    }

    private void setupFavouriteButton(final String id) {
        Button favouriteButton = (Button)findViewById(R.id.favouriteButton);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if this is already favourite
                boolean found = false;

                Uri movies = FavouritesContentProvider.CONTENT_URI;
                Cursor c = managedQuery(movies, null, null, null, null);
                if (c.moveToFirst()) {
                    do{
                        String movieID = c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_ID));
                        if(movieID.equals(id)){
                            found = true;
                            break;
                        }
                    } while (c.moveToNext());
                }

                if(found){

                    //Remove this
                    getContentResolver().delete(FavouritesContentProvider.CONTENT_URI,
                            "movie_id=?", new String[]{id});

                    Toast.makeText(getBaseContext(), "Un-favourited", Toast.LENGTH_SHORT).show();


                } else {

                    // Add a new student record
                    ContentValues values = new ContentValues();

                    values.put(FavouritesContentProvider.MOVIE_ID, id);

                    Uri uri = getContentResolver().insert(FavouritesContentProvider.CONTENT_URI, values);

                    Toast.makeText(getBaseContext(), "Favourited", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private int setViewWith(Intent intent) {
        final int movieID = intent.getIntExtra(Movie.ID, -1);
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
        return movieID;
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

    public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

        ArrayList<Review> mReviews;

        public ReviewsAdapter(Reviews reviews){
            mReviews = reviews.mReviews;
        }

        @Override
        public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_element, parent, false);
            return new ReviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReviewViewHolder holder, int position) {
            holder.bind(mReviews.get(position));
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }

        public class ReviewViewHolder extends RecyclerView.ViewHolder {

            public TextView mAuthorTextView, mContentTextView;

            public ReviewViewHolder(View itemView) {
                super(itemView);
                mAuthorTextView = (TextView) itemView.findViewById(R.id.reviewAuthorTextView);
                mContentTextView = (TextView) itemView.findViewById(R.id.reviewContentTextView);
            }

            public void bind(Review review){
                mAuthorTextView.setText(review.mAuthor);
                mContentTextView.setText(review.mContent);
            }


        }
    }

    public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

        ArrayList<Video> mVideos;

        public VideosAdapter(Videos videos){
            mVideos = videos.mVideos;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_element, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mVideos.size();
        }

        public class VideoViewHolder extends RecyclerView.ViewHolder {

            public RelativeLayout mContainer;
            public TextView mTrailerTextView;

            public VideoViewHolder(View itemView) {
                super(itemView);
                mContainer = (RelativeLayout) itemView.findViewById(R.id.trailerContainer);
                mTrailerTextView = (TextView) itemView.findViewById(R.id.trailerTextView);
            }

            public void bind(final int position){
                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String k = mVideos.get(position).mKey;
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + k)
                        ));
                    }
                });
                mTrailerTextView.setText("Trailer " + position);
            }

        }
    }

}
