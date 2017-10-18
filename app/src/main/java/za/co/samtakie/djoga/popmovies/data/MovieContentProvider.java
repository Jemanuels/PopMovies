package za.co.samtakie.djoga.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import static android.content.ContentValues.TAG;

/**
 * Created by CPT on 10/8/2017.
 * Register the MovieContentProvider in the manifest file
 * Set name, authorities, and exported attributes
 * exported = false limits access to this ContentProvider to only this app
 */

@SuppressWarnings("ALL")
public class MovieContentProvider extends ContentProvider {

    // It's convention  to use 100, 200, 300 etc for directories
    // and related ints (101, 102, ..) for items in that directory.
    public static final int MOVIES = 100;
    public static final int FAVORITES = 200;
    public static final int MOVIES_WITH_ID = 101;
    public static final int FAV_WITH_ID = 201;

    // Declare a static variable for the Uri match that we contract
    private final static UriMatcher sUriMatcher = buildUriMatcher();

    /***
     *
     * @return the UriMatcher either a single item or a directory
     * Define a static buildUriMatcher method that associates URI's with their int match
     */
    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add matches with addUri(String authority, String path, int code)
        // directory for movies
        uriMatcher.addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_MOVIELIST, MOVIES);

        // directory for favorite movies
        uriMatcher.addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_FAVORITE, FAVORITES);

        // single item for movies. /# = represent a numerical id
        uriMatcher.addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_MOVIELIST + "/#", MOVIES_WITH_ID);

        // single item for fav movies /# = represent a numerical id
        uriMatcher.addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_FAVORITE + "/#", FAV_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a MovieListDbHelper
    private MovieListDbHelper mMovieListDbHelper;

    @Override
    public boolean onCreate() {
        // Instantiate our mMovieListDbHelper
        mMovieListDbHelper = new MovieListDbHelper(getContext());

        //Return true from onCreate to signify success performing setup
        return true;
    }


    /**
     * Handles requests to insert a set of new rows.
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMovieListDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            // Only perform our implementation of bulkInsert if the URI matches the MOVIES code
            case MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MovieListContract.MovieListEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }

    }

    /**
     * Handles query requests from clients. We will use this method in PopMovies to query for all
     * of our movie data as well as to query for the favorite movies.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://za.co.samtakie.djoga.popmovies/movielist/23
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the movie data.
             * In this case, we want to return a cursor that contains one row of movie data
             */




            case MOVIES_WITH_ID: {

                String[] selectionArguments = new String[]{uri.getLastPathSegment()};

                cursor = mMovieListDbHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        MovieListContract.MovieListEntry.TABLE_NAME,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        /*
                         * The URI that matches MOVIE_WITH_ID
                         */
                        MovieListContract.MovieListEntry.COLUMN_MOVIEID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case FAV_WITH_ID: {

                String[] selectionArguments = new String[]{uri.getLastPathSegment()};

                cursor = mMovieListDbHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        MovieListContract.MovieListEntry.TABLE_NAME_FAV,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        /*
                         * The URI that matches MOVIE_WITH_ID
                         */
                        MovieListContract.MovieListEntry.COLUMN_MOVIEID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://za.co.samtakie.djoga.popmovies/movielist/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the movie in our movielist table.
             *
             * In this case, we want to return a cursor that contains every row of movie data
             * in our movielist table.
             */
            case MOVIES: {
                cursor = mMovieListDbHelper.getReadableDatabase().query(
                        MovieListContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://za.co.samtakie.djoga.popmovies/favorite/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the movies in our favorite table.
             *
             * In this case, we want to return a cursor that contains every row of favorite movie data
             * in our movielist table.
             */
            case FAVORITES: {
                cursor = mMovieListDbHelper.getReadableDatabase().query(
                        MovieListContract.MovieListEntry.TABLE_NAME_FAV,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get access to the movie database (to write new data to)
        final SQLiteDatabase db = mMovieListDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the movies directory
        int match = sUriMatcher.match(uri);

        // Insert new values into the database
        // Set the value for the returnedUri and write the default case for the unknown URI's
        Uri returnUri;

        switch (match){
            case MOVIES:
                // Inserting values into movielist table
                long id = db.insert(MovieListContract.MovieListEntry.TABLE_NAME, null, values);
                if(id > 0){
                    // success
                    returnUri = ContentUris.withAppendedId(MovieListContract.MovieListEntry.CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            case FAVORITES:
                // Inserting values into movielist table

                try{
                    db.insertOrThrow(MovieListContract.MovieListEntry.TABLE_NAME_FAV, null, values);
                    String idMovie = values.getAsString("movieid");
                    Log.d(TAG, idMovie);
                    returnUri = null;

                }catch (android.database.sqlite.SQLiteConstraintException e) {
                    String idMovie = values.getAsString("movieid");
                    Log.d(TAG, idMovie);
                    Log.e(TAG, "SQLiteConstraintException:" + e.getMessage());
                    returnUri =  null;
                }
                catch (android.database.sqlite.SQLiteException e) {
                    Log.e(TAG, "SQLiteException:" + e.getMessage());
                    returnUri = null;
                }
                catch (Exception e) {
                    Log.e(TAG, "Exception:" + e.getMessage());
                   returnUri = null;
                }

                break;

            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed
        //getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection){
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {

            //Only implement the functionality, given the proper URI, to delete ALL rows in the movie table
            case MOVIES:
                numRowsDeleted = mMovieListDbHelper.getWritableDatabase().delete(
                        MovieListContract.MovieListEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case FAVORITES:
                numRowsDeleted = mMovieListDbHelper.getWritableDatabase().delete(
                        MovieListContract.MovieListEntry.TABLE_NAME_FAV,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}