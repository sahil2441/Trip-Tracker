package me.sahiljain.locationstat.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.entity.UserDefault;

/**
 * Created by sahil on 28/2/15.
 */
public class UserDefaultAdapter extends ArrayAdapter {

    private Context context;
    private List<UserDefault> list;

    public UserDefaultAdapter(Context context, List<UserDefault> listFriends) {
        super(context, R.layout.list_of_friends_list_item, listFriends);
        this.context = context;
        this.list = listFriends;
    }

/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_of_friends_list_item_simple, parent, false);

        //Set name
        TextView textView = (TextView) rowView.findViewById(R.id.text_view_list_of_friends_list_item);
        textView.setText(list.get(position).getName());

        //Set userName
        TextView textViewUserName = (TextView) rowView.findViewById(R.id.username_list_of_friends_list_item);
        textViewUserName.setText(list.get(position).getUserID());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_list_of_friends_list_item);
        imageView.setImageResource(R.drawable.buddy);

        return rowView;
    }
*/
}
