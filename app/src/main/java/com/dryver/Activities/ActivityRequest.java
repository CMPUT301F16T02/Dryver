package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TimePicker;

import com.dryver.Controllers.RequestListAdapter;
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
    private ScrollView scrollView;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private EditText tripPrice;

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
        scrollView = (ScrollView) findViewById(R.id.requestScrollView);
        tripPrice = (EditText) findViewById(R.id.requestTripPrice);

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
                    requestSingleton.addRequest(rider, calendar, testFromLocation, testToLocation, price);
                    finish();
                }
            }
        });
    }
}
