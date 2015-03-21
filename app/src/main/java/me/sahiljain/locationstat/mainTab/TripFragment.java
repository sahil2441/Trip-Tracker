package me.sahiljain.locationstat.mainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by sahil on 21/3/15.
 */
public class TripFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    public static TripFragment newInstance(int position) {
        TripFragment tripFragment = new TripFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        tripFragment.setArguments(bundle);
        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout.LayoutParams layoutParams = new
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(layoutParams);
        return frameLayout;
    }

}
