package android.example.com.popularmovies.UI;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.example.com.popularmovies.Controller.FavouritesContentProvider;
import android.example.com.popularmovies.Controller.MovieGridAdapter;
import android.example.com.popularmovies.Controller.NetworkUtils;
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
    private int mCurrentSelection = SHOWING_POPULAR_MOVIES;

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

        mIndeterminateProgress = (ProgressBar)findViewById(R.id.indeterminateProgress);

        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(GET_MOVIE_TASK, null, this);

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

                ArrayList<String> movieIDs = new ArrayList<>();

                Uri movies = FavouritesContentProvider.CONTENT_URI;
                Cursor c = managedQuery(movies, null, null, null, null);
                if (c.moveToFirst()) {
                    do{
                        String movieID = c.getString(c.getColumnIndex(FavouritesContentProvider.MOVIE_ID));
                        movieIDs.add(movieID);
                    } while (c.moveToNext());
                }

                new GetMoviesTask(this, movieIDs).execute();
                break;

            case R.id.sort_by_most_popular:
                Toast.makeText(this, "Most popular", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_POPULAR_MOVIES){
                    mCurrentSelection = SHOWING_POPULAR_MOVIES;
                    new GetMoviesTask(this, null).execute();
                }
                break;

            case R.id.sort_by_top_rated:
                Toast.makeText(this, "Top rated", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_TOP_RATED_MOVIES){
                    mCurrentSelection = SHOWING_TOP_RATED_MOVIES;
                    new GetMoviesTask(this, null).execute();
                }
                break;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public GetMoviesLoaderTask onCreateLoader(int id, Bundle args) {
        return new GetMoviesLoaderTask(this);
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

    public static class GetMoviesLoaderTask
            extends android.support.v4.content.AsyncTaskLoader<Movies> {

        Context mContext;

        public GetMoviesLoaderTask(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            //Force a load
            forceLoad();
        }

        @Override
        public Movies loadInBackground() {
            Log.v("LOADING", "BACKGROUND");
            return NetworkUtils.getPopularMovies(mContext);
        }
    }

    public class GetMoviesTask extends AsyncTask<Void, Void, Void>{

        Context mContext;
        ArrayList<String> movieIDs;

        public GetMoviesTask(Context context, @Nullable ArrayList<String> movieIDs){
            mContext = context;
            movieIDs = movieIDs;
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
                    mMovies = NetworkUtils.getFavouritedMovies(mContext, movieIDs);
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
