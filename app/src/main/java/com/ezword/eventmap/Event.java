package com.ezword.eventmap;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chita on 12/05/2018.
 */

public class Event {
    private String mThumbnailImage;
    private String mTitle;
    private String mDescription;
    private LatLng mLocation;
    private String mHostPhone;

    public Event(String title, String desc, String thumbnail, LatLng location) {
        mThumbnailImage = thumbnail;
        mTitle = title;
        mDescription = desc;
        mLocation = location;
    }

    public String getThumbnailImage() {
        return mThumbnailImage;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public LatLng getLocation() {
        return mLocation;
    }
    public String getHostPhone() {
        return mHostPhone;
    }

}
