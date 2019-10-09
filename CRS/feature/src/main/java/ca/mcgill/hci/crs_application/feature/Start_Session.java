package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

}
