package ca.mcgill.hci.crs_application.feature;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.button.MaterialButton;

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
        String overrideMode = preferences.getString(getString(R.string.override_mode), null);

        ArrayList<String> locationNames = getLocationNames();
        final ArrayList<String> onlyNames = (ArrayList<String>)locationNames.clone();

        final RadioGroup modeGroup = findViewById(R.id.modeGroup);

        final ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, onlyNames);
        locationNames.add(0, "No selection");
        final Spinner currentLocSpinner = findViewById(R.id.overwrite_current_location_spinner);
        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, locationNames);

        MaterialButton manageLocationButton = findViewById(R.id.manageLocationButton);
        manageLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select a Location to Manage");
                final Intent intent = new Intent(view.getContext(), Manage_Location.class);
                final String values[] = onlyNames.toArray(new String[onlyNames.size()]);
                builder.setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = values[i];
                        String uuid = nameToUUID.get(name);
                        intent.putExtra("uuid", uuid);
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Set up location override spinner
        currentLocSpinner.setAdapter(locationArrayAdapter);
        currentLocSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = ((TextView)view).getText().toString();
                String uuid = nameToUUID.get(s);
                JSONObject obj = SavedData.getLocation(view.getContext(), uuid);
                try {
                    String mode = obj.getString("mode");
                    RadioButton button = null;
                    switch (mode) {
                        case "Work":
                            button = findViewById(R.id.workRadioButton);
                            break;
                        case "Social":
                            button = findViewById(R.id.socialRadioButton);
                            break;
                        case "Relax":
                            button = findViewById(R.id.relaxRadioButton);
                            break;
                        default:
                            Log.i("Mode", mode);
                    }
                    if (button != null) {
                        button.toggle();
                    }
                } catch (JSONException e) {
                    Log.i("JSON", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Manage Applications button
        Button manageApplications = findViewById(R.id.manageApplicationButton);
        manageApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Manage_Applications.class);
                RadioButton selected = findViewById(modeGroup.getCheckedRadioButtonId());
                intent.putExtra("mode", selected.getText().toString());
                startActivity(intent);
            }
        });

        // Set up presets
        if (selId != null) {
            JSONObject obj = SavedData.getLocation(this, selId);
            try {
                String mode = (overrideMode != null ? overrideMode : obj.getString("mode"));
                RadioButton button = null;
                switch (mode) {
                    case "Work":
                        button = findViewById(R.id.workRadioButton);
                        break;
                    case "Social":
                        button = findViewById(R.id.socialRadioButton);
                        break;
                    case "Relax":
                        button = findViewById(R.id.relaxRadioButton);
                        break;
                    default:
                        Log.i("Mode", mode);
                }
                if (button != null) {
                    button.toggle();
                }
            } catch (JSONException e) {
                Log.i("JSON", e.getMessage());
            }
        } else {
            Log.e("SelId", "SelId is null");
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
                    RadioButton selected = findViewById(modeGroup.getCheckedRadioButtonId());
                    if (!mode.equals(selected.getText().toString())) {
                        editor.putString(getString(R.string.override_mode), selected.getText().toString());
                    } else {
                        editor.remove(getString(R.string.override_mode));
                    }
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                }

                editor.apply();
                finish();
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
