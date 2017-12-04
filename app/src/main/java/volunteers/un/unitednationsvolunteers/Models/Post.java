package volunteers.un.unitednationsvolunteers.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public String websiteTitle;
    public String  websiteDescription;
    public String  imgurl;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String websiteTitle,String  websiteDescription,String  imgurl,String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.websiteTitle =websiteTitle;
        this.websiteDescription=websiteDescription;
        this.imgurl =imgurl;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("websiteTitle", websiteTitle);
        result.put("websiteDescription", websiteDescription);
        result.put("imgurl", imgurl);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
