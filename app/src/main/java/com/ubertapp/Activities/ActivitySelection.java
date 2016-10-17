package com.ubertapp.Activities;

import android.app.Activity;
import android.os.Bundle;
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
    }
}
