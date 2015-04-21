package me.sahiljain.tripTracker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.main.Constants;

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
                v.findViewById(R.id.text_view_trips_list_item).setBackgroundColor(currentColor);
                Toast toast = Toast.makeText(context, name + " " + "has been set as Default Trip",
                        Toast.LENGTH_SHORT);
                toast.show();
                activateTrip(trips.get(position).getTripId(), trips.get(position).getTripName());
            }
        });

        //For the case when view is rendered in the beginning
        if (trips.get(position).getTripId().equals(activeTripId)) {
            rowView.findViewById(R.id.text_view_trips_list_item).setBackgroundColor(currentColor);
        }
        ImageView imageViewDelete = (ImageView) rowView.findViewById(R.id.delete_trips_list_item);
        imageViewDelete.setImageResource(R.drawable.delete_trip);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialogue(trips.get(position).getTripId(), trips.get(position).getTripName());
            }
        });

        return rowView;
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
                        initiateDeleteTrip(tripId, tripName);

                    }
                }).
                show();
    }

    private void initiateDeleteTrip(Integer tripId, String tripName) {
        persistence.deleteTrip(context, tripId);
        Toast toast = Toast.makeText(context, tripName + " " + "has been deleted",
                Toast.LENGTH_SHORT);
        toast.show();

    }

    private void activateTrip(Integer tripId, String tripName) {
        persistence.activateTrip(context, tripId);
        /**
         * save default active trip is in shared preferences
         */
        preferences = context.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.ACTIVE_TRIP, tripId);
//        editor.putInt(Constants.ACTIVE_TRIP_POSITION, position);
        editor.apply();

        /**
         * Show Toast
         */
        Toast toast = Toast.makeText(context, tripName + " " + "has been set as Default Trip",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
