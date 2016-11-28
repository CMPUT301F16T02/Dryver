package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.ICallBack;

/**
 * Activity that displays all of the data relevant to the request a driver has selected
 *
 * @see RequestSingleton
 * @see UserController
 */
public class ActivityDryverSelection extends Activity {

    private TextView riderIdTextView;
    private TextView locationTextView;
    private TextView dryverSelectionDate;
    private TextView requestDescription;
    private TextView statusTextView;

    private Button acceptButton;
    private Button cancelButton;
    private Button viewMapButton;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dryver_selection);

        riderIdTextView = (TextView) findViewById(R.id.dryverSelectionRiderID);
        locationTextView = (TextView) findViewById(R.id.dryverSelectionLocation);
        dryverSelectionDate = (TextView) findViewById(R.id.dryverSelectionDate);
        requestDescription = (TextView) findViewById(R.id.dryverSelectionDescription);
        statusTextView = (TextView) findViewById(R.id.dryverSelectionToStatus);

        viewMapButton = (Button) findViewById(R.id.dryverSelectionMapButton);
        acceptButton = (Button) findViewById(R.id.dryverSelectionAcceptButton);
        cancelButton = (Button) findViewById(R.id.dryverSelectionCancelButton);

        requestDescription.setText("Description: " + requestSingleton.getTempRequest().getDescription());

        riderIdTextView.setText("Rider Username: " + requestSingleton.getTempRequest().getRiderId());
        locationTextView.setText(HelpMe.formatLocation(requestSingleton.getTempRequest()));
        dryverSelectionDate.setText("Request Date: " + HelpMe.getDateString(requestSingleton.getTempRequest().getDate()));
        setListeners();
        setDriverStatus();
    }

    /**
     *  Sets all UI (Button and textview) listeners for this activity
     */
    private void setListeners(){
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDryverSelection.this, ActivityDryverMap.class);
                startActivity(intent);
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().addDriver(userController.getActiveUser().getId());
                requestSingleton.pushTempRequest(new ICallBack() {
                    @Override
                    public void execute() {
                        finish();
                    }
                });
                setDriverStatus();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().removeDriver(userController.getActiveUser().getId());
                requestSingleton.pushTempRequest(new ICallBack() {
                    @Override
                    public void execute() {
                        finish();
                    }
                });
                setDriverStatus();
            }
        });

        riderIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016-11-27 set a view rider option.
            }
        });
    }

    /**
     * Overriding {@link Activity} onDestroy and clears {@link RequestSingleton}
     * temporary requests
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    /**
     * Sets the driver status by checking {@link RequestSingleton} temporary request
     * and updates the UI accordingly
     */
    private void setDriverStatus() {
        if (requestSingleton.getTempRequest().hasDriver(userController.getActiveUser().getId())) {
            isAcceptedButtonToggle(true);
            statusTextView.setText("Status: Ride is accepted.");
        } else {
            isAcceptedButtonToggle(false);
            statusTextView.setText("Status: Can accept ride.");
        }

        if (requestSingleton.getTempRequest().isAcceptedDriver(userController.getActiveUser().getId())) {
            acceptButton.setText("Accept Payment");
            acceptButton.setOnClickListener(null);
            cancelButton.setVisibility(View.INVISIBLE);
            statusTextView.setText("Status: You are the driver.");

            if (requestSingleton.getTempRequest().getStatus().equals(RequestStatus.DRIVER_CHOSEN)) {

            } else if (requestSingleton.getTempRequest().getStatus().equals(RequestStatus.PAYMENT_AUTHORIZED)) {
                acceptButton.setEnabled(true);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestSingleton.acceptPayment(new ICallBack() {
                            @Override
                            public void execute() {
                                finish();
                            }
                        });
                    }
                });
            }
        } else if (requestSingleton.getTempRequest().getAcceptedDriverID() != null) {
            acceptButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            statusTextView.setText("Status: Ride has a driver.");
        }
    }

    /**
     * Enables accept and cancel buttons
     *
     * @param bool
     */
    private void isAcceptedButtonToggle(boolean bool) {
        acceptButton.setEnabled(!bool);
        cancelButton.setEnabled(bool);
    }
        // TODO: 2016-11-27 should be a new class.
//        if (requestSingleton.getTempRequest().hasDriver(userController.getActiveUser().getId()) &&
//                requestSingleton.getTempRequest().getStatus().equals(RequestStatus.DRIVER_CHOSEN)) {
//            isAcceptedButtonToggle(true);
//        } else if((requestSingleton.getTempRequest().getStatus() == RequestStatus.DRIVERS_AVAILABLE ||
//                requestSingleton.getTempRequest().getStatus().equals(RequestStatus.NO_DRIVERS))){
//            isAcceptedButtonToggle(false);
//        }
////        statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
//
//

}
