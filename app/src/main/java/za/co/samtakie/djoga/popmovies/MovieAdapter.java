package za.co.samtakie.djoga.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


/**
 * Created by CPT on 8/12/2017.
 * Main Activity for loading the image thumbnails
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context contextMain;
    private final MovieAdapterOnClickHandler mMovieClickHandler;
    private static final String TAG = "MainActivity";

    /**
     * An on-click handler that we're defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    public interface MovieAdapterOnClickHandler{
        //void onClick(Cursor movieForDay, int moviePosition, View view);
        void onClick(int moviePosition, View view);
    }

    // Declare a private Cursor field called mCursor
    private Cursor mCursor;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, Context context){
        this.contextMain = context;
        mMovieClickHandler = clickHandler;
        Log.d(TAG, "Item has been clicked");

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mMovieImageView;
        public MovieAdapterViewHolder(View view){

            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_movie);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);
            int mMovieID = mCursor.getInt(MainActivity.INDEX_COLUMN_MOVIEID);
            mMovieClickHandler.onClick(mMovieID, view);
        }

    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context  = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String movieForTheDay = mCursor.getString(MainActivity.INDEX_COLUMN_POSTER_PATH);
        String url = "http://image.tmdb.org/t/p/w185" + movieForTheDay;
        Picasso.with(contextMain).setLoggingEnabled(true);
        Picasso.with(contextMain).load(url).error(R.drawable.error_img).placeholder(R.drawable.image_background).into(holder.mMovieImageView);
        ViewCompat.setTransitionName(holder.mMovieImageView, mCursor.getString(MainActivity.INDEX_COLUMN_ORIGINAL_TITLE));

    }

    @Override
    public int getItemCount() {

        if(mCursor == null){

            return 0;
        } else {

            return mCursor.getCount();
        }
    }

    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}