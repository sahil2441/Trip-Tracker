package me.sahiljain.tripTracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.entity.Trip;

/**
 * Created by sahil on 22/3/15.
 */
public class TripsAdapter extends ArrayAdapter<Trip> {

    private Context context;

    private List<Trip> trips;

    public TripsAdapter(Context context, List<Trip> resource) {
        super(context, R.layout.trips_list_item);
        this.trips = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trips_list_item, parent, false);

        TextView textViewMessage = (TextView) rowView.findViewById(R.id.text_view_trips_list_item);
        textViewMessage.setText(trips.get(position).getTripName());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_trips_list_item);
        imageView.setImageResource(R.drawable.logo_trip);

        return rowView;
    }
}
