package com.ezword.eventmap;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

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

        Button button1 = findViewById(R.id.button1);
        final EditText searchview = findViewById(R.id.searchView1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String g = searchview.getText().toString();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;

                try {
                    // Getting a maximum of 3 Address that matches the input
                    // text
                    addresses = geocoder.getFromLocationName(g, 3);
                    if (addresses != null && !addresses.equals(""))
                        search(addresses);

                } catch (Exception e) {

                }

            }
        });
    }

    private void search(List<Address> addresses) {
        Address address = (Address) addresses.get(0);
        double home_long = address.getLongitude();
        double home_lat = address.getLatitude();
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        TextView locationTv = findViewById(R.id.latlongLocation);
        locationTv.setText("Latitude:" + address.getLatitude() + ", Longitude:"
                + address.getLongitude());

    }
}
