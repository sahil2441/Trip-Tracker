package me.sahiljain.tripTracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import me.sahiljain.tripTracker.adapter.NotificationsAdapter;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Notification;

/**
 * Created by sahil on 21/3/15.
 */
public class NotificationFragment extends Fragment {

    private Persistence persistence;

    public static NotificationFragment newInstance() {
        NotificationFragment notificationFragment = new NotificationFragment();
        return notificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ListView listView = new ListView(getActivity());
        persistence = new Persistence();

        List<Notification> list = persistence.fetchNotifications(getActivity());

        NotificationsAdapter adapter = new NotificationsAdapter(getActivity(), list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        return listView;

    }
}
