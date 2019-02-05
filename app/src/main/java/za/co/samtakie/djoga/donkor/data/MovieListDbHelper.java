package za.co.samtakie.djoga.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CPT on 10/7/2017.
 */

@SuppressWarnings("ALL")
public class MovieListDbHelper extends SQLiteOpenHelper {

    // set the database name
    private static final String DATABASE_NAME = "movielist.db";

    // set the database version number, always update the version number if you change
    // the database scheme
    private static final int DATABASE_VERSION = 4;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MovieListContract.MovieListEntry.TABLE_NAME + " (" +
            MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY," +
            MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
            MovieListContract.MovieListEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
            MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_RATING + " REAL," +
            MovieListContract.MovieListEntry.COLUMN_MOVIEID + " REAL NOT NULL)";

    private static final String SQL_CREATE_FAV = "CREATE TABLE IF NOT EXISTS " + MovieListContract.MovieListEntry.TABLE_NAME_FAV + " (" +
            MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY," +
            MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
            MovieListContract.MovieListEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
            MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH + " TEXT," +
            MovieListContract.MovieListEntry.COLUMN_RATING + " REAL," +
            MovieListContract.MovieListEntry.COLUMN_MOVIEID + " REAL NOT NULL UNIQUE)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +
            MovieListContract.MovieListEntry.TABLE_NAME;



    public MovieListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_FAV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        //For Table TABLE_NAME_FAV we will be using Alter instead of DROP TABLE
        //
        onCreate(db);
    }
}
