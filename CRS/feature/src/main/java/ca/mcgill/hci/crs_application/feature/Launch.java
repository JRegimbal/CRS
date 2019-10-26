package ca.mcgill.hci.crs_application.feature;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Launch extends CRSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button startReturningUser = findViewById(R.id.buttonReturnUser);
        startReturningUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Launch.this, Returning_User.class);
                startActivity(intent);
            }
        });

        Button startNewUser = findViewById(R.id.buttonNewUser);
        startNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Launch.this, New_User.class);
                startActivity(intent);
            }
        });
    }

}
