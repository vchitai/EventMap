package com.ezword.eventmap;

import com.google.android.gms.maps.model.LatLng;

public class Event {
    private String location;
    private String title;
    private String eventId;
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
}
