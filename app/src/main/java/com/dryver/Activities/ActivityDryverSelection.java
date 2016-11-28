/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.R;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.ICallBack;


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

    private void setListeners() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    private void setDriverStatus() {
        if (requestSingleton.getTempRequest().hasDriver(userController.getActiveUser().getId())) {
            isAcceptedButtonToggle(true);
            statusTextView.setText("Status: Ride is accepted.");
        } else {
            isAcceptedButtonToggle(false);
            statusTextView.setText("Status: Can accept ride.");
        }
    }

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
//        if (requestSingleton.getTempRequest().isAcceptedDriver(userController.getActiveUser().getId()) &&
//                requestSingleton.getTempRequest().getStatus() == RequestStatus.PAYMENT_AUTHORIZED) {
//            acceptButton.setText("Accept Payment");
//            acceptButton.setEnabled(true);
//            acceptButton.setOnClickListener(null);
//            cancelButton.setVisibility(View.INVISIBLE);
//
//            acceptButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    requestSingleton.acceptPayment(new ICallBack() {
//                        @Override
//                        public void execute() {
//                            finish();
//                        }
//                    });
//                }
//            });
//        }

}
