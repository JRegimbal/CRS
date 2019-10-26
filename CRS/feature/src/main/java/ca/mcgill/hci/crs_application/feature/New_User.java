package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URI;
import java.net.URL;

public class New_User extends CRSActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        Button startSession = findViewById(R.id.buttonNewUserStartSession);
        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(New_User.this, Start_Session.class);
                startActivity(intent);
            }
        });

        Button guideButton = findViewById(R.id.userGuideButton);
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.user_guide_url)));
                startActivity(intent);
            }
        });

    }
}