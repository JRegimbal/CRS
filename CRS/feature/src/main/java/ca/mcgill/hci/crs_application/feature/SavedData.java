package ca.mcgill.hci.crs_application.feature;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class SavedData {
    private final String filename = "CRS User Data.json";

    public JSONObject getFileContents(Context context) {
        File jsonFile = new File(context.getFilesDir(), filename);
        if (jsonFile.exists()) {
            try {
                FileInputStream is = new FileInputStream((jsonFile));
                return new JSONObject(is.toString());
            } catch (FileNotFoundException e) {
                Log.e("File", "File not found. This shouldn't happen.\n" + e.getLocalizedMessage());
            } catch (JSONException e) {
                Log.e("File", e.getMessage());
            }
        }
        return null;
    }

    public boolean writeFileContents(Context context, JSONObject jsonObject) {
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
}
