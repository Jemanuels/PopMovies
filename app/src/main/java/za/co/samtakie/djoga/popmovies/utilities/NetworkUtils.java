package za.co.samtakie.djoga.popmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import za.co.samtakie.djoga.popmovies.data.MoviePreferences;


/**
 * Created by CPT on 8/12/2017.
 * NetworkUtils to get the data from the website
 */

@SuppressWarnings({"SameParameterValue", "UnnecessaryLocalVariable"})
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_MOVIE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String apiKey = za.co.samtakie.djoga.popmovies.BuildConfig.API_KEY;

    private final static String API_KEY_PARAM = "api_key";

    /**
     *
     * @param pathParams path that will be used to request data from the movie database
     * @return the url path
     */
    public static URL buildUrl(String pathParams, String pathType) {

        String moviePathName; // var moviePathName will hold the data for the path

        Uri builtUri;

        // check and make sure the requested URL is for a trailer
        // if the request is for a trailer add the additional /videos path
        // else the request is for a movie link
        switch (pathType) {
            case "trailer":
                moviePathName = pathParams;
                //Log.d(TAG, "The full trailer path value is:" + moviePathName);
                builtUri = Uri.parse(DYNAMIC_MOVIE_URL).buildUpon()
                        .appendPath(moviePathName)
                        .appendPath("videos")
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            case "movie":
                moviePathName = pathParams;
                //Log.d(TAG, "The movie full path is:" + moviePathName);
                builtUri = Uri.parse(DYNAMIC_MOVIE_URL).buildUpon()
                        .appendPath(moviePathName)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
            default:
                moviePathName = pathParams;
                Log.d(TAG, "The full reviews path value is:" + moviePathName);
                builtUri = Uri.parse(DYNAMIC_MOVIE_URL).buildUpon()
                        .appendPath(moviePathName)
                        .appendPath("reviews")
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                break;
        }

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "The full path is:" + url);
        return url;
    }

    public static URL getUrl(Context context, String pathType){
        if(MoviePreferences.isSortOrderAvailable(context)){
            String preferredSortOrder = MoviePreferences.getSortOrder(context);
            String sortBy = preferredSortOrder;

            if(Integer.parseInt(sortBy) == 0){
                return buildUrl("popular", pathType);
            } else if(Integer.parseInt(sortBy) == 1){
                return buildUrl("top_rated", pathType);
            }

        }else{
            return buildUrl("popular", pathType);
        }
        return null;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                String returnScanner = scanner.next();

                return returnScanner;

            } else {
                return null;

            }
        } finally {
            urlConnection.disconnect();
        }
    }
}