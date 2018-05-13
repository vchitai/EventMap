package com.ezword.eventmap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by chita on 13/05/2018.
 */

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Event> mEvents = null;
    private ArrayList<String> mFavorites = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mEventNameView;
        public TextView  mEventDescView;
        public View mEventView;
        public ViewHolder(View view) {
            super(view);
            mEventNameView = view.findViewById(R.id.event_name);
            mEventDescView = view.findViewById(R.id.event_desc);
            mEventView = view;
        }
    }

    public FavoriteListAdapter(Context context, ArrayList<Event> events) {
        mContext = context;
        mFavorites = FavoritesList.getInstance(mContext).getFavorites();
        mEvents = events;
    }

    @Override
    public FavoriteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View lectureView = inflater.inflate(R.layout.favorite_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(lectureView);
    }

    public void updateData(ArrayList<Event> events) {
        mEvents = events;
        mFavorites = FavoritesList.getInstance(mContext).getFavorites();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(FavoriteListAdapter.ViewHolder holder, int position) {
        if (mEvents.size() == 0)
            return;
        String id = mFavorites.get(position);
        Event event = null;
        for (int i = 0; i<mEvents.size(); i++)
            if (mEvents.get(i).getEventId().equals(id)) {
                event = mEvents.get(i);
                break;
            }

        if (event == null)
            return;
        holder.mEventNameView.setText(event.getTitle());
        holder.mEventDescView.setText(event.getDesc());
        final Event finalEvent = event;
        holder.mEventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof MapsActivity) {
                    ((MapsActivity) mContext).findEventNearHere(finalEvent.getLocationLatLng());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }
}
