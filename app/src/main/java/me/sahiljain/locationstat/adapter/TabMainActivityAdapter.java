package me.sahiljain.locationstat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.sahiljain.locationstat.mainTab.NotificationFragment;
import me.sahiljain.locationstat.mainTab.TripFragment;

/**
 * Created by sahil on 21/3/15.
 */
public class TabMainActivityAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"List of Trips", "Notifications"};

    public TabMainActivityAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return TripFragment.newInstance(index);
            case 1:
                return NotificationFragment.newInstance(index);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
