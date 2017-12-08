package volunteers.un.unitednationsvolunteers.Models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;
    public  String profileUrl;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text, String profile_url) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.profileUrl = profile_url;
    }

}
// [END comment_class]
