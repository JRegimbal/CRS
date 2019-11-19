package ca.mcgill.hci.crs_application.feature;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Start_Session extends CRSActivity {

    private JSONObject jsonObject = null;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get adapter for this
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, this.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addDataScheme("uuid");
        IntentFilter[] filters = {
                filter
        };

        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);

        if (SavedData.getNumLocations(this) < 1) {
            setContentView(R.layout.no_location_layout);
        }
        else {
            setContentView(R.layout.activity_start_session);
            Button overwriteSession = findViewById(R.id.buttonOverwrite);
            overwriteSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Start_Session.this, Overwrite_Session.class);
                    startActivity(intent);
                }
            });
            // checkNFCIntent();
            updateForCurrentLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage message = (NdefMessage) intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
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
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.current_location), uuid.toString());
                        if (preferences.contains(getString(R.string.override_mode))) {
                            editor.remove(getString(R.string.override_mode));
                        }
                        editor.commit();
                        // Call manage_location_activity
                        Intent i = new Intent(Start_Session.this, Manage_Location.class);
                        i.putExtra("uuid", uuid.toString());
                        startActivity(i);
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.current_location), uuid.toString());
                        if (preferences.contains(getString(R.string.override_mode))) {
                            editor.remove(getString(R.string.override_mode));
                        }
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
        TextView modeText = findViewById(R.id.mode);
        TextView locationText = findViewById(R.id.location);
        modeText.setText("Mode: " + mode);
        locationText.setText("Location: " + location);
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
