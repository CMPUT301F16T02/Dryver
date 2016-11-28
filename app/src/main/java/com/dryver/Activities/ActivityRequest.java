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

import java.util.Calendar;


/**
 * Activity used to get information from the user for a request they are about to make.
 * Or edit existing requests.
 */
public class ActivityRequest extends Activity {
    private Button setLocation;
    private Button submitRequest;
    private EditText requestDescription;
    private EditText tripPrice;
    private TextView locationText;

    private Calendar calendar = Calendar.getInstance();
    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Rider rider;

    /**
     * Overrides {@link Activity} onCreate methods, sets all relevant event listeners and UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        setLocation = (Button) findViewById(R.id.requestButtonLocation);
        submitRequest = (Button) findViewById(R.id.requestButtonSubmit);
        tripPrice = (EditText) findViewById(R.id.requestTripPrice);
        requestDescription = (EditText) findViewById(R.id.requestDescription);
        locationText = (TextView) findViewById(R.id.requestLocation);


        tripPrice.setText(HelpMe.formatCurrency(requestSingleton.getEstimate()));

        toggleSubmitButton();
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
                    requestSingleton.getTempRequest().setDescription(requestDescription.getText().toString());
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

        requestDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDescription.setText("");
            }
        });
    }

    /**
     * Overrides {@link Activity} onResume to refill UI elements with their temporary data
     */
    @Override
    public void onResume() {
        super.onResume();
        locationText.setText(HelpMe.formatLocation(requestSingleton.getTempRequest()));
        tripPrice.setText(HelpMe.formatCurrency(requestSingleton.getEstimate()));
        requestDescription.setText(requestSingleton.getTempRequest().getDescription());
        toggleSubmitButton();
    }

    /**
     * Overrides {@link Activity} onDestroy to clear temporary request
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    /**
     * Toggles UI element submit button
     */
    private void toggleSubmitButton() {
        if (requestSingleton.getTempRequest().hasRoute()) {
            submitRequest.setEnabled(true);
        } else {
            submitRequest.setEnabled(false);
        }
    }

}
