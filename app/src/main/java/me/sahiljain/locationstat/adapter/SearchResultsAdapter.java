package me.sahiljain.locationstat.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 1/3/15.
 */
public class SearchResultsAdapter extends ArrayAdapter<Address> {

    private Context context;
    private List<Address> list;

    public SearchResultsAdapter(Context context, List<Address> resource) {
        super(context, R.layout.search_results_list_item, resource);
        this.context = context;
        this.list = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_results_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text_view_search_results_list_item);
        String address = "";
        for (int i = 0; i < list.get(position).getMaxAddressLineIndex(); i++) {
            address += list.get(position).getAddressLine(i);
        }
        textView.setText(address);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo_search_results_list_item);
        imageView.setImageResource(R.drawable.search_logo);

        TextView textViewLat = (TextView) rowView.findViewById(R.id.lat_search_results__list_item);
        textViewLat.setText(String.valueOf(list.get(position).getLatitude()));

        TextView textViewLong = (TextView) rowView.findViewById(R.id.long_search_results__list_item);
        textViewLong.setText(String.valueOf(list.get(position).getLongitude()));

        return rowView;
    }
}
