package me.sahiljain.locationstat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 21/2/15.
 */
public class NotificationsAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;


    public NotificationsAdapter(Context context, List<String> resource) {
        super(context, R.layout.notification_list_item, resource);
        this.context = context;
        this.list = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_list_item, parent, false);
        TextView textViewMessage = (TextView) rowView.findViewById(R.id.text_view_notifications_list_item);
        textViewMessage.setText(list.get(position));

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_notifications_list_item);
        imageView.setImageResource(R.drawable.iconsmall);

        TextView textViewTimeStamp = (TextView) rowView.findViewById(R.id.time_stamp_notifications_list_item);
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        String s;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            s = "AM";
        } else {
            s = "PM";
        }

        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
                + " " + s;
        textViewTimeStamp.setText(curTime);
        return rowView;
    }
}
