package volunteers.un.unitednationsvolunteers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import volunteers.un.unitednationsvolunteers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends ContactListFragment {


    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user");

    }

}
