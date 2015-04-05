package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.SearchResultsAdapter;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 1/3/15.
 */
public class SearchResults extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        setContentView(R.layout.search_results);
        EditText editText = (EditText) findViewById(R.id.edit_text_search_results);
        editText.setCursorVisible(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchLocation = s.toString();
                if (searchLocation != null &&
                        !searchLocation.equalsIgnoreCase("")) {
                    new GeoCoderTask().execute(searchLocation);
                }
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view_search_results);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String latitude = ((TextView) view.findViewById(R.id.lat_search_results__list_item))
                        .getText().toString();
                String longitude = ((TextView) view.findViewById(R.id.long_search_results__list_item))
                        .getText().toString();

                editor.putString(Constants.SEARCH_LAT, latitude);
                editor.putString(Constants.SEARCH_LONG, longitude);
                editor.apply();
                onNavigateUp();
            }
        });
    }

    private class GeoCoderTask extends AsyncTask<String, Void, List<Address>> {
        @Override
        protected List<Address> doInBackground(String... searchLocation) {
            //Create an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            try {
                //Try to get a max of 100 results for the search
                addresses = geocoder.getFromLocationName(searchLocation[0], 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses == null || addresses.size() == 0) {
//                Toast.makeText(getBaseContext(), "No LocationFound", Toast.LENGTH_SHORT).show();
            } else {

                // Pass this list of Addresses to the Search Results Adapter
                ListView listView = (ListView) findViewById(R.id.list_view_search_results);
                listView.setAdapter(new SearchResultsAdapter(getApplicationContext(), addresses));
            }
        }
    }

}
