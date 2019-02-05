package za.co.samtakie.djoga.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by CPT on 10/7/2017.
 * This contract class defines constants that help applications work with the content URIs,
 * column names, intent actions, and other features of the MovieContentProvider.
 */

@SuppressWarnings("WeakerAccess")
public class MovieListContract {
    // The authority, which is how the code knows which Content Provider to access
    public static final String AUTHORITY = "za.co.samtakie.djoga.popmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movielist" directory
    public static final String PATH_MOVIELIST = "movielist";

    // This is the path for the "movielist" directory
    public static final String PATH_FAVORITE = "favorite";

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static final class MovieListEntry implements BaseColumns{

        // MovieListEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIELIST).build();

        // MovieListEntry content URI = base content URI + path
        public static final Uri CONTENT_URI_FAV = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME_FAV = "favorite"; // database for saving the fav movies
        public static final String TABLE_NAME = "movielist"; // database for saving the most popular and top rated
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_RATING =  "rating";
        public static final String COLUMN_MOVIEID = "movieid";




        /**
         * Builds a URI that adds the movie id to the end of the movie content URI path.
         * This is used to query details about a single movie entry by id. This is what we
         * use for the detail view query.
         *
         * @param movieID This is the id of the movie in the Database
         * @return Uri to query details about a single movie entry
         */
        public static Uri buildMovieItemUri(int movieID) {
            return CONTENT_URI.buildUpon()

                    .appendPath(Integer.toString(movieID))
                    .build();
        }

        /**
         * Builds a URI that adds the movie id to the end of the movie content URI path.
         * This is used to query details about a single movie entry by id. This is what we
         * use for the detail view query.
         *
         * @param movieID This is the id of the movie in the Database
         * @return Uri to query details about a single single movie from the Favorite database
         */
        public static Uri buildFavorite(int movieID) {
            return CONTENT_URI_FAV.buildUpon()
                    .appendPath(Integer.toString(movieID))
                    .build();
        }

        /**
         * Builds a URI that adds return all movies in the database
         * @return Uri to query details about a  movie from the movielist database
         */
        public static Uri buildForAllMovies() {
            return CONTENT_URI.buildUpon()
                    .build();
        }

        /**
         * Builds a URI that adds return all movies in the database from the favorite table
         * @return Uri to query details about all  movie from the favorite movie table
         */
        public static Uri buildForAllFav() {
            return CONTENT_URI_FAV.buildUpon()
                    .build();
        }

    }
}