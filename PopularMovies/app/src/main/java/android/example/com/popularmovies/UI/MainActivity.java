package android.example.com.popularmovies.UI;

import android.content.Context;
import android.database.Cursor;
import android.example.com.popularmovies.Controller.FavouritesContentProvider;
import android.example.com.popularmovies.Controller.MovieGridAdapter;
import android.example.com.popularmovies.Controller.NetworkUtils;
import android.example.com.popularmovies.Model.Movie;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Movies> {

    public static final int SHOWING_POPULAR_MOVIES = 0;
    public static final int SHOWING_TOP_RATED_MOVIES = 1;
    public static final int SHOWING_FAVOURITE_MOVIES = 2;
    private static final int GET_MOVIE_TASK = 1232830;
    private static final String CURRENT_SELECTION = "CURRENT_SELECTION";
    private volatile int mCurrentSelection = SHOWING_POPULAR_MOVIES;

    private static final int NUM_OF_COLUMNS = 2;
    RecyclerView mMoviesRecyclerView;
    MovieGridAdapter mMovieGridAdapter;
    private volatile Movies mMovies;
    private ProgressBar mIndeterminateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the gallery set up
        initializeGallery();

        //Get access to loading view
        mIndeterminateProgress = (ProgressBar)findViewById(R.id.indeterminateProgress);

        //Set rotation state change
        if(savedInstanceState != null){
            mCurrentSelection = savedInstanceState.getInt(CURRENT_SELECTION);
        } else {
            mCurrentSelection = SHOWING_POPULAR_MOVIES;
        }



        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<Object> loader = loaderManager.getLoader(GET_MOVIE_TASK);
//        if(loader != null) {
            loaderManager.restartLoader(GET_MOVIE_TASK, null, this);
//        } else {
//            loaderManager.restartLoader(GET_MOVIE_TASK, null, this);
//        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SELECTION, mCurrentSelection);
        super.onSaveInstanceState(outState);
    }

    private void initializeGallery() {
        mMoviesRecyclerView = (RecyclerView)findViewById(R.id.moviesRecyclerView);
        mMoviesRecyclerView.setHasFixedSize(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        mMovies = new Movies();
        mMovieGridAdapter = new MovieGridAdapter(mMovies);

        mMoviesRecyclerView.setAdapter(mMovieGridAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.show_favourites:
                Toast.makeText(this, "Your favourites", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_FAVOURITE_MOVIES){
                    mCurrentSelection = SHOWING_FAVOURITE_MOVIES;
                    new GetMoviesTask(this).execute();
                }
                break;

            case R.id.sort_by_most_popular:
                Toast.makeText(this, "Most popular", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_POPULAR_MOVIES){
                    mCurrentSelection = SHOWING_POPULAR_MOVIES;
                    new GetMoviesTask(this).execute();
                }
                break;

            case R.id.sort_by_top_rated:
                Toast.makeText(this, "Top rated", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_TOP_RATED_MOVIES){
                    mCurrentSelection = SHOWING_TOP_RATED_MOVIES;
                    new GetMoviesTask(this).execute();
                }
                break;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public  android.support.v4.content.AsyncTaskLoader<Movies> onCreateLoader(int id, Bundle args) {
        return new  android.support.v4.content.AsyncTaskLoader<Movies>(this){
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                //Force a load
                forceLoad();
            }

            @Override
            public Movies loadInBackground() {
                Log.v("LOADING", "BACKGROUND");
                Movies movies = new Movies();

                switch (mCurrentSelection){
                    case SHOWING_FAVOURITE_MOVIES:
                        Uri moviesURI = FavouritesContentProvider.CONTENT_URI;
                        Cursor c = managedQuery(moviesURI, null, null, null, null);
                        if (c.moveToFirst()) {
                            do{

                                Movie favouritedMovie = new Movie();

                                favouritedMovie.mID = Integer.parseInt(
                                        c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_ID)));

                                favouritedMovie.mAverageRating =
                                        c.getDouble(c.getColumnIndex(FavouritesContentProvider.MOVIE_AVG_RATING));

                                favouritedMovie.mPosterPath =
                                        c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_POSTER_PATH));

                                favouritedMovie.mPlotSynopsys =
                                        c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_PLOT));

                                favouritedMovie.mReleaseDate =
                                        c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_RELEASE_DATE));

                                favouritedMovie.mTitle =
                                        c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_TITLE));

                                movies.addMovie(favouritedMovie);

                            } while (c.moveToNext());

                            if(c != null && !c.isClosed()){
                                c.close();
                            }
                        }
                        break;
                    case SHOWING_POPULAR_MOVIES:
                        movies = NetworkUtils.getPopularMovies(MainActivity.this);
                        break;
                    case SHOWING_TOP_RATED_MOVIES:
                        movies = NetworkUtils.getTopRatedMovies(MainActivity.this);
                        break;

                    default:
                }
                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Movies> loader, Movies data) {

        //Check for errors
        if(data == null){
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            Log.v("LOADING", "FAILED");
            return;
        }

        //Set our member variable
        mMovies = data;

        //Show movie view
        mMoviesRecyclerView.setVisibility(View.VISIBLE);

        //Update recycler-view adapter
        mMovieGridAdapter.updateDataSet(mMovies);

        //Hide progress view
        mIndeterminateProgress.setVisibility(View.GONE);

        Toast.makeText(MainActivity.this, "Movies : " + data.mMoviesList.size(), Toast.LENGTH_SHORT).show();
        Log.v("LOADING", "SUCCESS");
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Movies> loader) {

    }

    public class GetMoviesTask extends AsyncTask<Void, Void, Void>{

        Context mContext;

        public GetMoviesTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMoviesRecyclerView.setVisibility(View.GONE);
            mIndeterminateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mCurrentSelection) {
                case SHOWING_TOP_RATED_MOVIES :
                    mMovies = NetworkUtils.getTopRatedMovies(mContext);
                    break;
                case SHOWING_POPULAR_MOVIES:
                    mMovies = NetworkUtils.getPopularMovies(mContext);
                    break;
                case SHOWING_FAVOURITE_MOVIES:
                    Uri movies = FavouritesContentProvider.CONTENT_URI;
                    Cursor c = managedQuery(movies, null, null, null, null);
                    mMovies = new Movies();
                    if (c.moveToFirst()) {
                        do{

                            Movie favouritedMovie = new Movie();

                            favouritedMovie.mID = Integer.parseInt(
                                    c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_ID)));

                            favouritedMovie.mAverageRating =
                                    c.getDouble(c.getColumnIndex(FavouritesContentProvider.MOVIE_AVG_RATING));

                            favouritedMovie.mPosterPath =
                                    c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_POSTER_PATH));

                            favouritedMovie.mPlotSynopsys =
                                    c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_PLOT));

                            favouritedMovie.mReleaseDate =
                                    c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_RELEASE_DATE));

                            favouritedMovie.mTitle =
                                    c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_TITLE));

                            mMovies.addMovie(favouritedMovie);

                        } while (c.moveToNext());
                    }

                    break;
                default:
                    mMovies = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            mMoviesRecyclerView.setVisibility(View.VISIBLE);
            if(mMovies == null){
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                mMovieGridAdapter.updateDataSet(mMovies);
            }
            mIndeterminateProgress.setVisibility(View.GONE);
        }
    }


}
