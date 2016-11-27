package com.dryver.Activities;


import android.app.Activity;
import android.content.Intent;
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
import com.dryver.Models.Rider;
import com.dryver.R;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.ICallBack;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
        tripPrice.setInputType(0);
        locationText = (TextView) findViewById(R.id.requestLocation);

        tripPrice.setText(getEstimate());

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
                    requestSingleton.getTempRequest().setCost(cost);
                    requestSingleton.getTempRequest().setDate(calendar);
                    requestSingleton.pushTempRequest(new ICallBack(){
                        @Override
                        public void execute(){
                            Log.i("CALLBACK", "Make Request");
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HelpMe.formatLocationTextView(requestSingleton.getTempRequest(), locationText);
        tripPrice.setText(getEstimate());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    /**
     * Formats the price as a 2 float currency
     */
    public String getEstimate() {
        NumberFormat formatter = new DecimalFormat("#.##");
        return formatter.format(requestSingleton.getEstimate());
    }
}
