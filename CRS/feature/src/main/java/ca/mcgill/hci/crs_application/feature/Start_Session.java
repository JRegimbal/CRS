package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

public class Start_Session extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start_session);
        super.onCreate(savedInstanceState);

        Button overwriteSession = findViewById(R.id.buttonOverwrite);
        overwriteSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start_Session.this, Overwrite_Session.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if this was opened by a NFC tag
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage message = (NdefMessage) getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
            // Try to parse as UUID even though URI type is known to be UID
            UUID uuid = null;
            try {
                String uuidText = message.getRecords()[0].getPayload().toString();
                uuid = UUID.fromString(uuidText.split(":")[1].trim());
            } catch (Exception e) {
                Log.i("NFC", e.getMessage());
            }
            if (uuid != null) {
                // Check if UUID is in database
                // if in database load location & mode
                // if not open manage_location activity
                Toast.makeText(this, "Loaded UUID: " + uuid.toString(), Toast.LENGTH_LONG);
                Log.i("NFC", "Loaded UUID: " + uuid.toString());
            }
        }
    }

}
