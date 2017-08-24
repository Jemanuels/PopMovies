package za.co.samtakie.djoga.popmovies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import za.co.samtakie.djoga.popmovies.MovieItem;

/**
 * Created by CPT on 8/12/2017.
 * Load the Json return file in a MovieItem Array
 */

public class OpenMovieJsonUtils {

    public static ArrayList<MovieItem> getSimpleMovieStringFromJson(Context context, String movieJsonStr) throws JSONException{

        final String MOVIE_RESULTS = "results";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_IMG_THUMBNAIL = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_BACKDROP = "backdrop_path";
        final String MOVIE_STATUS_CODE = "status_code";
        final String MOVIE_VOTE_AVERAGE = "vote_average";


        ArrayList<MovieItem> parsedMovieData;

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
        parsedMovieData = new ArrayList<>();

        for(int i = 0; i < movieArray.length(); i++){
            MovieItem movieDetails = new MovieItem();



            String originalTitle;
            String posterPath;
            String releaseDate;
            String overview;
            String backdropPath;
            double rating;


            JSONObject movieResults = movieArray.getJSONObject(i);

            originalTitle = movieResults.getString(MOVIE_ORIGINAL_TITLE);
            posterPath = movieResults.getString(MOVIE_IMG_THUMBNAIL);
            releaseDate = movieResults.getString(MOVIE_RELEASE_DATE);
            overview = movieResults.getString(MOVIE_OVERVIEW);
            backdropPath = movieResults.getString(MOVIE_BACKDROP);
            rating = movieResults.getDouble(MOVIE_VOTE_AVERAGE);

            movieDetails.setReleaseDate(releaseDate);
            movieDetails.setPosterPath(posterPath);
            movieDetails.setOverview(overview);
            movieDetails.setOriginalTitle(originalTitle);
            movieDetails.setBackdropPath(backdropPath);
            movieDetails.setRating(rating);

            parsedMovieData.add(i, movieDetails);

            //parsedMovieData.add(movieDetails);


        }

        return parsedMovieData;
    }


/*
    public static ArrayList<MovieItem> getSimpleMovieStringFromJson(Context context, String movieJsonStr) throws JSONException{

        final String MOVIE_RESULTS = "results";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_IMG_THUMBNAIL = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_BACKDROP = "backdrop_path";
        final String MOVIE_STATUS_CODE = "status_code";


        ArrayList<MovieItem> parsedMovieData = null;

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
        parsedMovieData = new ArrayList<MovieItem>();

        for(int i = 0; i < movieArray.length(); i++){
            MovieItem movieDetails = new MovieItem();



            String originalTitle;
            String posterPath;
            String releaseDate;
            String overview;
            String backdropPath;


            JSONObject movieResults = movieArray.getJSONObject(i);

            originalTitle = movieResults.getString(MOVIE_ORIGINAL_TITLE);
            posterPath = movieResults.getString(MOVIE_IMG_THUMBNAIL);
            releaseDate = movieResults.getString(MOVIE_RELEASE_DATE);
            overview = movieResults.getString(MOVIE_OVERVIEW);
            backdropPath = movieResults.getString(MOVIE_BACKDROP);

            movieDetails.setReleaseDate(releaseDate);
            movieDetails.setPosterPath(posterPath);
            movieDetails.setOverview(overview);
            movieDetails.setOriginalTitle(originalTitle);
            movieDetails.setBackdropPath(backdropPath);











            parsedMovieData.add(movieDetails);


        }

        return parsedMovieData;
    }
    */
}