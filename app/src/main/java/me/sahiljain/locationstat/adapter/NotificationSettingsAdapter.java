package me.sahiljain.locationstat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.main.Constants;

/**
 * Created by sahil on 21/2/15.
 */
public class NotificationSettingsAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;

    public NotificationSettingsAdapter(Context context, List<String> resource) {
        super(context, R.layout.notification_settings_list_item, resource);
        this.context = context;
        this.list = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_settings_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text_view_notification_settings_list_item);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.check_box_notification_settings);
        textView.setText(list.get(position));
        SharedPreferences preferences = context.getSharedPreferences
                (Constants.LOCATION_STAT_SHARED_PREFERNCES, Context.MODE_PRIVATE);
        checkBox.setChecked(preferences.getBoolean(Constants.NOTIFICATION_SETTINGS_CHECK_BOX + position, false));

        final SharedPreferences.Editor editor = preferences.edit();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(Constants.NOTIFICATION_SETTINGS_CHECK_BOX + position, isChecked);
                editor.commit();
            }
        });
        return rowView;
    }
}
