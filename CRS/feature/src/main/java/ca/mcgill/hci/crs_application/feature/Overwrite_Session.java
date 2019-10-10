package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Overwrite_Session extends Activity {
    private SharedPreferences preferences = null;
    private Map<String, String> nameToUUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overwrite_session);

        preferences = getPreferences(MODE_PRIVATE);

        ArrayList<String> locationNames = getLocationNames();
        locationNames.add(0, "No selection");
        // final String[] spstr = getResources().getStringArray(R.array.location_arrays);
        final Spinner sp = (Spinner)findViewById(R.id.overwrite_location_spinner);
        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,locationNames);
        sp.setAdapter(ar);
        sp.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String s=((TextView)view).getText().toString();

                if(!s.equals("No selection")) {
                    String uuid = nameToUUID.get(s);
                    Intent intent = new Intent(view.getContext(), Manage_Location.class);
                    intent.putExtra("uuid", uuid);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    private ArrayList<String> getLocationNames() {
        ArrayList<String> locationNames = new ArrayList<String>();
        try {
            JSONObject contents = SavedData.getFileContents(this);
            JSONArray locations = contents.getJSONArray("locations");
            nameToUUID = new HashMap<>(locations.length());
            for (int i = 0; i < locations.length(); i++) {
                JSONObject location = locations.getJSONObject(i);
                String name = location.getString("name");
                if (locationNames.contains(name)) {
                    name += " ";
                    int digit = 1;
                    while (locationNames.contains(name + String.valueOf(digit))) {
                        digit += 1;
                    }
                    name += String.valueOf(digit);
                }
                locationNames.add(name);
                nameToUUID.put(name, location.getString("uuid"));
            }
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return locationNames;
    }

}
