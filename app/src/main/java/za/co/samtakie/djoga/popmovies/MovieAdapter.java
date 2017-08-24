package za.co.samtakie.djoga.popmovies;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by CPT on 8/12/2017.
 * Main Activity for loading the image thumbnails
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context contextMain;
    private ArrayList<MovieItem> movieItem;
    private final MovieAdapterOnClickHandler mMovieClickHandler;

    /**
     * An on-click handler that we're defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    public interface MovieAdapterOnClickHandler{
        void onClick(ArrayList<MovieItem> movieForDay, int moviePosition, View view);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, Context context){
        this.contextMain = context;
        mMovieClickHandler = clickHandler;

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
            ArrayList<MovieItem> movieForDay = movieItem;
            mMovieClickHandler.onClick(movieForDay, adapterPosition, view);

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

        String movieForTheDay = movieItem.get(position).getPosterPath();
        String url = "http://image.tmdb.org/t/p/w185" + movieForTheDay;
        Picasso.with(contextMain).setLoggingEnabled(true);
        Picasso.with(contextMain).load(url).error(R.drawable.error_img).placeholder(R.drawable.image_background).into(holder.mMovieImageView);
        ViewCompat.setTransitionName(holder.mMovieImageView, movieItem.get(position).getOriginalTitle());

    }

    @Override
    public int getItemCount() {

        if(movieItem == null){

            return 0;
        } else {

            return movieItem.size();
        }
    }



    public void setMovieData(ArrayList<MovieItem> movieData){

        movieItem = movieData;
        notifyDataSetChanged();

    }

}