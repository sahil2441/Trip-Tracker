package me.sahiljain.locationstat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 21/2/15.
 */
public class PreferencesAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> list;

    public PreferencesAdapter(Context context, List<String> resource) {
        super(context, R.layout.preferences_list_item, resource);
        this.context = context;
        this.list = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.preferences_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text_view_preferences_list_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_preferences_list_item);
        textView.setText(list.get(position));

        if (list.get(position).equalsIgnoreCase(Constants.ADD_A_FRIEND)) {
            imageView.setImageResource(R.drawable.add_friend);
        } else if (list.get(position).equalsIgnoreCase(Constants.PROFILE)) {
            imageView.setImageResource(R.drawable.profile);
        } else if (list.get(position).equalsIgnoreCase(Constants.NOTIFICATION_SETTINGS)) {
            imageView.setImageResource(R.drawable.notification_bell);
        } else if (list.get(position).equalsIgnoreCase(Constants.LIST_OF_FRIENDS)) {
            imageView.setImageResource(R.drawable.list_of_friends);
        }
        return rowView;
    }
}
