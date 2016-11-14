package com.dryver.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.dryver.R;

/**
 * Activity used to get information from the user for a request they are about to make.
 * Or edit existing requests.
 */
public class ActivityRequest extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
    }
}
