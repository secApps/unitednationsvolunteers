package volunteers.un.unitednationsvolunteers.viewholders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import volunteers.un.unitednationsvolunteers.ChatListActivity;
import volunteers.un.unitednationsvolunteers.Helpers.CircleTransform;
import volunteers.un.unitednationsvolunteers.Models.ChatUser;
import volunteers.un.unitednationsvolunteers.Models.Friend;
import volunteers.un.unitednationsvolunteers.Models.ListFriend;
import volunteers.un.unitednationsvolunteers.R;
import volunteers.un.unitednationsvolunteers.data.FriendDB;
import volunteers.un.unitednationsvolunteers.data.StaticConfig;
import volunteers.un.unitednationsvolunteers.ui.ChatActivity;

/**
 * Created by jarman on 12/8/17.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView emailView;
    public ImageView profileView;
    public ImageButton call, email, sms, chat;
    Activity context;
    LovelyProgressDialog dialogWait;
    private ListFriend dataListFriend = null;
    private ArrayList<String> listFriendID = null;

    public ContactViewHolder(View itemView, Activity context) {
        super(itemView);
        this.context = context;
        nameView = itemView.findViewById(R.id.contact_name);
        emailView = itemView.findViewById(R.id.contact_email);
        profileView = (ImageView) itemView.findViewById(R.id.contact_author_photo);
        call = (ImageButton) itemView.findViewById(R.id.call);
        email = (ImageButton) itemView.findViewById(R.id.email);
        sms = (ImageButton) itemView.findViewById(R.id.sms);
        chat = (ImageButton) itemView.findViewById(R.id.chat);
        dialogWait = new LovelyProgressDialog(context);
        listFriendID = new ArrayList<>();
        if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(context).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendID.add(friend.id);
                }
            }
        }

    }

    public void bindToContact(Context c, final ChatUser user) {
        nameView.setText(user.name);
        emailView.setText(user.email);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Dexter.withActivity(context)
                            .withPermission(Manifest.permission.CALL_PHONE)
                            .withListener(new PermissionListener() {
                                @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.phone));
                                    context.getApplicationContext().startActivity(intent);
                                    /* ... */}
                                @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */
                                    Toast.makeText(context,"Please Accept Calling Permission",Toast.LENGTH_SHORT).show();}
                                @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                            }).check();


            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                intent.setData(Uri.parse("mailto:"+user.email)); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                context.startActivity(intent);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(context)
                        .withPermission(Manifest.permission.SEND_SMS)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + user.phone));
                                context.getApplicationContext().startActivity(intent);
                                    /* ... */}
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */
                                Toast.makeText(context,"Please Accept SMS Permission",Toast.LENGTH_SHORT).show();}
                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                        }).check();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findIDEmail(user.email);
            }
        });
        if(user.profileUrl!=null)
                Picasso.with(c).load(user.profileUrl).fit().centerInside()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .error(R.drawable.com_facebook_profile_picture_blank_square).transform(new CircleTransform())
                        .into(profileView);
    }

    private void findIDEmail(String email) {

        dialogWait.setCancelable(false)
                .setIcon(R.drawable.ic_add_friend)
                .setTitle("Finding friend....")
                .setTopColorRes(R.color.colorPrimary)
                .show();
        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogWait.dismiss();
                if (dataSnapshot.getValue() == null) {
                    //email not found
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_add_friend)
                            .setTitle("Fail")
                            .setMessage("Email not found")
                            .show();
                } else {
                    String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                    if (id.equals(StaticConfig.UID)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Fail")
                                .setMessage("Email not valid")
                                .show();
                    } else {
                        HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                        Friend user = new Friend();
                        user.name = (String) userMap.get("name");
                        user.email = (String) userMap.get("email");
                        user.avata = (String) userMap.get("avata");
                        user.id = id;
                        user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                        checkBeforAddFriend(id, user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void checkBeforAddFriend(String idFriend, Friend userInfo) {
        dialogWait.setCancelable(false)
                .setIcon(R.drawable.ic_add_friend)
                .setTitle("Add friend....")
                .setTopColorRes(R.color.colorPrimary)
                .show();

        //Check xem da ton tai id trong danh sach id chua
        if (listFriendID.contains(idFriend)) {
            dialogWait.dismiss();
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, userInfo.name);
            ArrayList<CharSequence> idFriendd = new ArrayList<CharSequence>();
            idFriendd.add(idFriend);
            intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriendd);
            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, userInfo.idRoom);
            ChatActivity.bitmapAvataFriend = new HashMap<>();
            if (!userInfo.avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                byte[] decodedString = Base64.decode(userInfo.avata, Base64.DEFAULT);
                ChatActivity.bitmapAvataFriend.put(userInfo.id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            } else {
                ChatActivity.bitmapAvataFriend.put(userInfo.id, BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_pic));
            }
            context.startActivity(intent);
        } else {
            addFriend(idFriend, true);
            FriendDB.getInstance(context).addFriend(userInfo);
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, userInfo.name);
            ArrayList<CharSequence> idFriendd = new ArrayList<CharSequence>();
            idFriendd.add(idFriend);
            intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriendd);
            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, userInfo.idRoom);
            ChatActivity.bitmapAvataFriend = new HashMap<>();
            if (!userInfo.avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                byte[] decodedString = Base64.decode(userInfo.avata, Base64.DEFAULT);
                ChatActivity.bitmapAvataFriend.put(userInfo.id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            } else {
                ChatActivity.bitmapAvataFriend.put(userInfo.id, BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_pic));
            }
            context.startActivity(intent);

        }
    }

    private void addFriend(final String idFriend, boolean isIdFriend) {
        {
            if (idFriend != null) {
                if (isIdFriend) {
                    FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).push().setValue(idFriend)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        addFriend(idFriend, false);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialogWait.dismiss();
                                    new LovelyInfoDialog(context)
                                            .setTopColorRes(R.color.colorAccent)
                                            .setIcon(R.drawable.ic_add_friend)
                                            .setTitle("False")
                                            .setMessage("False to add friend success")
                                            .show();
                                }
                            });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(StaticConfig.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                addFriend(null, false);
                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialogWait.dismiss();
                                    new LovelyInfoDialog(context)
                                            .setTopColorRes(R.color.colorAccent)
                                            .setIcon(R.drawable.ic_add_friend)
                                            .setTitle("False")
                                            .setMessage("False to add friend success")
                                            .show();
                                }
                            });
                }
            } else {
                dialogWait.dismiss();
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_add_friend)
                        .setTitle("Success")
                        .setMessage("Add friend success")
                        .show();
            }
        }
    }

}
