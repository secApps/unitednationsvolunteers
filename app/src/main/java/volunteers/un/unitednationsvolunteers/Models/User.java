package volunteers.un.unitednationsvolunteers.Models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public  String profileUrl;
    public  String address;
    public  String phone;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public User(String username, String email,String url, String phone,String address){
        this.username = username;
        this.email = email;
        this.profileUrl = url;
        this.phone = phone;
        this.address = address;

    }

}
// [END blog_user_class]
