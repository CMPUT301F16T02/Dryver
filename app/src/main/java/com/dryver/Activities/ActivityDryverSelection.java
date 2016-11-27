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


public class ActivityDryverSelection extends Activity {

    private TextView riderIdTextView;
    private TextView locationTextView;
    private TextView dryverSelectionDate;
    private TextView statusTextView;
    private TextView requestDescription;

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
        statusTextView = (TextView) findViewById(R.id.dryverSelectionToStatus);
        requestDescription = (TextView) findViewById(R.id.dryverSelectionDescription);

        viewMapButton = (Button) findViewById(R.id.dryverSelectionMapButton);
        acceptButton = (Button) findViewById(R.id.dryverSelectionAcceptButton);
        cancelButton = (Button) findViewById(R.id.dryverSelectionCancelButton);

        riderIdTextView.setText("Rider Username: " + requestSingleton.getTempRequest().getRiderId());
        HelpMe.formatLocationTextView(requestSingleton.getTempRequest(), locationTextView);
        dryverSelectionDate.setText("Request Date: " + HelpMe.getDateString(requestSingleton.getTempRequest().getDate()));
        setListeners();
        setDriverStatus();
    }

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

        //Cancels the request
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    private void setDriverStatus() {
        if (requestSingleton.getTempRequest().hasDriver(userController.getActiveUser().getId()) &&
                requestSingleton.getTempRequest().getStatus() == RequestStatus.DRIVER_CHOSEN) {
            isAcceptedButtonToggle(true);
        } else if((requestSingleton.getTempRequest().getStatus() == RequestStatus.DRIVERS_AVAILABLE ||
                requestSingleton.getTempRequest().getStatus() == RequestStatus.NO_DRIVERS)){
            isAcceptedButtonToggle(false);
        }
        statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());

        if (requestSingleton.getTempRequest().isAcceptedDriver(userController.getActiveUser().getId()) &&
                requestSingleton.getTempRequest().getStatus() == RequestStatus.PAYMENT_AUTHORIZED) {
            acceptButton.setText("Accept Payment");
            acceptButton.setEnabled(true);
            acceptButton.setOnClickListener(null);
            cancelButton.setVisibility(View.INVISIBLE);

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
    }

    private void isAcceptedButtonToggle(boolean bool) {
        acceptButton.setEnabled(!bool);
        cancelButton.setEnabled(bool);
    }
}
