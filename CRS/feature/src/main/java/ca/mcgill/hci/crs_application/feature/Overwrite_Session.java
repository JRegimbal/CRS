package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
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

public class Overwrite_Session extends CRSActivity {
    private SharedPreferences preferences = null;
    private Map<String, String> nameToUUID = null;
    private String selId = null;
    private int selIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overwrite_session);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMenu();
    }

    private void setUpMenu() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        selId = preferences.getString(getString(R.string.current_location), null);

        ArrayList<String> locationNames = getLocationNames();
        ArrayList<String> onlyNames = (ArrayList<String>)locationNames.clone();
        final ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, onlyNames);
        locationNames.add(0, "No selection");
        // final String[] spstr = getResources().getStringArray(R.array.location_arrays);
        final Spinner sp = (Spinner)findViewById(R.id.overwrite_location_spinner);
        final Spinner currentLocSpinner = findViewById(R.id.overwrite_current_location_spinner);
        final Spinner modeSpinner = findViewById(R.id.modespinner);
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

        currentLocSpinner.setAdapter(ar1);
        currentLocSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = ((TextView)view).getText().toString();
                String uuid = nameToUUID.get(s);
                JSONObject obj = SavedData.getLocation(view.getContext(), uuid);
                try {
                    String mode = obj.getString("mode");
                    String[] modes = getResources().getStringArray(R.array.mode_arrays);
                    for (int j = 0; j < modes.length; j++) {
                        if (mode.equals(modes[j])) {
                            modeSpinner.setSelection(j);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.i("JSON", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button manageApplications = findViewById(R.id.manageApplicationButton);
        manageApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Manage_Applications.class);
                intent.putExtra("mode", (String)modeSpinner.getSelectedItem());
                startActivity(intent);
            }
        });

        if (selId != null) {
            JSONObject obj = SavedData.getLocation(this, selId);
            try {
                String mode = obj.getString("mode");
                String[] modes = getResources().getStringArray(R.array.mode_arrays);
                for (int i = 0; i < modes.length; i++) {
                    if (mode.equals(modes[i])) {
                        modeSpinner.setSelection(i);
                        break;
                    }
                }
            } catch (JSONException e) {
                Log.i("JSON", e.getMessage());
            }
        }

        if (selIdx > 0)
            currentLocSpinner.setSelection(selIdx);

        Button confirm = findViewById(R.id.buttonConfirmOverwrite);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = (String)currentLocSpinner.getSelectedItem();
                SharedPreferences.Editor editor = preferences.edit();
                String selUuid = nameToUUID.get(item);
                if (selId == null || !selId.equals(selUuid)) {
                    editor.putString(getString(R.string.current_location), selUuid);
                }

                JSONObject obj = SavedData.getLocation(view.getContext(), selUuid);
                try {
                    String mode = obj.getString("mode");
                    if (!mode.equals(modeSpinner.getSelectedItem().toString())) {
                        editor.putString(getString(R.string.override_mode), modeSpinner.getSelectedItem().toString());
                    } else {
                        editor.remove(getString(R.string.override_mode));
                    }
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                }

                editor.apply();
                finish();
                //Intent intent = new Intent(view.getContext(), Start_Session.class);
                //startActivity(intent);
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
                String uuid = location.getString("uuid");
                if (uuid.equals(selId)) {
                    selIdx = i;
                }
                nameToUUID.put(name, uuid);
            }
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return locationNames;
    }

}
