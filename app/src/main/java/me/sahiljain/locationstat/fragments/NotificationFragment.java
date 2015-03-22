package me.sahiljain.locationstat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import me.sahiljain.locationstat.adapter.NotificationsAdapter;
import me.sahiljain.locationstat.db.DataBaseNotifications;

/**
 * Created by sahil on 21/3/15.
 */
public class NotificationFragment extends Fragment {

    private DataBaseNotifications dataBaseNotifications;


    public static NotificationFragment newInstance() {
        NotificationFragment notificationFragment = new NotificationFragment();
        return notificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ListView listView = new ListView(getActivity());
        dataBaseNotifications = new DataBaseNotifications(getActivity());

        List<String> list = dataBaseNotifications.fetchListNotifications();

        List<String> listTimeStamp = dataBaseNotifications.fetchListTime();

        NotificationsAdapter adapter = new NotificationsAdapter(getActivity(), list, listTimeStamp);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        return listView;

    }
}
