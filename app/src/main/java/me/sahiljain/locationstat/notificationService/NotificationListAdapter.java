package me.sahiljain.locationstat.notificationService;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 19/2/15.
 */
public class NotificationListAdapter extends BaseAdapter {

    private Context context;

    private List<String> list;

    public NotificationListAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.list = stringList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = list.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.notification_list_item, null);
        TextView textView = (TextView) convertView.findViewById(R.id.text_view_notification_list_item);
        textView.setText(s);
        return convertView;
    }
}
