package volunteers.un.unitednationsvolunteers;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import volunteers.un.unitednationsvolunteers.Fragments.MyPostsFragment;
import volunteers.un.unitednationsvolunteers.Fragments.MyTopPostsFragment;
import volunteers.un.unitednationsvolunteers.Fragments.PostListFragment;
import volunteers.un.unitednationsvolunteers.Fragments.RecentPostsFragment;
import volunteers.un.unitednationsvolunteers.Helpers.BottomNavigationViewHelper;
import volunteers.un.unitednationsvolunteers.Models.User;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    BottomNavigationView navigation;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseUser user1;
    private FirebaseAuth mAuth;
    DatabaseReference database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    MenuItem prevMenuItem;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_contact:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_safety:
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_profile:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();
        database.child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Log.d("access","OKAY");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("access","NOT OKAY");
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user1= user;

                if (user != null) {
                    String username = usernameFromEmail(user.getEmail());

                    // Write new user
                    writeNewUser(user.getUid(), username, user.getEmail());
                   // startActivity(new Intent(MainActivity.this,NewPostActivity.class));
                    mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                        private final Fragment[] mFragments = new Fragment[] {
                                new RecentPostsFragment(),
                                new MyPostsFragment(),
                                new MyTopPostsFragment()
                        };
                        private final String[] mFragmentNames = new String[] {
                                getString(R.string.title_feed),
                                getString(R.string.title_contact),
                                getString(R.string.title_safety)
                        };
                        @Override
                        public Fragment getItem(int position) {
                            return mFragments[position];
                        }
                        @Override
                        public int getCount() {
                            return mFragments.length;
                        }
                        @Override
                        public CharSequence getPageTitle(int position) {
                            return mFragmentNames[position];
                        }
                    };
                    mViewPager = findViewById(R.id.viewpager);
                    mViewPager.setAdapter(mPagerAdapter);
                    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            if (prevMenuItem != null) {
                                prevMenuItem.setChecked(false);
                            }
                            else
                            {
                                navigation.getMenu().getItem(0).setChecked(false);
                            }

                            navigation.getMenu().getItem(position).setChecked(true);
                            prevMenuItem = navigation.getMenu().getItem(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                   // signOut();
                    // User is signed in
                    Log.d("firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                   startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    Log.d("firebase", "onAuthStateChanged:signed_out");
                }

                // ...
            }
        };

        // Set up the ViewPager with the sections adapter.

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Firebase sign out
        mAuth.signOut();

        // Google sign out

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        database.child("users").child(userId).setValue(user);
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
