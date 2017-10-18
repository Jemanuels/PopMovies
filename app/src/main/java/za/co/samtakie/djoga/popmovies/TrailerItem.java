package za.co.samtakie.djoga.popmovies;

/*
 * Created by CPT on 01/10/2017.
 * The TrailerItem hold all the data of the trailer
 */


import android.os.Parcel;
import android.os.Parcelable;
//
public class TrailerItem implements Parcelable{

    private String id; // var id to hold the id of the trailer
    private String key; // var key to hold the trailer key for using in youtube
    private String name; // var name hold the name of the trailer
    private String site; // var site hold the site where the trailer will be played in this case Youtube
    private String type; // var type hold the type of the trailer: Trailer, Clip, Teasers ect..
    private int size; // var size hold the value of the size of the video


    private TrailerItem(Parcel in){

        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        type = in.readString();
        size  = in.readInt();
    }

    /***
     *
     * @param id the id of the trailer
     * @param key the trailer code for playback with youtube
     * @param name the name of the trailer
     * @param site the site where it will be played from
     * @param size the movie size HD etc...
     * @param type of the trailer, Trailer Clip or Teaser
     */
    public TrailerItem(String id, String key, String name, String site, int size, String type){

        setID(id);
        setKey(key);
        setName(name);
        setSite(site);
        setSize(size);
        setType(type);
    }
    public TrailerItem(){}

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }
    public void setType(String type){ this.type = type; }


    @SuppressWarnings("unchecked")
    public static final Creator CREATOR = new Creator() {

        public TrailerItem createFromParcel(Parcel in) {

            return new TrailerItem(in);

        }

        public TrailerItem[] newArray(int size) {

            return new TrailerItem[size];

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
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeString(type);
        dest.writeInt(size);
    }
}