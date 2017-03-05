package android.example.com.popularmovies.Controller;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import android.example.com.popularmovies.Model.Movie;
import android.net.Uri;
import android.text.TextUtils;

public class FavouritesContentProvider extends ContentProvider {

    //Out content provider contract
    public static final String PROVIDER_NAME = "android.example.com.popularmovies.Controller.FavouritesContentProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/favourites";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    //Enum
    public static final int FAVOURITE_MOVIE = 1;
    public static final int FAVOURITE_MOVIE_ID = 2;

    //Database information
    public static final String DATABASE_NAME = "Popular_Movies_DB";
    private static final int DATABASE_VERSION = 1;

    //Table within sqlite
    public static final String FAVOURITES_TABLE_NAME = "Favourites";
    public static final String _ID = "_id";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_PLOT = "movie_plot";
    public static final String MOVIE_RELEASE_DATE = "movie_release_date";
    public static final String MOVIE_POSTER_PATH = "movie_poster_path";
    public static final String MOVIE_AVG_RATING = "movie_avg_rating";

    //Content provider
    private static HashMap<String, String> FAVOURITEMOVIE_PROJECTION_MAP;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favourites", FAVOURITE_MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, "favourites/#", FAVOURITE_MOVIE_ID);
    }

    private SQLiteDatabase db;
    private static final String CREATE_DB_TABLE =
            " CREATE TABLE " + FAVOURITES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " movie_id TEXT NOT NULL," +
                    " movie_title TEXT NOT NULL," +
                    " movie_plot TEXT NOT NULL," +
                    " movie_release_date TEXT NOT NULL," +
                    " movie_poster_path TEXT NOT NULL," +
                    " movie_avg_rating REAL NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FAVOURITES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(FAVOURITES_TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(FAVOURITES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case FAVOURITE_MOVIE:
                qb.setProjectionMap(FAVOURITEMOVIE_PROJECTION_MAP);
                break;

            case FAVOURITE_MOVIE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, null);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case FAVOURITE_MOVIE:
                count = db.delete(FAVOURITES_TABLE_NAME, selection, selectionArgs);
                break;

            case FAVOURITE_MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(FAVOURITES_TABLE_NAME, _ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case FAVOURITE_MOVIE:
                return "android.example.com.popularmovies.Controller.FavouritesContentProvider.favourites";
            case FAVOURITE_MOVIE_ID:
                return "android.example.com.popularmovies.Controller.FavouritesContentProvider.favourites";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
