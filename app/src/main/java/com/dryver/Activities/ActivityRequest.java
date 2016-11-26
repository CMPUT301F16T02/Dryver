package com.dryver.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Utility.HelpMe;
import com.dryver.Models.Rider;
import com.dryver.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;


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

        Double cost = new Double(requestSingleton.getEstimate());
        Toast.makeText(this, cost.toString(), Toast.LENGTH_SHORT);
        tripPrice.setText(cost.toString());

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

                    requestSingleton.getTempRequest().setCost(cost);
                    requestSingleton.getTempRequest().setDate(calendar);
                    requestSingleton.pushTempRequest();
                    finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HelpMe.formatLocationTextView(requestSingleton.getTempRequest(), locationText);
        Double price = new Double(requestSingleton.getEstimate());
        tripPrice.setText(price.toString());
        Toast.makeText(this, new Double(price).toString(), Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

}
