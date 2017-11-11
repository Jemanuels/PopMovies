package za.co.samtakie.djoga.popmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import za.co.samtakie.djoga.popmovies.data.MovieListContract;
import za.co.samtakie.djoga.popmovies.sync.MovieSyncUtils;

import static za.co.samtakie.djoga.popmovies.R.menu.movie;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * movie data.
     */
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieListContract.MovieListEntry._ID,
            MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,
            MovieListContract.MovieListEntry.COLUMN_POSTER_PATH,
            MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,
            MovieListContract.MovieListEntry.COLUMN_OVERVIEW,
            MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH,
            MovieListContract.MovieListEntry.COLUMN_RATING,
            MovieListContract.MovieListEntry.COLUMN_MOVIEID
    };
    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_ID = 0;
    public static final int INDEX_COLUMN_ORIGINAL_TITLE = 1;
    public static final int INDEX_COLUMN_POSTER_PATH = 2;
    public static final int INDEX_COLUMN_RELEASE_DATE = 3;
    public static final int INDEX_COLUMN_OVERVIEW = 4;
    public static final int INDEX_COLUMN_BACKDROP_PATH = 5;
    public static final int INDEX_COLUMN_RATING = 6;
    public static final int INDEX_COLUMN_MOVIEID = 7;
    public static final String CHECKORDER = "checkOrder";
    private static final String LIST_STATE = "listState";
    /*
     * This ID will be used to identify the Loader responsible for loading our movie data.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     */
    private static final int ID_MOVIE_LOADER = 36;
    private static final int ID_FAV_LOADER = 38;
    // Set the name of the class for using the Log function to print data on the screen
    private final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private String sortByOrder;
    private LinearLayoutManager layoutManager;
    private Parcelable listState;
    private int scrollPosition;
    private TextView mFavErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mFavErrorMessage = (TextView) findViewById(R.id.fav_error_message);
        layoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loader);
        Log.d("on create", "activity has been created");
        String mSortOrder = sortOrder(this);

        Log.d("Sort Order ", mSortOrder);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            sortByOrder = savedInstanceState.getString(CHECKORDER);
            mRecyclerView.scrollToPosition(scrollPosition);
            Log.d("create position", String.valueOf(scrollPosition));
            //listState=savedInstanceState.getParcelable("ListState");
            if(Integer.parseInt(sortByOrder) == 2) {
                MovieListContract.MovieListEntry.buildForAllFav();
                getSupportLoaderManager().initLoader(ID_FAV_LOADER, null, this);
            } else {
                MovieListContract.MovieListEntry.buildForAllMovies();
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
            }
        } else {
            sortByOrder = sortOrder(this);
            if (Integer.parseInt(sortByOrder) == 2) {
                MovieListContract.MovieListEntry.buildForAllFav();
                getSupportLoaderManager().initLoader(ID_FAV_LOADER, null, this);
            } else {
                MovieListContract.MovieListEntry.buildForAllMovies();
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
            }
        }


        mLoadingIndicator.setVisibility(View.VISIBLE);


        MovieSyncUtils.initialize(this, MovieListContract.MovieListEntry.CONTENT_URI);



    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        sortByOrder = sortOrder(this);
        Log.d("scrollPosition ", String.valueOf(scrollPosition));
        savedInstanceState.putString(CHECKORDER, sortByOrder);
        savedInstanceState.putInt("scrollPosition", scrollPosition);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        scrollPosition = savedInstanceState.getInt("scrollPosition");
        sortByOrder = savedInstanceState.getString(CHECKORDER);
    }


    private String sortOrder(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getString(R.string.pref_sort_key), "2");
    }


    @Override
    public void onClick(int moviePosition, View view){

        Uri uriForMovieClicked;

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        if(Integer.parseInt(sortByOrder) == 2) {

            uriForMovieClicked = MovieListContract.MovieListEntry.buildFavorite(moviePosition);
            intent.putExtra("favorite", "fav");
        } else {

            uriForMovieClicked = MovieListContract.MovieListEntry.buildMovieItemUri(moviePosition);

        }
        intent.setData(uriForMovieClicked);
        intent.putExtra(DetailActivity.HEADER_ID, moviePosition);

        ActivityOptions activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(this,
                view.findViewById(R.id.iv_movie), "poster");
        startActivity(intent, activityOptionsCompat.toBundle());


    }

    private void showMovieDataView(){

        mErrorMessage.setVisibility(View.INVISIBLE);
        mFavErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) {
            return 2;
        }
        return nColumns;
    }

    private void showErrorMessage(){


        if (Integer.valueOf(sortByOrder) == 2) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mFavErrorMessage.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId){
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieListContract.MovieListEntry.CONTENT_URI;
                Log.d("POP/RATED ", "ID_MOVIE_LOADER");
                String sortOrder = MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " ASC";

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder);


            case ID_FAV_LOADER:
                Uri favQueryUri = MovieListContract.MovieListEntry.CONTENT_URI_FAV;
                Log.d("FAV ", "ID_FAV_LOADER");
                String sortFavOrder = MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " ASC";

                return new CursorLoader(this,
                        favQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortFavOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mMovieAdapter.swapCursor(data);

        if(mPosition == RecyclerView.NO_POSITION){
            mPosition = 0;
        }

        mRecyclerView.smoothScrollToPosition(mPosition);

        if(data.getCount() != 0){

            showMovieDataView();
            mLoadingIndicator.setVisibility(View.GONE);
        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mMovieAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(movie, menu);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (sortByOrder != null) {

            if (Integer.parseInt(sortByOrder) == 2) {
                MovieListContract.MovieListEntry.buildForAllFav();
                getSupportLoaderManager().initLoader(ID_FAV_LOADER, null, this);
            } else {
                MovieListContract.MovieListEntry.buildForAllMovies();
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sortByOrder != null) {
            if (Integer.parseInt(sortByOrder) == 2) {
                MovieListContract.MovieListEntry.buildForAllFav();
                getSupportLoaderManager().initLoader(ID_FAV_LOADER, null, this);
                Log.d("resume fav order ", String.valueOf(sortByOrder));
            } else {
                MovieListContract.MovieListEntry.buildForAllMovies();
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
                Log.d("resume p/r order ", String.valueOf(sortByOrder));
            }

        }


        mRecyclerView.scrollToPosition(scrollPosition);
        Log.d("resume position ", String.valueOf(scrollPosition));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRecyclerView.scrollToPosition(scrollPosition);

    }
}