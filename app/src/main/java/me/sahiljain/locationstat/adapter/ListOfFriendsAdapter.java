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

/**
 * Created by sahil on 28/2/15.
 */
public class ListOfFriendsAdapter extends ArrayAdapter {

    private Context context;
    private List<String> list;

    public ListOfFriendsAdapter(Context context, List<String> listFriends) {
        super(context, R.layout.list_of_friends_list_item, listFriends);
        this.context = context;
        this.list = listFriends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_of_friends_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text_view_list_of_friends_list_item);
        textView.setText(list.get(position));

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_list_of_friends_list_item);
        imageView.setImageResource(R.drawable.buddy);

        return rowView;
    }
}
