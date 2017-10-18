package za.co.samtakie.djoga.popmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by CPT on 10/14/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MoviePreferences {

    /*
    Set the most popular and top rated movies data which will be use to query for the movie data
     */
    private static final String PREF_SORT_ORDER = "sort_by";
    private static final String TAG = "MoviePreferences.class";
    /**
     * @param context used to access SharedPreferences
     * @return an string containing the selected sort order
     */
    public static String getSortOrder(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // Sort by orders are: Popular, Top Rated and Favorite from the settings activity
        String preferredSortOrder;
        preferredSortOrder = sp.getString(PREF_SORT_ORDER, "0");
        Log.d(TAG, "preferred " + preferredSortOrder);
        return preferredSortOrder;
    }



    /**
     * Returns true if the sort order value is available.
     * @param context used to get the SharedPreferences
     * @return true if sort order is saved in SharedPreferences
     */
    public static boolean isSortOrderAvailable(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean spContainPopular = sp.contains(PREF_SORT_ORDER);
        boolean spContainBothPopularAndTopRated = false;
        if (spContainPopular) {
            spContainBothPopularAndTopRated = true;
        }
        return spContainBothPopularAndTopRated;
    }
}