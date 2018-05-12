package com.ezword.eventmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by chita on 12/05/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;

    CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View parentView = inflater.inflate(R.layout.map_info_window, null);
        ImageView infoImage = parentView.findViewById(R.id.map_info_image);
        TextView infoTitle = parentView.findViewById(R.id.map_info_title);
        TextView infoDesc = parentView.findViewById(R.id.map_info_desc);
        final Event event = (Event) marker.getTag();
        assert event != null;
        infoTitle.setText(event.getTitle());
        infoDesc.setText(event.getDesc());
        return parentView;
    }
}
