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

@SuppressWarnings({"DefaultFileTemplate", "UnusedAssignment", "UnnecessaryLocalVariable"})
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {


    private ArrayList<ReviewItem> reviewItem;




    public ReviewAdapter(Context context){
        Context contextMain = context;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mAuthorTextView;
        public final TextView mContentTextView;
        public ReviewAdapterViewHolder(View view){

            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.tv_author);
            mContentTextView = (TextView) view.findViewById(R.id.tv_content);


        }



    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context  = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ReviewAdapterViewHolder holder, int position) {

        String reviewAuthor = reviewItem.get(position).getAuthor();
        String reviewContent = reviewItem.get(position).getContent();
        holder.mAuthorTextView.setText(reviewAuthor);
        holder.mContentTextView.setText(reviewContent);

    }

    @Override
    public int getItemCount() {

        if(reviewItem == null){

            return 0;
        } else {

            return reviewItem.size();
        }
    }



    public void setReviewData(ArrayList<ReviewItem> reviewData){

        reviewItem = reviewData;
        notifyDataSetChanged();

    }

}