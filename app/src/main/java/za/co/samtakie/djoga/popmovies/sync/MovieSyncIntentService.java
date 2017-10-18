package za.co.samtakie.djoga.popmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by CPT on 10/14/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService(){
        super("MovieSyncIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieSyncTask.syncMovie(this);
    }
}
