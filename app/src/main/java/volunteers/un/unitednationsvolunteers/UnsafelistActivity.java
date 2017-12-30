package volunteers.un.unitednationsvolunteers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import volunteers.un.unitednationsvolunteers.Models.ChatUser;
import volunteers.un.unitednationsvolunteers.viewholders.ContactViewHolder;
import volunteers.un.unitednationsvolunteers.viewholders.SafetyViewHolder;

public class UnsafelistActivity extends AppCompatActivity {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<ChatUser, SafetyViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsafelist);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler = findViewById(R.id.unsafe_list);
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query contactsQuery = getQuery(mDatabase);
        contactsQuery.keepSynced(true);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ChatUser>()
                .setQuery(contactsQuery, ChatUser.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<ChatUser, SafetyViewHolder>(options) {

            @Override
            public SafetyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new SafetyViewHolder(inflater.inflate(R.layout.unsafe_item, viewGroup, false),UnsafelistActivity.this);
            }

            @Override
            protected void onBindViewHolder(SafetyViewHolder viewHolder, int position, final ChatUser model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();


                // Bind Post to ViewHolder
                viewHolder.bindToContact(getApplicationContext(), model);
            }};

        mRecycler.setAdapter(mAdapter);
    }

    private Query getQuery(DatabaseReference mDatabase) {
        return  mDatabase.child("user");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
