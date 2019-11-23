package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Manage_Applications extends CRSActivity {
    private PackageManager pm;
    private String mode;
    private ArrayList<String> settingsToWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__applications);

        String label = getSupportActionBar().getTitle().toString();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pm = getPackageManager();
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        getSupportActionBar().setTitle(label + mode);

        updateAppList();

        Button confirmButton = findViewById(R.id.appMangeConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray array = new JSONArray();
                    for (String entry : settingsToWrite) {
                        array.put(entry);
                    }
                    SavedData.writeSettings(v.getContext(), mode, array);
                } catch (Exception e) {
                    Log.e("Write Settings", e.getMessage());
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAppList();

    }

    private void updateAppList() {
        List<ApplicationInfo> applications = getInstalledApplications();
        LinearLayout appLayout = findViewById(R.id.applicationsList);
        appLayout.removeAllViews();

        try {
            JSONArray modeSettings = SavedData.getSettings(this, mode);
            ArrayList<String> settingsList = new ArrayList<String>();
            for (int i = 0; i < modeSettings.length(); i++) {
                settingsList.add(modeSettings.getString(i));
            }
            settingsToWrite = (ArrayList<String>)settingsList.clone();

            for (ApplicationInfo application : applications) {
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);


                boolean checked = !settingsList.contains(application.packageName);

                CheckBox checkbox = new CheckBox(this);
                //checkbox.setText(application.packageName);
                checkbox.setText(application.loadLabel(pm).toString());
                checkbox.setHint(application.packageName);
                checkbox.setChecked(checked);
                checkbox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String name = compoundButton.getHint().toString();
                        if (b) {
                            settingsToWrite.remove(name);
                        } else {
                            if (!settingsToWrite.contains(name)) {
                                settingsToWrite.add(name);
                            }
                        }
                    }
                });
                layout.addView(checkbox);
                appLayout.addView(layout);
            }

        } catch (JSONException e) {
            Log.e("updateAppList", e.getMessage());
        }
    }

    private List<ApplicationInfo> getInstalledApplications() {
        List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (int i = 0; i < list.size(); i++) {
            ApplicationInfo info = list.get(i);
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                list.remove(info);
            }
        }
        Collections.sort(list, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo a, ApplicationInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
        return list;
       /* for (ApplicationInfo info : applications) {
            Drawable icon = pm.getApplicationIcon(info);
            Log.d("App", info.packageName);
        }*/
    }
}
