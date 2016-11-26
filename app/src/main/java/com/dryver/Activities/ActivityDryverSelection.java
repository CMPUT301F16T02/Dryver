package com.dryver.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.R;
import com.dryver.Utility.HelpMe;


public class ActivityDryverSelection extends Activity {

    private TextView riderIdTextView;
    private TextView locationTextView;
    private TextView dryverSelectionDate;
    private TextView statusTextView;
    private TextView requestDescription;

    private Button acceptButton;
    private Button cancelButton;

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

        acceptButton = (Button) findViewById(R.id.dryverSelectionAcceptButton);
        cancelButton = (Button) findViewById(R.id.dryverSelectionCancelButton);

        riderIdTextView.setText("Rider Username: " + requestSingleton.getTempRequest().getRiderId());
        HelpMe.formatLocationTextView(requestSingleton.getTempRequest(), locationTextView);
        dryverSelectionDate.setText("Request Date: " + HelpMe.getDateString(requestSingleton.getTempRequest().getDate()));
        setListeners();
        setDriverStatus();
    }

    private void setListeners(){
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().addDriver(userController.getActiveUser().getId());
                requestSingleton.pushTempRequest();
                setDriverStatus();
            }
        });

        //Cancels the request
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().removeDriver(userController.getActiveUser().getId());
                requestSingleton.pushTempRequest();
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
        if (requestSingleton.getTempRequest().hasDriver(userController.getActiveUser().getId())) {
            statusTextView.setText("Status: " + "Pending Rider confirmation");
            isAcceptedButtonToggle(true);
        } else {
            statusTextView.setText("Status: " + "Can Accept");
            isAcceptedButtonToggle(false);
        }

        if (requestSingleton.getTempRequest().isAcceptedDriver(userController.getActiveUser().getId())) {
            acceptButton.setText("Finalize Ride");
            acceptButton.setEnabled(true);
            acceptButton.setOnClickListener(null);
            cancelButton.setVisibility(View.INVISIBLE);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 2016-11-26 handle finalized acceptance between driver and user.
                }
            });


        }
    }

    private void isAcceptedButtonToggle(boolean bool) {
        acceptButton.setEnabled(!bool);
        cancelButton.setEnabled(bool);

    }
}
