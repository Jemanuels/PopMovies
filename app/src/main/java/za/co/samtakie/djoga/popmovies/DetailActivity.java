package za.co.samtakie.djoga.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.samtakie.djoga.popmovies.data.MovieListContract;
import za.co.samtakie.djoga.popmovies.utilities.NetworkUtils;
import za.co.samtakie.djoga.popmovies.utilities.OpenReviewJsonUtils;
import za.co.samtakie.djoga.popmovies.utilities.OpenTrailerJsonUtils;

import static za.co.samtakie.djoga.popmovies.R.id.poster;


@SuppressWarnings("ALL")
public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{


    public static final String HEADER_ID = "detail_id";
    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * movie data.
     */
    public static final String[] MAIN_DETAIL_PROJECTION = {
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
    private static final String BASE_URL_TMDB = "http://image.tmdb.org/t/p/";
    private static final int ID_DETAIL_LOADER = 37;

    //  two static variable,
    public static int scrollX;
    public static int scrollY;

    @BindView(R.id.title) TextView tvTitle;
    @BindView(R.id.release_date) TextView tvReleaseDate;
    @BindView(R.id.synopsis)TextView tvOverView;
    @BindView(R.id.rating) TextView tvContainer;
    @BindView(poster) ImageView ivPoster;
    @BindView(R.id.scrollMain)
    ScrollView mScrollView;

    // RecyclerView to view the trailers of the movie.
    @BindView(R.id.rv_trailer)
    RecyclerView mRecyclerView;
    // RecyclerView to view the review of the movie.
    @BindView(R.id.rv_review)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.fav_message)
    CoordinatorLayout mCoordinatorLayout;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    Parcelable mListState;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private String originalTitle;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private String backdropPath;
    private double rating;
    private int movieID;
    private int scrollPositionTrailer;
    private int scrollPositionReview;
    private boolean mLoaderStarted;
    // The URI that is used to access the chosen day's weather details
    private Uri mUri;
    // this variable will hold the image url for saving this in the saveinstance bundle
    private String imageUrl;
    private LinearLayoutManager reviewLayoutManager;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ButterKnife.bind(this);

        layoutManager = new GridLayoutManager(DetailActivity.this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        reviewLayoutManager = new GridLayoutManager(DetailActivity.this, 1);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setNestedScrollingEnabled(false);

        mTrailerAdapter = new TrailerAdapter(this, this);
        mReviewAdapter = new ReviewAdapter(this);

        mRecyclerView.setAdapter(mTrailerAdapter);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loader_detail);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message_detail);


        mUri = getIntent().getData();
        if(mUri == null){
            throw new NullPointerException("URI for DetailActivity cannot be null");
        }

        final FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton delFabButton = (FloatingActionButton) findViewById(R.id.del_fab);
        delFabButton.hide();
        Bundle bd = getIntent().getExtras();
        String favorite;
        favorite = bd.getString("favorite");
        if(favorite != null){

            if(favorite.equals("fav")){
                fabButton.hide();
                delFabButton.show();
            } else {
                fabButton.show();
                delFabButton.hide();
            }

        }

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new movie will be added to
         * the favorite table
         */
        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String[] mProjection = { MovieListContract.MovieListEntry.COLUMN_MOVIEID };
                String mSelectionClause = MovieListContract.MovieListEntry.COLUMN_MOVIEID + " = ?";
                String[] selectionArgs = {""};
                selectionArgs[0] = String.valueOf(movieID);

                // Insert movie to favorite table via a ContentResolver
                // Create new empty ContentValues object
                ContentValues contentValues = new ContentValues();
                // Put the movie details into the ContentValues
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,releaseDate);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH, posterPath);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW, overview);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH, backdropPath);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_RATING, rating);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIEID, movieID);

                Cursor c = getContentResolver().query(
                        MovieListContract.MovieListEntry.CONTENT_URI_FAV,
                        mProjection,
                        mSelectionClause,
                        selectionArgs,
                        null);
                // Check and make sure the movie doesn't exits in the fav table
                // If it is ignore adding the data and inform the user.
                assert c != null;
                if(c.getCount() == 0) {

                    getContentResolver().insert(MovieListContract.MovieListEntry.CONTENT_URI_FAV, contentValues);
                    //Toast.makeText(getBaseContext(), originalTitle + " has been added to your favorite", Toast.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, originalTitle + " has been added to your favorite", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    fabButton.hide();
                    delFabButton.show();
                }else {
                    //Toast.makeText(getBaseContext(), originalTitle + " is already in your favorite", Toast.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, originalTitle + " is already in your favorite", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    delFabButton.show();
                    fabButton.hide();

                }
            }
        });

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new movie will be deleted from
         * the favorite table
         */
        delFabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String[] mProjection = {MovieListContract.MovieListEntry.COLUMN_MOVIEID};
                String mSelectionClause = MovieListContract.MovieListEntry.COLUMN_MOVIEID + " = ?";
                String[] selectionArgs = {""};
                selectionArgs[0] = String.valueOf(movieID);

                // Insert movie to favorite table via a ContentResolver
                // Create new empty ContentValues object
                ContentValues contentValues = new ContentValues();
                // Put the movie details into the ContentValues
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE, releaseDate);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH, posterPath);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW, overview);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_BACKDROP_PATH, backdropPath);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_RATING, rating);
                contentValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIEID, movieID);

                Cursor c = getContentResolver().query(
                        MovieListContract.MovieListEntry.CONTENT_URI_FAV,
                        mProjection,
                        mSelectionClause,
                        selectionArgs,
                        null);
                // Check and make sure the movie  exits in the fav table
                // If it is delete the data and inform the user
                assert c != null;
                if (c.getCount() != 0) {

                    getContentResolver().delete(MovieListContract.MovieListEntry.CONTENT_URI_FAV, mSelectionClause, selectionArgs);
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, originalTitle + " has been removed from your favorite", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    fabButton.show();
                    delFabButton.hide();
                } else {

                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, originalTitle + " is already removed from your favorite", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    delFabButton.hide();
                    fabButton.show();

                }
            }
        });


        if (savedInstanceState != null) {

            loadThumbnail(savedInstanceState.getString("imageUrl"));
            mLoaderStarted = savedInstanceState.getBoolean("True");
            mRecyclerView.scrollToPosition(scrollPositionTrailer);
            mReviewRecyclerView.scrollToPosition(scrollPositionReview);
            final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
            if (position != null)
                scrollX = position[0];
            scrollY = position[1];
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);


                }
            });

        }
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);


    }

    //save value on onSaveInstanceState
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray("SCROLL_POSITION",
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});

        // Save list state
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("mListState", mListState);
        // putting recyclerview items
        outState.putParcelableArrayList("mTrailerAdapter", mTrailerAdapter.getTrailerItem());
        super.onSaveInstanceState(outState);

        scrollPositionTrailer = layoutManager.findFirstCompletelyVisibleItemPosition();
        scrollPositionReview = reviewLayoutManager.findFirstCompletelyVisibleItemPosition();

        outState.putInt("scrollPositionTrailer", scrollPositionTrailer);
        outState.putInt("scrollPositionReview", scrollPositionReview);

    }

    //Restore them on onRestoreInstanceState
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollPositionTrailer = savedInstanceState.getInt("scrollPositionTrailer");
        scrollPositionReview = savedInstanceState.getInt("scrollPositionReview");

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable("mListState");
            mReviewRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
            // make sure that you capture the image url to orientation changed
            imageUrl = savedInstanceState.getString("imageUrl");
        }

        final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
        scrollY = position[1];
        scrollX = position[0];
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);

                }
            });
    }

    private void loadTrailerData(String pathType){

        //showMovieDataView();
        new FetchTrailerTask().execute(pathType);
        new FetchReviewTask().execute(pathType);

        mScrollView.scrollTo(0, 500);

    }

    private void loadImage(ImageView imgView, String url) {
        Picasso.with(imgView.getContext())
                .load(url)

                .placeholder(R.drawable.image_background)
                .into(imgView);
    }

    private void loadThumbnail(String url) {
        loadImage(ivPoster, url);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(ArrayList<TrailerItem> trailerForDay, int trailerPosition, View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=" + trailerForDay.get(trailerPosition)
                        .getKey()));

        // Verify that the intent will resolve to an activity
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        } else {
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, "No app has been installed to view the trailer, please check with your administrator", Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {
        switch(loaderId){
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MAIN_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if(data != null && data.moveToFirst()){
            cursorHasValidData = true;
        }

        if(!cursorHasValidData){
            return;
        }

        // Title // tvTitle.setText(movieDetails.get(0).getOriginalTitle());
        String movieTitle = data.getString(INDEX_COLUMN_ORIGINAL_TITLE);
        tvTitle.setText(movieTitle);
        originalTitle = movieTitle;

        //The movie thumbnail //
        String moviePath = data.getString(INDEX_COLUMN_POSTER_PATH);
        String posterW185 = "w185";

        ivPoster.setTag(BASE_URL_TMDB + posterW185 + moviePath);
        imageUrl = (String) ivPoster.getTag();
        loadThumbnail(BASE_URL_TMDB + posterW185 + moviePath);
        posterPath = moviePath;

        //The movie rating//
        int movieRating = data.getInt(INDEX_COLUMN_RATING);
        tvContainer.setText(String.valueOf(movieRating) + "/10");
        rating = movieRating;

        // MovieID
        String mMovieId = String.valueOf(data.getInt(INDEX_COLUMN_MOVIEID));
        loadTrailerData(mMovieId);
        movieID = data.getInt(INDEX_COLUMN_MOVIEID);

        // Movie overview //
        String movieOverview = data.getString(INDEX_COLUMN_OVERVIEW);
        tvOverView.setText(movieOverview);
        overview = movieOverview;

        // Movie Release date //
        String movieReleaseDate = data.getString(INDEX_COLUMN_RELEASE_DATE);
        tvReleaseDate.setText(movieReleaseDate);
        releaseDate = movieReleaseDate;
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showTrailerErrorMessage() {

        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showReviewErrorMessage() {

        mErrorMessage.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);


    }


    //update & save their value on onPause & onResume.
    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }


//this is important. scrollTo doesn't work in main thread.
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(scrollX, scrollY);
            }
        });
    }

    @Override
    protected void onRestart() {

        getSupportLoaderManager().restartLoader(ID_DETAIL_LOADER, null, this);

        super.onRestart();

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(scrollX, scrollY);
            }
        });
    }

    private class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<TrailerItem>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);


        }

        @Override
        protected ArrayList<TrailerItem> doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }
            String pathName = params[0];
            URL trailerRequestUrl = NetworkUtils.buildUrl(pathName, "trailer");
            try{
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);

                @SuppressWarnings("UnnecessaryLocalVariable")
                ArrayList<TrailerItem> simpleTrailerData = OpenTrailerJsonUtils.getSimpleTrailerStringFromJson(DetailActivity.this, jsonTrailerResponse);
                return simpleTrailerData;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<TrailerItem> trailerData) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(trailerData != null) {

                mTrailerAdapter.setTrailerData(trailerData);


            } else {


                showTrailerErrorMessage();

            }
        }
    }

    private class FetchReviewTask extends AsyncTask<String, Void, ArrayList<ReviewItem>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);


        }

        @Override
        protected ArrayList<ReviewItem> doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }
            String pathName = params[0];
            URL trailerRequestUrl = NetworkUtils.buildUrl(pathName, "reviews");
            try{
                String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);

                @SuppressWarnings("UnnecessaryLocalVariable")
                ArrayList<ReviewItem> simpleReviewData = OpenReviewJsonUtils.getSimpleReviewStringFromJson(DetailActivity.this, jsonReviewResponse);
                return simpleReviewData;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<ReviewItem> reviewData) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(reviewData != null) {

                mReviewAdapter.setReviewData(reviewData);


            } else {


                showReviewErrorMessage();

            }
        }


    }


}