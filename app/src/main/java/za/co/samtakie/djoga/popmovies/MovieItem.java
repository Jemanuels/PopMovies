package za.co.samtakie.djoga.popmovies;

/*
 * Created by CPT on 8/13/2017.
 * The MovieItem Class hold all the properties of the Movie.
 */


import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements Parcelable{

    private String originalTitle;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private String backdropPath;
    private double rating;

    private MovieItem(Parcel in){

        originalTitle = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        backdropPath = in.readString();
        rating  = in.readDouble();

    }

    /***
     *
     * @param originalTitle the title of the movie
     * @param posterPath the poster image of the movie
     * @param releaseDate the release date of the movie
     * @param overview the overview of the movie
     * @param backdropPath this can serve as a background poster
     */
    public MovieItem( String originalTitle, String posterPath, String releaseDate, String overview, String backdropPath, double rating){

        setBackdropPath(backdropPath);
        setOriginalTitle(originalTitle);
        setOverview(overview);
        setPosterPath(posterPath);
        setReleaseDate(releaseDate);
        setRating(rating);

    }
    public MovieItem(){}

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @SuppressWarnings("unchecked")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public MovieItem createFromParcel(Parcel in) {

            return new MovieItem(in);

        }

        public MovieItem[] newArray(int size) {

            return new MovieItem[size];

        }
    };

    /***
     *
     * @return return a number for the object.
     */
    @Override
    public int describeContents() {
        return this.hashCode();
    }

    /***
     *
     * @param dest write the data back to the given destination
     * @param flag the flag hold the position of where the data is be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flag) {

        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeString(backdropPath);
        dest.writeDouble(rating);

    }
}