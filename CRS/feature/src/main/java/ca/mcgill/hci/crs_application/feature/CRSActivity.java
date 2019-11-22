package ca.mcgill.hci.crs_application.feature;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

abstract class CRSActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 424;
    private static final int WRITE_REQUEST_CODE = 42;
    private static final int LOCATION_REQUEST_CODE = 24;
    private static final String mediaType = "*/*";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_guide_item) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.user_guide_url)));
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.import_settings_item) {
            // Check permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_REQUEST_CODE
                        );
            }
            else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(mediaType);
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
            return true;
        }
        else if (item.getItemId() == R.id.export_settings_item) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_REQUEST_CODE
                );
            }
            else {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(mediaType);
                intent.putExtra(Intent.EXTRA_TITLE, "CRS Settings.json");
                startActivityForResult(intent, WRITE_REQUEST_CODE);
            }
            return true;
        }
        else if (item.getItemId() == R.id.wifi_menu_item) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE
                );
            }
            else {
                Intent intent = new Intent(this, WifiSwitchOrAdd.class);
                startActivity(intent);
            }
            return true;
        }
        else if (item.getItemId() == R.id.session_settings_menu_item) {
            Intent intent = new Intent(this, Overwrite_Session.class);
            startActivity(intent);
            return true;
        }
        else {
            Log.d("CRS", String.valueOf(item.getItemId()));
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {
            Uri documentUri = null;
            switch (requestCode) {
                case READ_REQUEST_CODE:
                    if (resultData != null) {
                        documentUri = resultData.getData();
                        try {
                            String text = readTextFromUri(documentUri);
                            JSONObject object = new JSONObject(text);
                            SavedData.writeFileContents(this, object);
                        } catch (Exception e) {
                            Snackbar.make(findViewById(android.R.id.content), "Error reading file! " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;
                case WRITE_REQUEST_CODE:
                    if (resultData != null) {
                        documentUri = resultData.getData();
                        try {
                            JSONObject object = SavedData.getFileContents(this);
                            String text = object.toString(4);
                            writeTextToUri(text, documentUri);
                        } catch (Exception e) {
                            Snackbar.make(findViewById(android.R.id.content), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;
                default:
                    Log.d("Unexpected request code", String.valueOf(requestCode));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(mediaType);
                    startActivityForResult(intent, READ_REQUEST_CODE);
                }
                break;
            case WRITE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(mediaType);
                    intent.putExtra(Intent.EXTRA_TITLE, "CRS Settings");
                    startActivityForResult(intent, WRITE_REQUEST_CODE);
                }
                break;
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, WifiSwitchOrAdd.class);
                    startActivity(intent);
                }
            default:
                break;
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private void writeTextToUri(String text, Uri uri) throws IOException {
        try (OutputStream outputStream =
                getContentResolver().openOutputStream(uri);
             BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Objects.requireNonNull(outputStream)))) {
            writer.write(text);
        }
    }

}
