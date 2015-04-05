package me.sahiljain.tripTracker.main;

import android.app.Application;

import com.parse.Parse;

import me.sahiljain.tripTracker.entity.Trip;

/**
 * Created by sahil on 15/2/15.
 */
public class App extends Application {

    /**
     * Create a trip variable here.
     * this variable is carried through all the activities when a new trip is being created.
     */
    private Trip trip = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "g6RAVxcxermOczF7n8WEuN7nBTe7vTzADJTqMh6F", "v5zBzf0ZxefhdnLnRulZ8dSkUjsOn1sYuQAEb89Z");
        trip = new Trip();
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
