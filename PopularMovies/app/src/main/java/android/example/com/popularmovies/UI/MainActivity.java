package android.example.com.popularmovies.UI;

import android.content.Context;
import android.example.com.popularmovies.Controller.MovieGridAdapter;
import android.example.com.popularmovies.Controller.NetworkUtils;
import android.example.com.popularmovies.Model.Movies;
import android.example.com.popularmovies.R;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int SHOWING_POPULAR_MOVIES = 0;
    public static final int SHOWING_TOP_RATED_MOVIES = 1;
    public static final int SHOWING_FAVOURITE_MOVIES = 2;
    private int mCurrentSelection = SHOWING_POPULAR_MOVIES;

    private static final int NUM_OF_COLUMNS = 2;
    RecyclerView mMoviesRecyclerView;
    MovieGridAdapter mMovieGridAdapter;
    private Movies mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeGallery();

        new GetPopularMoviesTask(this).execute();

    }

    private void initializeGallery() {
        mMoviesRecyclerView = (RecyclerView)findViewById(R.id.moviesRecyclerView);

        mMovies = new Movies();

        mMovieGridAdapter = new MovieGridAdapter(mMovies);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);

        mMoviesRecyclerView.setAdapter(mMovieGridAdapter);

        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                Toast.makeText(this, "Favs", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sort_by_most_popular:
                Toast.makeText(this, "Most popular", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_POPULAR_MOVIES){
                    new GetPopularMoviesTask(this).execute();
                    mCurrentSelection = SHOWING_POPULAR_MOVIES;
                }
                break;

            case R.id.sort_by_top_rated:
                Toast.makeText(this, "Top rated", Toast.LENGTH_SHORT).show();
                if(mCurrentSelection != SHOWING_TOP_RATED_MOVIES){
                    new GetTopRatedMoviesTask(this).execute();
                    mCurrentSelection = SHOWING_TOP_RATED_MOVIES;
                }
                break;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public class GetPopularMoviesTask extends AsyncTask<Void, Void, String>{

        Context mContext;

        public GetPopularMoviesTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("STARTING API REQUEST", System.currentTimeMillis() + "");
        }

        @Override
        protected String doInBackground(Void... voids) {
            mMovies = NetworkUtils.getPopularMovies(mContext);
            Log.v("Number of movies", mMovies.mMoviesList.size() + "");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("DONE API REQUEST", System.currentTimeMillis() + "");
            mMovieGridAdapter = new MovieGridAdapter(mMovies);
            mMoviesRecyclerView.setAdapter(mMovieGridAdapter);
        }
    }

    public class GetTopRatedMoviesTask extends AsyncTask<Void, Void, String>{

        Context mContext;

        public GetTopRatedMoviesTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("STARTING API 2 REQUEST", System.currentTimeMillis() + "");
        }

        @Override
        protected String doInBackground(Void... voids) {
            mMovies = NetworkUtils.getTopRatedMovies(mContext);
            Log.v("Number of movies", mMovies.mMoviesList.size() + "");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("DONE API 2 REQUEST", System.currentTimeMillis() + "");
            mMovieGridAdapter = new MovieGridAdapter(mMovies);
            mMoviesRecyclerView.setAdapter(mMovieGridAdapter);
        }
    }

}
