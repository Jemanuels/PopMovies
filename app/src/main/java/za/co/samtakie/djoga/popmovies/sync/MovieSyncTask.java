package za.co.samtakie.djoga.popmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import java.net.URL;

import za.co.samtakie.djoga.popmovies.data.MovieListContract;
import za.co.samtakie.djoga.popmovies.utilities.NetworkUtils;
import za.co.samtakie.djoga.popmovies.utilities.OpenMovieJsonUtils;

/**
 * Created by CPT on 10/14/2017.
 */

@SuppressWarnings({"DefaultFileTemplate", "WeakerAccess"})
public class MovieSyncTask {

    /**
     * Performs the network request for updated movie, parses the JSON from that request, and
     * inserts the new movie information into our ContentProvider.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncMovie(Context context) {
        try{
            /*
             * The getUrl method will return the URL that we need to get the movie data JSON for the
             * movie. It will decide whether to create a URL based off the top rated or popular
             */
            URL movieRequestUrl = NetworkUtils.getUrl(context, "movie");

            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

            ContentValues[] movieValues = OpenMovieJsonUtils.getSimpleMovieStringFromJson(context, jsonMovieResponse);

            if(movieValues != null && movieValues.length != 0){
                ContentResolver movieContentResolver = context.getContentResolver();

                movieContentResolver.delete(
                        MovieListContract.MovieListEntry.CONTENT_URI,
                        null,
                        null);

                // insert our new data
                movieContentResolver.bulkInsert(
                        MovieListContract.MovieListEntry.CONTENT_URI,
                        movieValues);



            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}