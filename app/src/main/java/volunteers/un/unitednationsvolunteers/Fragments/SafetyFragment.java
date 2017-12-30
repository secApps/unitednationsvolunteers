package volunteers.un.unitednationsvolunteers.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;


import java.util.List;


import volunteers.un.unitednationsvolunteers.MainActivityChat;
import volunteers.un.unitednationsvolunteers.R;
import volunteers.un.unitednationsvolunteers.UnsafelistActivity;
import volunteers.un.unitednationsvolunteers.data.StaticConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class SafetyFragment extends Fragment {

Button update,unsafe;

    public SafetyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_blank, container, false);
        update =(Button)root.findViewById(R.id.update_location);
        unsafe=(Button)root.findViewById(R.id.unsafe);
        unsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UnsafelistActivity.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location/"+StaticConfig.UID);
                                final DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
                               final GeoFire geoFire = new GeoFire(ref);
// Acquire a reference to the system Location Manager
                                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
                                LocationListener locationListener = new LocationListener() {
                                    public void onLocationChanged(Location location) {
                                        geoFire.setLocation("loc", new GeoLocation(location.getLatitude(), location.getLongitude()));
                                        user_ref.child("latitude").setValue(location.getLatitude());
                                        user_ref.child("longitude").setValue(location.getLongitude());
                                        // Called when a new location is found by the network location provider.
                                     Log.d("LOCATION",location.toString());
                                    }

                                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                                    public void onProviderEnabled(String provider) {}

                                    public void onProviderDisabled(String provider) {}
                                };

// Register the listener with the Location Manager to receive location updates
                                Location lastKnownLocation=  locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                geoFire.setLocation("loc", new GeoLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                                user_ref.child("latitude").setValue(lastKnownLocation.getLatitude());
                                user_ref.child("longitude").setValue(lastKnownLocation.getLongitude());
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            }
                        }).check();
// Register the listener with the Location Manager to receive location updates
                 }
        });

        return root;
    }

}
