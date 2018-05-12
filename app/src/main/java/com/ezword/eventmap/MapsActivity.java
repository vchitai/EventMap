package com.ezword.eventmap;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<Event> mEventData;
    private ArrayList<Host> mHostList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private static String TAG = "MapsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initData();
        prepareMap();
        syncDatabase();
    }

    private void syncDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mReference = mDatabase.getReference();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "Value is: " + dataSnapshot.getValue());
                for (DataSnapshot eventSnapshot: dataSnapshot.child("events").getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    mEventData.add(event);
                    //Log.d(TAG, "Value is: " + event.toString());
                }
                for (DataSnapshot hostSnapshot: dataSnapshot.child("hosts").getChildren()) {
                    Host host = hostSnapshot.getValue(Host.class);
                    mHostList.add(host);
                    //Log.d(TAG, "Value is: " + host.toString());
                }
                loadMarker();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initData() {
        mEventData = new ArrayList<>();
        mHostList = new ArrayList<>();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                moveAndZoomPlace(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void prepareMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadMarker() {
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (mEventData.size() == 0)
            return;
        for (int i = 0; i<mEventData.size(); i++) {
            Marker mk = mMap.addMarker(new MarkerOptions().position(mEventData.get(i).getLocationLatLng()).title(mEventData.get(i).getTitle()));
            mk.setTag(mEventData.get(i));
        }
        moveAndZoomPlace(mEventData.get(0).getLocationLatLng());
    }

    private void moveAndZoomPlace(LatLng latLng) {
        if (latLng == null || mMap == null)
            return;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this)));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });

        loadMarker();

    }
}
