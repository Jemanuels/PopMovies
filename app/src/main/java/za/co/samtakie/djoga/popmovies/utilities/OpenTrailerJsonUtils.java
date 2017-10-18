package za.co.samtakie.djoga.popmovies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import za.co.samtakie.djoga.popmovies.TrailerItem;

/**
 * Created by CPT on 8/12/2017.
 * Load the Json return file in a TrailerItem Array
 */

@SuppressWarnings("UnusedParameters")
public class OpenTrailerJsonUtils {

    public static ArrayList<TrailerItem> getSimpleTrailerStringFromJson(Context context, String trailerJsonStr) throws JSONException{

        final String TRAILER_RESULTS = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";
        final String TRAILER_STATUS_CODE = "status_code";


        ArrayList<TrailerItem> parsedTrailerData;

        int errorCode;
        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        if(trailerJson.has(TRAILER_STATUS_CODE)){
            errorCode = trailerJson.getInt(TRAILER_STATUS_CODE);

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

        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_RESULTS);
        parsedTrailerData = new ArrayList<>();

        for(int i = 0; i < trailerArray.length(); i++){
            TrailerItem trailerDetails = new TrailerItem();



            String id;
            String name;
            String key;
            int size;
            String type;
            String site;


            JSONObject trailerResults = trailerArray.getJSONObject(i);

            id = trailerResults.getString(TRAILER_ID);
            name = trailerResults.getString(TRAILER_NAME);
            key = trailerResults.getString(TRAILER_KEY);
            site = trailerResults.getString(TRAILER_SITE);
            type = trailerResults.getString(TRAILER_TYPE);
            size = trailerResults.getInt(TRAILER_SIZE);

            trailerDetails.setID(id);
            trailerDetails.setName(name);
            trailerDetails.setKey(key);
            trailerDetails.setSite(site);
            trailerDetails.setType(type);
            trailerDetails.setSize(size);

            parsedTrailerData.add(i, trailerDetails);


        }

        return parsedTrailerData;
    }
}