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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.ICallBack;

/**
 * The activity responsible for viewing a requests details more closely / inspecting a request.
 * Allows you to cancel requests, as well as view the drivers associated with a request.
 */

public class ActivityRyderSelection extends Activity {

    private TextView locationTextView;
    private TextView requestSelectionDate;
    private TextView statusTextView;

    private Button cancelButton;
    private Button viewDriversButton;
    private Button deleteButton;

    private RatingBar ratingBar;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ryder_selection);

        locationTextView = (TextView) findViewById(R.id.requestSelectionLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);
        cancelButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
        deleteButton = (Button) findViewById(R.id.requestSelectionButtonDelete);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);

        isCancelled();

        locationTextView.setText(HelpMe.formatLocation(requestSingleton.getTempRequest()));
        requestSelectionDate.setText("Request Date: " + HelpMe.getDateString(requestSingleton.getTempRequest().getDate()));
        setStatusTextView();

        configureUI();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    private void configureUI() {
        cancelButton.setEnabled(true);
        deleteButton.setEnabled(true);
        viewDriversButton.setEnabled(false);
        viewDriversButton.setText("View Drivers");
        ratingBar.setVisibility(View.GONE);

        //Clicking this opens the driver list through the controller
        if (requestSingleton.getTempRequest().getStatus() == RequestStatus.DRIVERS_AVAILABLE) {
            viewDriversButton.setEnabled(true);
            viewDriversButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestSingleton.viewRequestDrivers(ActivityRyderSelection.this, requestSingleton.getTempRequest());
                }
            });
        } else if (requestSingleton.getTempRequest().getStatus() == RequestStatus.DRIVER_CHOSEN) {
            cancelButton.setEnabled(false);
            deleteButton.setEnabled(false);

            viewDriversButton.setEnabled(true);
            viewDriversButton.setText("Authorize Payment");
            viewDriversButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewDriversButton.setEnabled(false);
                    setStatusTextView();
                    requestSingleton.authorizePayment(new ICallBack() {
                        @Override
                        public void execute() {
                            finish();
                        }
                    });
                }
            });
        } else if (requestSingleton.getTempRequest().getStatus() == RequestStatus.PAYMENT_ACCEPTED) {
            ratingBar.setVisibility(View.VISIBLE);
            cancelButton.setEnabled(false);

            viewDriversButton.setEnabled(true);
            viewDriversButton.setText("Send Rating");
            viewDriversButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewDriversButton.setEnabled(false);
                    setStatusTextView();
                    requestSingleton.sendRating(new ICallBack() {
                        @Override
                        public void execute() {
                            ratingBar.setEnabled(false);
                            viewDriversButton.setEnabled(false);
                        }
                    }, ratingBar.getRating());
                }
            });
        }
    }

    private void setListeners() {
        //Cancels the request
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().setStatus(RequestStatus.CANCELLED);
                requestSingleton.pushTempRequest(new ICallBack() {
                    @Override
                    public void execute() {
                        finish();
                    }
                });
                isCancelled();
                statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
            }
        });

        //Deletes the request
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.removeRequest(requestSingleton.getTempRequest());
                statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
                finish();
            }
        });
    }

    /**
     * Checks if the request is already cancelled.
     */
    private void isCancelled() {
        if (requestSingleton.getTempRequest().getStatus().equals(RequestStatus.CANCELLED)) {
            cancelButton.setEnabled(false);
        }
    }

    private void setStatusTextView() {
        statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
    }
}
