package com.ezword.eventmap.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ezword.eventmap.R;
import com.ezword.eventmap.adapters.CustomInfoWindowAdapter;
import com.ezword.eventmap.adapters.FavoriteListAdapter;
import com.ezword.eventmap.cores.Event;
import com.ezword.eventmap.cores.FireBase;
import com.ezword.eventmap.cores.Host;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static double mSearchRadius = (double) 1000.0;
    private static String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ArrayList<Event> mEventData;
    private ArrayList<Host> mHostList;
    private RecyclerView mFavoriteList;
    private FavoriteListAdapter mFavoriteListAdapter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initData();
        setUpNavigationLayout();
        prepareMap();
        syncDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFavoriteListAdapter.updateData(mEventData);
    }

    private void setUpNavigationLayout() {
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mFavoriteList = findViewById(R.id.favorite_rv);
        mFavoriteListAdapter = new FavoriteListAdapter(this, mEventData, -1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        mFavoriteList.setLayoutManager(layoutManager);
        mFavoriteList.setAdapter(mFavoriteListAdapter);
    }

    private void syncDatabase() {
        FirebaseDatabase mDatabase = FireBase.getDatabase();
        DatabaseReference mReference = mDatabase.getReference();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "Value is: " + dataSnapshot.getValue());
                for (DataSnapshot eventSnapshot : dataSnapshot.child("events").getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    mEventData.add(event);
                    //Log.d(TAG, "Value is: " + event.toString());
                }
                for (DataSnapshot hostSnapshot : dataSnapshot.child("hosts").getChildren()) {
                    Host host = hostSnapshot.getValue(Host.class);
                    mHostList.add(host);
                    //Log.d(TAG, "Value is: " + host.toString());
                }
                loadMarker();
                findEventNearPositionNow();
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
                findEventNearHere(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        findViewById(R.id.maps_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findEventNearPositionNow();
            }
        });
    }

    private void prepareMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }

    public boolean inRadius(LatLng center, LatLng needCheckPoint) {
        return distance(center.latitude, center.longitude, needCheckPoint.latitude, needCheckPoint.longitude) < mSearchRadius;
    }

    private Marker setMarker(Event event) {
        Marker mk = mMap.addMarker(new MarkerOptions().position(event.getLocationLatLng()).title(event.getTitle()));
        mk.setTag(event);
        mk.setSnippet("Event");
        return mk;
    }

    private void loadMarker() {
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (mEventData.size() == 0)
            return;
        mFavoriteListAdapter.updateData(mEventData);
        for (int i = 0; i < mEventData.size(); i++) {
            setMarker(mEventData.get(i));
        }
        moveAndZoomPlace(mEventData.get(0).getLocationLatLng());
    }

    private boolean loadMarker(LatLng latLng) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (mEventData.size() == 0)
            return false;
        Marker focus = null;
        double minDistance = mSearchRadius;
        for (int i = 0; i < mEventData.size(); i++) {
            Event event = mEventData.get(i);
            if (inRadius(latLng, event.getLocationLatLng())) {
                double thisDistance = distance(latLng.latitude, latLng.longitude, event.getLocationLatLng().latitude, event.getLocationLatLng().longitude);
                Marker marker = setMarker(event);
                if (minDistance > thisDistance) {
                    minDistance = thisDistance;
                    focus = marker;
                }
            }
        }
        moveAndZoomPlace(mEventData.get(0).getLocationLatLng());
        if (focus != null) {
            focus.showInfoWindow();
            return true;
        }
        return false;
    }

    public void resetAdapter(int position) {
        mFavoriteListAdapter = new FavoriteListAdapter(this, mEventData, position);
        mFavoriteList.setAdapter(mFavoriteListAdapter);
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

    public void findEventNearPositionNow() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Dịch vụ vị trí chưa được bật", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                findEventNearHere(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });

    }
    public void findEventNearHere(LatLng latLng) {
        mMap.clear();
        if (loadMarker(latLng))
            mMap.addMarker(new MarkerOptions().position(latLng).title("Có một số sự kiện xung quanh bạn. Hãy bấm để khám phá!"));
        else
            mMap.addMarker(new MarkerOptions().position(latLng).title("Không tìm thấy sự kiện nào đang tổ chức gần bạn")).showInfoWindow();

        drawCircle(latLng);
        moveAndZoomPlace(latLng);
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(1000);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this)));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown())
                    marker.hideInfoWindow();
                else
                    marker.showInfoWindow();
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getSnippet() == null || !marker.getSnippet().equals("Event"))
                    return;
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                Event event = (Event) marker.getTag();
                if (event == null)
                    return;
                intent.putExtra("event", event);
                Host host = null;
                for (int i = 0; i < mHostList.size(); i++) {
                    if (mHostList.get(i).getHostId().equals(event.getHostId())) {
                        host = mHostList.get(i);
                    }
                }
                intent.putExtra("host", host);
                startActivity(intent);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                findEventNearHere(latLng);
            }
        });

        loadMarker();
        findEventNearPositionNow();

    }
}
