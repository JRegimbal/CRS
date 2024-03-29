package ca.mcgill.hci.crs_application.feature;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class Manage_Location extends CRSActivity {
    private String uuid = null;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_location);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Button confirmButton = findViewById(R.id.buttonLocationConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView locationName = findViewById(R.id.locationName);
                Spinner modeSpinner = findViewById(R.id.manageLocation_mode_spinner);
                if (uuid != null) {
                    if (locationName.getText().toString().trim().length() == 0) {
                        Snackbar.make(v, "Location Cannot Be Empty", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("uuid", uuid);
                        obj.put("name", locationName.getText().toString().trim());
                        obj.put("mode", modeSpinner.getSelectedItem().toString().trim());

                        SavedData.updateOrAddLocation(getApplicationContext(), obj);
                    } catch (Exception e) {
                        Log.e("Manage", e.getMessage());
                    }
                } else {
                    Log.e("Manage", "UUID is null!");
                }

                finish();
                // Intent intent = new Intent(Manage_Location.this, Start_Session.class);
                // startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.buttonDeleteLocation);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uuid != null) {
                    if (SavedData.getNumLocations(v.getContext()) > 1) {
                        SavedData.deleteLocation(v.getContext(), uuid);
                        // Check if this was the current location
                        if (uuid.equals(preferences.getString(getString(R.string.current_location), null))) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(getString(R.string.current_location), null);
                            editor.putString(getString(R.string.override_mode), null);
                            editor.apply();
                        }
                        finish();
                        //Intent intent = new Intent(Manage_Location.this, Start_Session.class);
                        //startActivity(intent);
                    }
                    else
                        Snackbar.make(v, "Cannot Delete Only Location", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Get UUID from calling intent
        uuid = getIntent().getStringExtra("uuid");
        JSONObject location = SavedData.getLocation(this, uuid);
        if (location != null) {
            TextView locationName = findViewById(R.id.locationName);
            try {
                locationName.setText(location.getString("name"));
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage());
            }
        }
    }
}
