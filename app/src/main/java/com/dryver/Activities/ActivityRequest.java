package com.dryver.Activities;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.IBooleanCallBack;
import com.dryver.Models.Request;
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
    private Request request;
    private Location testFromLocation = new Location("from");
    private Location testToLocation = new Location("to");

    private Location fromLocation;
    private Location toLocation;

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
        datePicker = (DatePicker) findViewById(R.id.requestDatePicker);

        HelpMe.setTimePicker(calendar, timePicker);
        HelpMe.setDatePicker(calendar, datePicker);

        fromLocation = requestSingleton.getTempFromLocation();
        toLocation = requestSingleton.getTempToLocation();

        this.request = new Request(rider.getId(), calendar, fromLocation, toLocation, 0);
        // TODO: 2016-11-14 Set these locations through the map map.
        // set default locations for now
        testFromLocation.setLatitude(54.523869);
        testFromLocation.setLongitude(-123.526146);
        testToLocation.setLatitude(53.638623);
        testToLocation.setLongitude(-113.506537);

        checkIntent();

        findViewById(R.id.requestTripPrice).requestFocus();

        setListeners();
    }

    /**
     * Sets the event listeners for the set location button's clock and the submit request button's
     * click
     */
    public void setListeners(){
        /*
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRequest.this, ActivityRequestMap.class);
                startActivity(intent);
            }
        });
        */

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HelpMe.isEmptyTextField(tripPrice)) {
                    Double cost = Double.parseDouble(tripPrice.getText().toString());
                    HelpMe.setCalendar(calendar, datePicker, timePicker);
                    request.setCost(cost);
                    request.setDate(calendar);
                    requestSingleton.pushRequest(request);
                    finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fromLocationText.setText("From: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        toLocationText.setText("To  : Lat: " + toLocation.getLatitude() + " Long: " + toLocation.getLongitude());
    }

    // TODO: 2016-11-14 implement checker. if intent: get request and edit, otherwise: make new request

    /**
     * Checks the intent of the ......... <SOMEONE WRITE WTF THIS IS SUPPOSED TO DO>
     */
    public void checkIntent() {
        String intentString;
        Intent intent = getIntent();
        if (intent.hasExtra("requestId")) {
            intentString = intent.getStringExtra("requestId");
            if ((this.request = requestSingleton.getRequestById(intentString)) != null) {
                tripPrice.setText(Double.toString(request.getCost()));
                HelpMe.setTimePicker(request.getDate(), timePicker);
                HelpMe.setDatePicker(request.getDate(), datePicker);
            }
        }
    }

    // TODO: 2016-11-14 toggle extra options with a button
}
