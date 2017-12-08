package volunteers.un.unitednationsvolunteers.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import volunteers.un.unitednationsvolunteers.Helpers.CircleTransform;
import volunteers.un.unitednationsvolunteers.Models.ChatUser;
import volunteers.un.unitednationsvolunteers.R;

/**
 * Created by jarman on 12/8/17.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView emailView;
    public ImageView profileView;

    public ContactViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.contact_name);
        emailView = itemView.findViewById(R.id.contact_email);
        profileView = (ImageView)itemView.findViewById(R.id.contact_author_photo);

    }

    public void bindToContact(Context c, ChatUser user) {
        nameView.setText(user.name);
        emailView.setText(user.email);
        if(user.profileUrl!=null)
                Picasso.with(c).load(user.profileUrl).fit().centerInside()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .error(R.drawable.com_facebook_profile_picture_blank_square).transform(new CircleTransform())
                        .into(profileView);
    }

}
