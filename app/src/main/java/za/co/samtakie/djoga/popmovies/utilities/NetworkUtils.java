package za.co.samtakie.djoga.popmovies.utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by CPT on 8/12/2017.
 * NetworkUtils to ge the data from the website
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_MOVIE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String apiKey = za.co.samtakie.djoga.popmovies.BuildConfig.API_KEY;


    private final static String API_KEY_PARAM = "api_key";

    /**
     *
     * @param sortBy path that will be used to request data from the movie database
     * @return the url path
     */
    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(DYNAMIC_MOVIE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
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