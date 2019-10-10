package ca.mcgill.hci.crs_application.feature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Manage_Location extends Activity {
    private String uuid = null;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_location);

        preferences = this.getSharedPreferences("CRS", MODE_PRIVATE);

        Button confirmButton = findViewById(R.id.buttonLocationConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView locationName = findViewById(R.id.locationName);
                Spinner modeSpinner = findViewById(R.id.manageLocation_mode_spinner);
                if (uuid != null) {
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
                Intent intent = new Intent(Manage_Location.this, Start_Session.class);
                startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.buttonDeleteLocation);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uuid != null) {
                    SavedData.deleteLocation(v.getContext(), uuid);
                    Intent intent = new Intent(Manage_Location.this, Start_Session.class);
                    startActivity(intent);
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
