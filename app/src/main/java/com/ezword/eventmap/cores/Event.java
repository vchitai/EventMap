package com.ezword.eventmap.cores;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId;
    private String title;
    private String location;
    private String address;
    private String date;
    private String poster;
    private String desc;
    private String hostId;

    public Event() {

    }

    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getLocation() {
        return location;
    }

    public LatLng getLocationLatLng() {
        String[] latLng = location.split(",");
        return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
    }

    public String getEventId() {
        return eventId;
    }

    public String getHostId() {
        return hostId;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }
}
