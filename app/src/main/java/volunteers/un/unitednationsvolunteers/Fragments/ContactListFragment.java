package volunteers.un.unitednationsvolunteers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import volunteers.un.unitednationsvolunteers.R;
import volunteers.un.unitednationsvolunteers.viewholders.ContactViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class ContactListFragment extends Fragment {

    private static final String TAG = "ContactListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<ChatUser, ContactViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public ContactListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.contact_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query contactsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ChatUser>()
                .setQuery(contactsQuery, ChatUser.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<ChatUser, ContactViewHolder>(options) {

            @Override
            public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ContactViewHolder(inflater.inflate(R.layout.contact_item, viewGroup, false),getActivity());
            }

            @Override
            protected void onBindViewHolder(ContactViewHolder viewHolder, int position, final ChatUser model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();


                // Bind Post to ViewHolder
                viewHolder.bindToContact(getContext(), model);
            }};

                mRecycler.setAdapter(mAdapter);
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

            public abstract Query getQuery(DatabaseReference databaseReference);

        }


