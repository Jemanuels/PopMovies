package za.co.samtakie.djoga.popmovies;

/*
 * Created by CPT on 01/10/2017.
 * The TrailerItem hold all the data of the trailer
 */


//
@SuppressWarnings("unused")
public class ReviewItem{

    private String id; // var id to hold the id of the trailer
    private String author; // var key to hold the trailer key for using in youtube
    private String content; // var name hold the name of the trailer

    /***
     *
     * @param id the id of the trailer
     * @param author the author of the review
     * @param content the content written by the author linked to the movie

     */
    public ReviewItem(String id, String author, String content){

        setID(id);
        setAuthor(author);
        setContent(content);

    }
    public ReviewItem(){}

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

}