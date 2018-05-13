package com.ezword.eventmap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Event event = (Event) getIntent().getSerializableExtra("event");
        final Host host = (Host) getIntent().getSerializableExtra("host");
        ((TextView) findViewById(R.id.map_info_title)).setText(event.getTitle());
        ((TextView) findViewById(R.id.map_info_desc)).setText(event.getDesc());
        ((TextView) findViewById(R.id.map_info_date)).setText(event.getDate());
        ((TextView) findViewById(R.id.map_info_address)).setText(event.getAddress());
        ((TextView) findViewById(R.id.host_name)).setText(host.getName());
        ((TextView) findViewById(R.id.host_desc)).setText(host.getLink());
        Picasso.get().load(event.getPoster()).into((ImageView)findViewById(R.id.map_info_image));
        findViewById(R.id.host_phone_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + host.getPhone()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
            }
        });
        findViewById(R.id.host_link_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://" + host.getLink()));
                startActivity(i);
            }
        });
        findViewById(R.id.host_email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Cần thêm thông tin");
                intent.putExtra(Intent.EXTRA_TEXT, "...");
                intent.setData(Uri.parse("mailto:" + host.getEmail())); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoritesList favoritesList = FavoritesList.getInstance(getApplicationContext());
                int id = favoritesList.isExist(event.getEventId());
                if (id == -1) {
                    favoritesList.addFavorites(event.getEventId());
                    Toast.makeText(getApplicationContext(), "Favorite List added", Toast.LENGTH_SHORT).show();
                }
                else {
                    favoritesList.removeFavorites(id);
                    Toast.makeText(getApplicationContext(), "Favorite List removed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
