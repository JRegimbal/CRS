package ca.mcgill.hci.crs_application.feature;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
public class Overwrite_Session extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overwrite_session);

        final String[] spstr = getResources().getStringArray(R.array.location_arrays);
        final Spinner sp = (Spinner)findViewById(R.id.location_spinner);
        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spstr);
        sp.setAdapter(ar);
        sp.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String s=((TextView)view).getText().toString();

                if(!s.equals("No selection"))
                    startActivity(new Intent(view.getContext(), Manage_Location.class));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });


    }

}
