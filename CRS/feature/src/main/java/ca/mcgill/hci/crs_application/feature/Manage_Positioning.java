package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Manage_Positioning extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__positioning);
    }

    public void nfc_mode(View view)
    {
        Intent intent = new Intent(this, Start_Session_NFC.class);
        startActivity(intent);
    }

    public void wifi_mode(View view)
    {
        Intent intent = new Intent(this, Start_Session_Wifi.class);
        startActivity(intent);
    }

}
