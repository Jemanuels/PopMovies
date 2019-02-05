package za.co.samtakie.djoga.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CPT on 01/10/2017.
 */

@SuppressWarnings({"DefaultFileTemplate", "UnusedParameters"})
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {


    private final TrailerAdapterOnClickHandler mTrailerClickHandler;
    private ArrayList<TrailerItem> trailerItem;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler, Context context){

        mTrailerClickHandler = clickHandler;

    }

    public ArrayList<TrailerItem> getTrailerItem() {
        return new ArrayList<TrailerItem>(trailerItem);
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context  = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder holder, int position) {

        String trailerForTheDay = trailerItem.get(position).getName();

        holder.mTrailerTextView.setText(trailerForTheDay);

    }

    @Override
    public int getItemCount() {

        if(trailerItem == null){

            return 0;
        } else {

            return trailerItem.size();
        }
    }

    public void setTrailerData(ArrayList<TrailerItem> trailerData){

        trailerItem = trailerData;
        notifyDataSetChanged();

    }

    /**
     * An on-click handler that we're defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    public interface TrailerAdapterOnClickHandler {
        @SuppressWarnings("UnusedParameters")
        void onClick(ArrayList<TrailerItem> trailerForDay, int trailerPosition, View view);
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrailerTextView;

        public TrailerAdapterViewHolder(View view) {

            super(view);
            mTrailerTextView = (TextView) view.findViewById(R.id.tv_trailer);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            ArrayList<TrailerItem> trailerForDay = trailerItem;
            mTrailerClickHandler.onClick(trailerForDay, adapterPosition, view);

        }

    }

}