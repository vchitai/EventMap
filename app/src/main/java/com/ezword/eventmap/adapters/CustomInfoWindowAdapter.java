package com.ezword.eventmap.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezword.eventmap.R;
import com.ezword.eventmap.cores.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater = null;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    private View setupEventView(Marker marker) {
        View parentView = inflater.inflate(R.layout.map_info_window, new LinearLayout(inflater.getContext()), false);
        ImageView infoImage = parentView.findViewById(R.id.map_info_image);
        TextView infoTitle = parentView.findViewById(R.id.map_info_title);
        TextView infoDesc = parentView.findViewById(R.id.map_info_desc);
        final Event event = (Event) marker.getTag();
        assert event != null;
        Picasso.get().load(event.getPoster()).placeholder(R.drawable.placeholder).into(infoImage);
        infoTitle.setText(event.getTitle());
        infoDesc.setText(event.getDesc());
        return parentView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (Objects.equals(marker.getSnippet(), "Event")) {
            return setupEventView(marker);
        }
        return null;
    }
}
