package com.dryver.Activities;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.HelpMe;
import com.dryver.Models.Rider;
import com.dryver.R;

import java.util.Calendar;


/**
 * Activity used to get information from the user for a request they are about to make.
 * Or edit existing requests.
 */
public class ActivityRequest extends Activity {
    private Button setLocation;
    private Button submitRequest;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private EditText tripPrice;
    private TextView fromLocationText;
    private TextView toLocationText;

    private Calendar calendar = Calendar.getInstance();
    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Rider rider;
    private Location testFromLocation = new Location("from");
    private Location testToLocation = new Location("to");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        setLocation = (Button) findViewById(R.id.requestButtonLocation);
        submitRequest = (Button) findViewById(R.id.requestButtonSubmit);
        tripPrice = (EditText) findViewById(R.id.requestTripPrice);
        fromLocationText = (TextView) findViewById(R.id.requestFromLocation);
        toLocationText = (TextView) findViewById(R.id.requestToLocation);

        timePicker = (TimePicker) findViewById(R.id.requestTimePicker);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        datePicker = (DatePicker) findViewById(R.id.requestDatePicker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // TODO: 2016-11-14 Set these locations through the map map.
        // set default locations for now
        testFromLocation.setLatitude(53.523869);
        testFromLocation.setLongitude(-113.526146);
        testToLocation.setLatitude(53.548623);
        testToLocation.setLongitude(-113.506537);

        findViewById(R.id.requestTripPrice).requestFocus();

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRequest.this, ActivityRequestMap.class);
                startActivity(intent);
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new request here.
                if (!HelpMe.isEmptyTextField(tripPrice)) {
                    // TODO: 2016-11-14 limit the number of decimal places to 2
                    Double price = Double.parseDouble(tripPrice.getText().toString());
                    calendar.set(   datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());
                    requestSingleton.addRequest(userController.getActiveUser().getId(), calendar, testFromLocation, testToLocation, price);
                    finish();
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        fromLocationText.setText("From: Lat: " + testFromLocation.getLatitude() + " Long: " + testFromLocation.getLongitude());
        toLocationText.setText("To  : Lat: " + testToLocation.getLatitude() + " Long: " + testToLocation.getLongitude());
    }

    // TODO: 2016-11-14 implement checker. if intent: get request and edit, otherwise: make new request
//    public void checkIntent() {
//        Intent intent = getIntent();
//        if (intent.hasExtra("position")) {
//
//        }
//    }

    // TODO: 2016-11-14 toggle extra options with a button
}
