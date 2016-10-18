package com.ubertapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ubertapp.R;

/**
 * Selection screen which provides the user with a choice to choose if he'd like to be a rider or a driver.
 */
public class ActivitySelection extends Activity {

    private Button driverButton;
    private Button requestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        driverButton = (Button) findViewById(R.id.driver_button);
        requestButton = (Button) findViewById(R.id.request_button);

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySelection.this, ActivityDriver.class);
                ActivitySelection.this.startActivity(intent);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySelection.this, ActivityRiderRequest.class);
                ActivitySelection.this.startActivity(intent);
            }
        });
    }
}
