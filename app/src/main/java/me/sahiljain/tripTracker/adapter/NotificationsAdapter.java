package me.sahiljain.tripTracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.NotificationDetailedActivity;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notifications_list_item, parent, false);

        TextView textViewMessage = (TextView) rowView.findViewById(R.id.text_view_notifications_list_item);
        textViewMessage.setText(list.get(position).getMessage());
        textViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationDetailedActivity(list.get(position).getMessage(),
                        list.get(position).getSenderID());
            }
        });

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_notifications_list_item);
        imageView.setImageResource(R.drawable.iconsmall);

        TextView textViewTimeStamp = (TextView) rowView.findViewById(R.id.time_stamp_notifications_list_item);
        textViewTimeStamp.setText(list.get(position).getTime());

        return rowView;
    }

    private void openNotificationDetailedActivity(String message, String senderID) {
        Intent intent = new Intent(context, NotificationDetailedActivity.class);
        intent.putExtra(Constants.SENDER_ID, senderID);
        intent.putExtra(Constants.NOTIFICATION, message);
        context.startActivity(intent);

    }
}
