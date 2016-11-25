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
import com.dryver.Utility.HelpMe;
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
    private TextView locationText;

    private Calendar calendar = Calendar.getInstance();
    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Rider rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        setLocation = (Button) findViewById(R.id.requestButtonLocation);
        submitRequest = (Button) findViewById(R.id.requestButtonSubmit);
        tripPrice = (EditText) findViewById(R.id.requestTripPrice);
        locationText = (TextView) findViewById(R.id.requestLocation);

        timePicker = (TimePicker) findViewById(R.id.requestTimePicker);
        datePicker = (DatePicker) findViewById(R.id.requestDatePicker);
        HelpMe.setTimePicker(calendar, timePicker);
        HelpMe.setDatePicker(calendar, datePicker);

        timePicker.setVisibility(View.INVISIBLE);
        datePicker.setVisibility(View.INVISIBLE);

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRequest.this, ActivityRequestMap.class);
                startActivity(intent);
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HelpMe.isEmptyTextField(tripPrice)) {
                    Double cost = Double.parseDouble(tripPrice.getText().toString());
                    HelpMe.setCalendar(calendar, datePicker, timePicker);

                    requestSingleton.getMakeRequest().setCost(cost);
                    requestSingleton.getMakeRequest().setDate(calendar);
                    requestSingleton.pushMakeRequest();
                    finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HelpMe.formatLocationTextView(requestSingleton.getMakeRequest(), locationText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.setMakeRequest(null);
    }
}
