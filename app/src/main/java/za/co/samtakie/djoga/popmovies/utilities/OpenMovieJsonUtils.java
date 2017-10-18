package za.co.samtakie.djoga.popmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import za.co.samtakie.djoga.popmovies.data.MovieListContract;

/**
 * Created by CPT on 8/12/2017.
 * Load the Json return file in a ContentValues array
 */

@SuppressWarnings("UnusedParameters")
public class OpenMovieJsonUtils {

    private static final String MOVIE_RESULTS = "results";
    private static final String MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String MOVIE_IMG_THUMBNAIL = "poster_path";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_BACKDROP = "backdrop_path";
    private static final String MOVIE_STATUS_CODE = "status_code";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_ID = "id";

    public static ContentValues[] getSimpleMovieStringFromJson(Context context, String movieJsonStr) throws JSONException{


        int errorCode;
        JSONObject movieJson = new JSONObject(movieJsonStr);

        if(movieJson.has(MOVIE_STATUS_CODE)){
            errorCode = movieJson.getInt(MOVIE_STATUS_CODE);

            switch (errorCode){
                case 7:
                    // Api key is invalid
                    return null;

                case 34:
                    // The resource you requested could not be found.
                    return null;

                default:
                    // Some issue with the server
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

        ContentValues[] movieContentValues = new ContentValues[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++){
            ContentValues movieValues = new ContentValues();

            String originalTitle;
            String posterPath;
            String releaseDate;
            String overview;
            String backdropPath;
            double rating;
            int movieID;


            JSONObject movieResults = movieArray.getJSONObject(i);

            originalTitle = movieResults.getString(MOVIE_ORIGINAL_TITLE);
            posterPath = movieResults.getString(MOVIE_IMG_THUMBNAIL);
            releaseDate = movieResults.getString(MOVIE_RELEASE_DATE);
            overview = movieResults.getString(MOVIE_OVERVIEW);
            backdropPath = movieResults.getString(MOVIE_BACKDROP);
            rating = movieResults.getDouble(MOVIE_VOTE_AVERAGE);
            movieID = movieResults.getInt(MOVIE_ID);

            movieValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,releaseDate);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_RATING, rating);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIEID, movieID);

            //parsedMovieData.add(i, movieDetails);
            movieContentValues[i] = movieValues;

            //parsedMovieData.add(movieDetails);


        }

        return movieContentValues;
    }

}