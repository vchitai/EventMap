package com.ezword.eventmap.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezword.eventmap.R;
import com.ezword.eventmap.activities.MapsActivity;
import com.ezword.eventmap.cores.Event;
import com.ezword.eventmap.cores.FavoritesList;

import java.util.ArrayList;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Event> mEvents = null;
    private ArrayList<String> mFavorites = null;
    private int mHighLightItem = -1;

    public FavoriteListAdapter(Context context, ArrayList<Event> events, int highLightItem) {
        mContext = context;
        mFavorites = FavoritesList.getInstance(mContext).getFavorites();
        mEvents = events;
        mHighLightItem = highLightItem;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public FavoriteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lectureView = inflater.inflate(R.layout.favorite_item, parent, false);

        return new ViewHolder(lectureView);
    }

    public void updateData(ArrayList<Event> events) {
        mEvents = events;
        mFavorites = FavoritesList.getInstance(mContext).getFavorites();
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final FavoriteListAdapter.ViewHolder holder, final int position) {
        if (mEvents.size() == 0)
            return;
        String id = mFavorites.get(position);
        Event event = null;
        for (int i = 0; i < mEvents.size(); i++)
            if (mEvents.get(i).getEventId().equals(id)) {
                event = mEvents.get(i);
                break;
            }
        if (mHighLightItem == position) {
            ((CardView) holder.mEventView).setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.mEventNameView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        }
        if (event == null)
            return;
        holder.mEventNameView.setText(event.getTitle());
        //holder.mEventDescView.setText(event.getDesc());
        final Event finalEvent = event;
        holder.mEventView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (mContext instanceof MapsActivity) {
                    ((MapsActivity) mContext).findEventNearHere(finalEvent.getLocationLatLng());
                    ((MapsActivity) mContext).resetAdapter(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mEventNameView;
        //TextView mEventDescView;
        View mEventView;

        ViewHolder(View view) {
            super(view);
            mEventNameView = view.findViewById(R.id.event_name);
            //mEventDescView = view.findViewById(R.id.event_desc);
            mEventView = view;
        }
    }
}
