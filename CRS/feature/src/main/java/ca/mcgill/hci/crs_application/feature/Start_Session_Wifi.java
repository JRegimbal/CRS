package ca.mcgill.hci.crs_application.feature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Start_Session_Wifi extends CRSActivity {

    private JSONObject jsonObject = null;
    private SharedPreferences preferences = null;
    private WifiManager wifiMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__session__wifi);
        checkWifi();
    }

    private void checkWifi()
    {
        wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr != null)
        {
            Intent intent = new Intent(this, Start_Session_Wifi.class);
            startActivity(intent);
            if (wifiMgr.isWifiEnabled()) // WiFi adapter is ON
            {
                // pop up "No Wi-Fi Connection"
                startActivity(getParentActivityIntent());
            }else
            {
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                if (wifiInfo.getBSSID() == null) // no Wifi connected
                {
                    // pop up "No Wi-Fi Connection"
                } else {
                    String bssid = wifiInfo.getBSSID();
                    //checkWifiIntent(bssid);
                    //updateForCurrentLocation();
                }
            }
        }else
        {
            startActivity(getParentActivityIntent());
        }
    }

    private void checkWifiIntent(String bssid)
    {
        // Check if BSSID is in database
        // if in database load location & mode
        // if not open manage_location activity
        jsonObject = SavedData.getFileContents(this);
        /*
        try {
            JSONArray locations = jsonObject.getJSONArray("locations");
            JSONObject location = null;
            for (int i = 0; i < locations.length(); i++) {
                try {
                    JSONObject loc = locations.getJSONObject(i);
                    String id = loc.getString("uuid");
                    if (bssid.equals(id)) {
                        location = loc;
                        break;
                    }
                } catch (Exception e) {
                    Log.i("Check location", e.getMessage());
                }
            }
            if (location == null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.current_location), bssid);
                if (preferences.contains(getString(R.string.override_mode))) {
                    editor.remove(getString(R.string.override_mode));
                }
                editor.commit();
                // Call manage_location_activity
                Intent intent = new Intent(this, Manage_Location.class);
                intent.putExtra("uuid", bssid);
                startActivity(intent);
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.current_location), bssid);
                if (preferences.contains(getString(R.string.override_mode))) {
                    editor.remove(getString(R.string.override_mode));
                }
                editor.commit();
            }

        } catch (Exception e) {
            Log.i("Check location", e.getMessage());
        }
        Log.i("NFC", "Loaded UUID: " + bssid);
        */
    }

    public void startSession(View view)
    {
        Intent intent = new Intent(this, During_Session.class);
        String loc = findViewById(R.id.location_wifi).toString();
        String mode = findViewById(R.id.mode_wifi).toString();
        intent.putExtra("location", loc);
        intent.putExtra("location", mode);
        startActivity(intent);
    }

    public void overwriteSession(View view)
    {
        Intent intent = new Intent(this, Overwrite_Session.class);
        startActivity(intent);
    }

    private void setLocationAndModeText(String location, String mode) {
        TextView modeText = findViewById(R.id.mode_wifi);
        TextView locationText = findViewById(R.id.location_wifi);
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
