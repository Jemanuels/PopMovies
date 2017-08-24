package za.co.samtakie.djoga.popmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static za.co.samtakie.djoga.popmovies.R.id.poster;

public class Detail extends AppCompatActivity {


    private ImageView ivPoster;
    private static final String BASE_URL_TMDB = "http://image.tmdb.org/t/p/";
    public static final String HEADER_ID = "detail_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvTitle;
        TextView tvReleaseDate;
        TextView tvOverView;
        TextView tvContainer;

        String posterW185 = "w185";

        ivPoster = (ImageView) findViewById(poster);
        tvTitle = (TextView) findViewById(R.id.title);
        tvReleaseDate = (TextView) findViewById(R.id.release_date);
        tvOverView = (TextView) findViewById(R.id.synopsis);
        tvContainer = (TextView) findViewById(R.id.rating);


        Intent intent = getIntent();

        if(intent != null){

            ArrayList<MovieItem> movieDetails = intent.getParcelableArrayListExtra("movieForDay");
            tvOverView.setText(movieDetails.get(0).getOverview());
            tvTitle.setText(movieDetails.get(0).getOriginalTitle());
            tvReleaseDate.setText(movieDetails.get(0).getReleaseDate());
            tvContainer.setText(String.valueOf(movieDetails.get(0).getRating() + "/10"));
            loadThumbnail(BASE_URL_TMDB + posterW185 + movieDetails.get(0).getPosterPath());

        }


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
}