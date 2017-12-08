package volunteers.un.unitednationsvolunteers.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import volunteers.un.unitednationsvolunteers.Models.Post;
import volunteers.un.unitednationsvolunteers.R;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public  ImageView imageView;
    public  ImageView author_photo;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
        imageView = (ImageView)itemView.findViewById(R.id.image);
        author_photo = (ImageView)itemView.findViewById(R.id.post_author_photo);
        imageView.setVisibility(View.GONE);
    }

    public void bindToPost(Context c,Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        Picasso.with(c).load(post.profileUrl).fit().centerInside()
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(author_photo);
        if(post.imgurl!=null)
        if(post.imgurl.isEmpty()){
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(c).load(post.imgurl).fit().centerInside()
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(imageView);
        }

        starView.setOnClickListener(starClickListener);
    }

}
