package me.sahiljain.tripTracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.entity.Notification;

/**
 * Created by sahil on 21/2/15.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private List<Notification> list;

    public NotificationsAdapter(Context context, List<Notification> resource) {
        super(context, R.layout.notifications_list_item, resource);
        this.context = context;
        this.list = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notifications_list_item, parent, false);

        TextView textViewMessage = (TextView) rowView.findViewById(R.id.text_view_notifications_list_item);
        textViewMessage.setText(list.get(position).getMessage());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_notifications_list_item);
        imageView.setImageResource(R.drawable.iconsmall);

        TextView textViewTimeStamp = (TextView) rowView.findViewById(R.id.time_stamp_notifications_list_item);
        textViewTimeStamp.setText(list.get(position).getTime());

        return rowView;
    }
}
