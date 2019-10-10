package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Returning_User extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returning_user);
        Button startReturningUser = findViewById(R.id.buttonImportProfile);
        startReturningUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Returning_User.this, login.class);
                //startActivity(intent);
                Toast.makeText(v.getContext(), "Importing is not yet implemented!", Toast.LENGTH_LONG).show();
            }
        });

        Button startSession = findViewById(R.id.buttonReturningStartSession);
        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Returning_User.this, Start_Session.class);
                startActivity(intent);
            }
        });

    }

}
