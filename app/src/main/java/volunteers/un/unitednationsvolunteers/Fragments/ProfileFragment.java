package volunteers.un.unitednationsvolunteers.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import volunteers.un.unitednationsvolunteers.Helpers.CircleTransform;
import volunteers.un.unitednationsvolunteers.Models.ChatUser;
import volunteers.un.unitednationsvolunteers.R;
import volunteers.un.unitednationsvolunteers.data.StaticConfig;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    ChatUser user_profile_data;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference mDatabase;
    ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    TextView user_name_big,user_name,address,email,phone;
    ImageView profile_pic;
    int request_code = 3420;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==request_code&&resultCode== RESULT_OK){
            final Uri uri1=data.getData();
//            getActivity().startService(new Intent(getContext(), MyUploadService.class)
//                    .putExtra(MyUploadService.EXTRA_FILE_URI, uri)
//                    .setAction(MyUploadService.ACTION_UPLOAD));
            Dexter.withActivity(getActivity())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
//                    getActivity().startService(new Intent(getContext(), MyUploadService.class)
//                            .putExtra(MyUploadService.EXTRA_FILE_URI, uri1)
//                            .setAction(MyUploadService.ACTION_UPLOAD));
                }
                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
            }).check();
            if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {

                // do your stuff..
                Log.d("here1","here");
                showProgressDialog("Uploading");
                try {

                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri1);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bmp, uri1.getLastPathSegment().toString(), null);
                    Uri uri = Uri.parse(path);
                    StorageReference filepath= FirebaseStorage.getInstance().getReference("pictures");

                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            hideProgressDialog();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("new_profile",downloadUrl.toString());
                            ChatUser user = new ChatUser(user_profile_data.name, user_profile_data.email,downloadUrl.toString(),user_profile_data.phone,"default");
                            mDatabase.child("profileUrl").setValue(downloadUrl.toString());

                        }
                    });
                    Log.d("here2","here");
                    hideProgressDialog();
                } catch (Exception e) {
                    Log.d("here3","here");
                    hideProgressDialog();
                    Toast.makeText(getContext(),"Failed to upload",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }



        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        user_name_big =(TextView)root.findViewById(R.id.big_name);
        user_name =(TextView)root.findViewById(R.id.user_name) ;
        phone = (TextView)root.findViewById(R.id.phone) ;
        address = (TextView)root.findViewById(R.id.address) ;
        email = (TextView)root.findViewById(R.id.email) ;
        profile_pic =(ImageView) root.findViewById(R.id.profile);
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,request_code);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user_profile_data = dataSnapshot.getValue(ChatUser.class);
                user_name_big.setText(user_profile_data.name);
                user_name.setText(user_profile_data.name);
                phone.setText(user_profile_data.phone);
                email.setText(user_profile_data.email);
                address.setText("address");
                Log.d("pic",user_profile_data.profileUrl);
                Picasso.with(getContext()).load(user_profile_data.profileUrl).fit().centerInside()
                        .placeholder(R.drawable.edit_pic)
                        .error(R.drawable.edit_pic).transform(new CircleTransform())
                        .into(profile_pic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("User", "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }



    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getContext(), "PERMISSION Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
