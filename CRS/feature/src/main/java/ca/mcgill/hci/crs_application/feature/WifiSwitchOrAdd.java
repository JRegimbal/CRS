package ca.mcgill.hci.crs_application.feature;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class WifiSwitchOrAdd extends CRSActivity {
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setContentView(R.layout.activity_wifi_switch_or_add);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UUID uuid = getWifiUUID();
        if (uuid == null) {
            Toast.makeText(getApplicationContext(), "Not Connected to WiFi!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            TextView tv = findViewById(R.id.wifiLocationView);
            tv.setText(uuid.toString());
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
