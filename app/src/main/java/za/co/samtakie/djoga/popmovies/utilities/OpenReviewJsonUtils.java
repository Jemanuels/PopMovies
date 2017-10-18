package za.co.samtakie.djoga.popmovies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import za.co.samtakie.djoga.popmovies.ReviewItem;

/**
 * Created by CPT on 8/12/2017.
 * Load the Json return file in a ReviewItem Array
 */

@SuppressWarnings("UnusedParameters")
public class OpenReviewJsonUtils {

    public static ArrayList<ReviewItem> getSimpleReviewStringFromJson(Context context, String reviewJsonStr) throws JSONException{

        final String REVIEW_RESULTS = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_STATUS_CODE = "status_code";


        ArrayList<ReviewItem> parsedReviewData;

        int errorCode;
        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        if(reviewJson.has(REVIEW_STATUS_CODE)){
            errorCode = reviewJson.getInt(REVIEW_STATUS_CODE);

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

        JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);
        parsedReviewData = new ArrayList<>();

        for(int i = 0; i < reviewArray.length(); i++){
            ReviewItem reviewDetails = new ReviewItem();

            String id;
            String author;
            String content;


            JSONObject reviewResults = reviewArray.getJSONObject(i);

            id = reviewResults.getString(REVIEW_ID);
            author = reviewResults.getString(REVIEW_AUTHOR);
            content = reviewResults.getString(REVIEW_CONTENT);

            reviewDetails.setID(id);
            reviewDetails.setAuthor(author);
            reviewDetails.setContent(content);

            parsedReviewData.add(i, reviewDetails);

        }

        return parsedReviewData;
    }
}