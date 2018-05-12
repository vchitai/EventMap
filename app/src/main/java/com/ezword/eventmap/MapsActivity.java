package com.ezword.eventmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Event> mEventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initData();
    }

    private void initData() {
        mEventData = new ArrayList<Event>();
        mEventData.add(new Event("Linh tinh", "Linh tinh", "bg.jpg", new LatLng(10.7624165,106.6790126)));
        mEventData.add(new Event("Linh tinh", "Linh tinh", "bg.jpg", new LatLng(10.773266,106.6572787)));
        mEventData.add(new Event("Linh tinh", "Linh tinh", "bg.jpg", new LatLng(10.7732638,106.6244477)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (int i = 0; i<mEventData.size(); i++) {
            Marker mk = mMap.addMarker(new MarkerOptions().position(mEventData.get(i).getLocation()).title(mEventData.get(i).getTitle()));
            mk.setTag(mEventData.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mEventData.get(0).getLocation()));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this)));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Event event = (Event)marker.getTag();
                marker.showInfoWindow();
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
    }
}
