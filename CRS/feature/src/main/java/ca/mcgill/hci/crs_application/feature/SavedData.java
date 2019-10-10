package ca.mcgill.hci.crs_application.feature;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

class SavedData {
    private static final String filename = "CRS User Data.json";

    public static JSONObject getFileContents(Context context) {
        File jsonFile = new File(context.getFilesDir(), filename);
        if (jsonFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                String line = reader.readLine();
                StringBuffer contents = new StringBuffer();
                while (line != null) {
                    contents.append(line).append("\n");
                    line = reader.readLine();
                }
                return new JSONObject(contents.toString());
            } catch (FileNotFoundException e) {
                Log.e("File", "File not found. This shouldn't happen.\n" + e.getLocalizedMessage());
            } catch (JSONException e) {
                Log.e("File", e.getMessage());
            } catch (IOException e) {
                Log.e("File", e.getMessage());
            }
        }
        return buildNewJSON();
    }

    public static boolean writeFileContents(Context context, JSONObject jsonObject) {
        try {
            String jsonText = jsonObject.toString(4);
            FileOutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);
            os.write(jsonText.getBytes());
            return true;
        } catch (JSONException e) {
            Log.e("File", "Exception making jsonText. I dont' know how this could happen?");
        } catch (FileNotFoundException e) {
            Log.e("File", e.getMessage());
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        }
        return false;
    }

    public static void updateOrAddLocation(Context context, JSONObject location) throws Exception {
        if (location.has("uuid") && location.has("name") && location.has("mode")) {
            JSONObject contents = getFileContents(context);
            JSONArray locations = contents.getJSONArray("locations");
            JSONObject obj = null;
            int i;
            for (i = 0; i < locations.length(); i++) {
                JSONObject temp = locations.getJSONObject(i);
                if (location.getString("uuid").equals(temp.getString("uuid"))) {
                    obj = temp;
                    break;
                }
            }
            if (obj != null) {
                locations.put(i, location);
            } else {
                locations.put(location);
            }
            contents.put("locations", locations);
            writeFileContents(context, contents);
        } else {
            throw new Exception("Location does not have required fields.");
        }
    }

    public static JSONObject getLocation(Context context, String uuid) {
        JSONObject location = null;
        try {
            JSONObject contents = getFileContents(context);
            JSONArray locations = contents.getJSONArray("locations");
            for (int i = 0; i < locations.length(); i++) {
                JSONObject temp = locations.getJSONObject(i);
                if (uuid.equals(temp.getString("uuid"))) {
                    location = temp;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("SavedData.getLocation", e.getMessage());
        }
        return location;
    }

    public static void deleteLocation(Context context, String uuid) {
        try {
            JSONObject contents = getFileContents(context);
            JSONArray locations = contents.getJSONArray("locations");
            JSONArray newLocations = new JSONArray();
            for (int i = 0; i < locations.length(); i++) {
                JSONObject temp = locations.getJSONObject(i);
                if (!uuid.equals(temp.getString("uuid"))) {
                    newLocations.put(temp);
                }
            }
            contents.put("locations", newLocations);
            writeFileContents(context, contents);
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
    }

    private static JSONObject buildNewJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("locations", new JSONArray());
        } catch (Exception e) {
            Log.e("File", e.getMessage());
        }
        return obj;
    }
}
