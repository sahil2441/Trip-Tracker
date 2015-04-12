package me.sahiljain.tripTracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import me.sahiljain.tripTracker.adapter.TripsAdapter;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;

/**
 * Created by sahil on 21/3/15.
 */
public class TripFragment extends Fragment {

    private Persistence persistence;

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ListView listView = new ListView(getActivity());
        persistence = new Persistence();

        List<Trip> list = persistence.fetchTrips(getActivity());
        TripsAdapter adapter = new TripsAdapter(getActivity(), list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        return listView;
    }
}
