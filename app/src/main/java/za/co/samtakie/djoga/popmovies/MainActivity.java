package za.co.samtakie.djoga.popmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.net.URL;
import java.util.ArrayList;

import za.co.samtakie.djoga.popmovies.utilities.NetworkUtils;
import za.co.samtakie.djoga.popmovies.utilities.OpenMovieJsonUtils;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loader);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortByOrder = sharedPreferences.getString("example_list", "1");


        if(Integer.parseInt(sortByOrder) == 1){
            loadMovieData("popular");
        } else if(Integer.parseInt(sortByOrder) == 0){
            loadMovieData("top_rated");
        }
    }


    private void loadMovieData(String orderType){

        showMovieDataView();
        new FetchMovieTask().execute(orderType);

    }

    @Override
    public void onClick(ArrayList<MovieItem> movieForDay, int moviePosition, View view){

        ArrayList<MovieItem> movies;

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        movies = new ArrayList<>();
        movies.add(new MovieItem(movieForDay.get(moviePosition).getOriginalTitle(),
                movieForDay.get(moviePosition).getPosterPath(),
                movieForDay.get(moviePosition).getReleaseDate(),
                movieForDay.get(moviePosition).getOverview(),
                movieForDay.get(moviePosition).getBackdropPath(),
                movieForDay.get(moviePosition).getRating()));
        intent.putParcelableArrayListExtra("movieForDay", movies);
        intent.putExtra(DetailActivity.HEADER_ID, moviePosition);
        ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(this,
                view.findViewById(R.id.iv_movie), "poster");
        startActivity(intent, activityOptionsCompat.toBundle());

    }

    private void showMovieDataView(){

        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showErrorMessage(){

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);

    }

    private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }
            String sortBy = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);
            try{
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                @SuppressWarnings("UnnecessaryLocalVariable")
                ArrayList<MovieItem> simpleMovieData = OpenMovieJsonUtils.getSimpleMovieStringFromJson(MainActivity.this, jsonMovieResponse);
                return simpleMovieData;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieData) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(movieData != null) {

                mMovieAdapter.setMovieData(movieData);
                showMovieDataView();

            } else {


                showErrorMessage();

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_settings){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}