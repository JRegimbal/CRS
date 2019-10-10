package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Start_Session extends AppCompatActivity{

    private JSONObject jsonObject = null;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start_session);
        super.onCreate(savedInstanceState);

        preferences = getPreferences(MODE_PRIVATE);

        Button overwriteSession = findViewById(R.id.buttonOverwrite);
        overwriteSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start_Session.this, Overwrite_Session.class);
                startActivity(intent);
            }
        });
        checkNFCIntent();

        updateForCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNFCIntent();
        updateForCurrentLocation();
    }

    private void checkNFCIntent() {
        // Check if this was opened by a NFC tag
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage message = (NdefMessage) getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
            // Try to parse as UUID even though URI type is known to be UID
            UUID uuid = null;
            try {
                String uuidText = new String(message.getRecords()[0].getPayload());
                uuid = UUID.fromString(uuidText.split(":")[1].trim());
            } catch (Exception e) {
                Log.i("NFC", e.getMessage());
            }
            if (uuid != null) {
                // Check if UUID is in database
                // if in database load location & mode
                // if not open manage_location activity
                jsonObject = SavedData.getFileContents(this);
                try {
                    JSONArray locations = jsonObject.getJSONArray("locations");
                    JSONObject location = null;
                    for (int i = 0; i < locations.length(); i++) {
                        try {
                            JSONObject loc = locations.getJSONObject(i);
                            String id = loc.getString("uuid");
                            if (uuid.toString().equals(id)) {
                                location = loc;
                                break;
                            }
                        } catch (Exception e) {
                            Log.i("Check location", e.getMessage());
                        }
                    }
                    if (location == null) {
                        location = new JSONObject();
                        location.put("uuid", uuid.toString());
                        location.put("mode", "Work");
                        location.put("name", "New Location");
                        locations.put(location);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.current_location), uuid.toString());
                        if (preferences.contains(getString(R.string.override_mode))) {
                            editor.remove(getString(R.string.override_mode));
                        }
                        editor.commit();
                        SavedData.updateOrAddLocation(this, location);
                        // Call manage_location_activity
                        Intent intent = new Intent(Start_Session.this, Manage_Location.class);
                        intent.putExtra("uuid", uuid.toString());
                        startActivity(intent);
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.current_location), uuid.toString());
                        editor.commit();
                    }

                } catch (Exception e) {
                    Log.i("Check location", e.getMessage());
                }
                Log.i("NFC", "Loaded UUID: " + uuid.toString());
            }
        }
    }

    private void setLocationAndModeText(String location, String mode) {
        Button modeButton = findViewById(R.id.mode);
        Button locationButton = findViewById(R.id.location);
        modeButton.setText(mode);
        locationButton.setText(location);
    }

    private void updateForCurrentLocation() {
        if (preferences != null) {
            String locationText = "N/A";
            String modeText = "N/A";
            if (preferences.contains(getString(R.string.current_location))) {
                String uuid = preferences.getString(getString(R.string.current_location), null);
                JSONObject location = SavedData.getLocation(this, uuid);
                if (location != null) {
                    try {
                        locationText = location.getString("name");
                        modeText = location.getString("mode");
                    } catch (JSONException e) {
                        Log.e("JSON", e.getMessage());
                    }
                }
            }
            if (preferences.contains(getString(R.string.override_mode))) {
                modeText = preferences.getString(getString(R.string.override_mode), null);
            }
            setLocationAndModeText(locationText, modeText);
        }
    }

}
