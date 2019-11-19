package ca.mcgill.hci.crs_application.feature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class WifiSwitchOrAdd extends CRSActivity {
    private WifiManager wifiManager;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setContentView(R.layout.activity_wifi_switch_or_add);
        MaterialButton cancelButton = findViewById(R.id.wifiCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final UUID uuid = getWifiUUID();
        if (uuid == null) {
            Toast.makeText(getApplicationContext(), "Not Connected to WiFi!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Check if the UUID is known
            MaterialButton confirmButton = findViewById(R.id.wifiConfirmButton);
            TextView tv = findViewById(R.id.wifiLocationView);
            JSONObject location = SavedData.getLocation(this, uuid.toString());
            if (location == null) {
                tv.setText("Unknown Location! Press 'Confirm' to Add.");
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.current_location), uuid.toString());
                        if (preferences.contains(getString(R.string.override_mode))) {
                            editor.remove(getString(R.string.override_mode));
                        }
                        editor.apply();
                        Intent intent = new Intent(view.getContext(), Manage_Location.class);
                        intent.putExtra("uuid", uuid.toString());
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else {
                try {
                    tv.setText("Switch to '" + location.getString("name") + "'?");
                    confirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(getString(R.string.current_location), uuid.toString());
                            if (preferences.contains(getString(R.string.override_mode))) {
                                editor.remove(getString(R.string.override_mode));
                            }
                            editor.apply();
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    Log.d("JSON", e.getMessage());
                }
            }
        }
    }

    private UUID getWifiUUID() {
        WifiInfo info = wifiManager.getConnectionInfo();
        String bssid = info.getBSSID();
        if (bssid != null) {
            UUID uuid = UUID.nameUUIDFromBytes(bssid.getBytes());
            Log.d("BSSID", bssid);
            return uuid;
        }
        return null;
    }
}
