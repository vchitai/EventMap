package com.ezword.eventmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Created by chita on 12/05/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater = null;

    CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap      bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private View setupEventView(Marker marker) {
        @SuppressLint("InflateParams") View parentView = inflater.inflate(R.layout.map_info_window, null);
        ImageView infoImage = parentView.findViewById(R.id.map_info_image);
        TextView infoTitle = parentView.findViewById(R.id.map_info_title);
        TextView infoDesc = parentView.findViewById(R.id.map_info_desc);
        final Event event = (Event) marker.getTag();
        assert event != null;
        Picasso.get().load(event.getPoster()).into(infoImage);
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
