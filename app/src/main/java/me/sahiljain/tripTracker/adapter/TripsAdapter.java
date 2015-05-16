package me.sahiljain.tripTracker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.TabMainActivityUpdateListener;
import me.sahiljain.tripTracker.main.TripDetailedActivity;

/**
 * Created by sahil on 22/3/15.
 */
public class TripsAdapter extends ArrayAdapter<Trip> {

    private Context context;

    private List<Trip> trips;

    private Persistence persistence;

    private SharedPreferences preferences;

    private int activeTripId;

    private int currentColor;

    private TabMainActivityUpdateListener tabMainActivityUpdateListener;

    public TripsAdapter(Context context, List<Trip> resource) {
        super(context, R.layout.trips_list_item, resource);
        this.trips = resource;
        this.context = context;
        preferences = context.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        activeTripId = preferences.getInt(Constants.ACTIVE_TRIP, 0);
        persistence = new Persistence();
        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trips_list_item, parent, false);

        final TextView textViewMessage = (TextView) rowView.findViewById(R.id.text_view_trips_list_item);
        textViewMessage.setText(trips.get(position).getTripName());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_trips_list_item);
        imageView.setImageResource(R.drawable.logo_trip);

        final String name = trips.get(position).getTripName();
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Launch new Activity- TripDetailView
                 */
                launchTripDetailViewActivity(trips.get(position).getTripId(), trips.get(position).getTripName());
            }
        });

        //For the case when view is rendered in the beginning
        if (trips.get(position).getTripId().equals(activeTripId)) {
            rowView.findViewById(R.id.text_view_trips_list_item).setBackgroundColor(currentColor);
        }

        return rowView;
    }

    private void launchTripDetailViewActivity(Integer tripId, String tripName) {
        Intent intent = new Intent(context, TripDetailedActivity.class);
        intent.putExtra(Constants.TRIP_ID, tripId);
        intent.putExtra(Constants.TRIP_NAME, tripName);
        context.startActivity(intent);
    }

    private void showConfirmationDialogue(final Integer tripId, final String tripName) {
        new AlertDialog.Builder(context).setTitle("Confirm").
                setMessage("Delete Trip " + tripName + "?").
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                show();
    }

}
