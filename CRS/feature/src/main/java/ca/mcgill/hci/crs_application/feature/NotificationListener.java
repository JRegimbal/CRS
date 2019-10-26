package ca.mcgill.hci.crs_application.feature;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationListener extends NotificationListenerService {
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i("NotificationListener", "Bind");
        IBinder binder = super.onBind(intent);
        return binder;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn != null) {
            Log.d("NotificationListener", "Package: " + sbn.getPackageName());
            if (getList().contains(sbn.getPackageName())) {
                Log.d("NotificationListener", "Cancel: " + sbn.getPackageName());
                cancelNotification(sbn.getKey());
            }
        }
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn != null) {
            Log.i("NotificationListener", "Removed: " + sbn.getPackageName());
        }
        super.onNotificationRemoved(sbn);
    }

    private ArrayList<String> getList() {
        String location = preferences.getString(getString(R.string.current_location), null);
        ArrayList<String> list = new ArrayList<String>();
        if (location != null) {
            try {
                String overrideMode = preferences.getString(getString(R.string.override_mode), null);
                String mode;
                if (overrideMode != null) {
                    mode = overrideMode;
                } else {
                    JSONObject locObj = SavedData.getLocation(this, location);
                    mode = locObj.getString("mode");
                }
                JSONArray applications = SavedData.getSettings(this, mode);
                for (int i=  0; i < applications.length(); i++) {
                    list.add(applications.getString(i));
                }
            } catch (JSONException e) {
                Log.e("NotificationListener", e.getMessage());
            }
        }
        for (String name : list) {
            Log.d("NotificationListener", name);
        }
        return list;
    }
}
