/*
 * Copyright (C) 2016
 * Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.dryver.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.Models.Rider;
import com.dryver.Models.User;
import com.dryver.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The activity responsible for viewing a requests details more closely / inspecting a request.
 * Allows you to cancel requests, as well as view the drivers associated with a request.
 */

public class ActivityRequestSelection extends Activity {

    private TextView titleTextView;
    private TextView riderNameTextView;
    private TextView fromLocationTextView;
    private TextView toLocationTextView;
    private TextView requestSelectionDate;
    private TextView statusTextView;
    private Button deleteButton;
    private Button acceptButton;
    private Button cancelButton;
    private Button viewDriversButton;
    private SimpleDateFormat sdf;
    private Request request;
    private Location fromLocation;
    private Location toLocation;
    private User activeUser;
    private Rider rider;
    private RequestStatus status;
    private String userMode;
    private int position;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private UserController userController = UserController.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_selection);

        Intent intent = getIntent();

        request = requestSingleton.getViewedRequest();

        position = intent.getIntExtra("position", 99);
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));
        Log.d("USERNAME: ", request.getRiderId());
        rider = new Rider(ES.getUserByString(request.getRiderId()));

        status = request.getStatus();

        fromLocation = request.getFromLocation();
        toLocation = request.getToLocation();

        //Text View initialization
        titleTextView = (TextView) findViewById(R.id.requestSelectionTitle);
        riderNameTextView = (TextView) findViewById(R.id.requestSelectionRiderName);
        fromLocationTextView = (TextView) findViewById(R.id.requestSelectionFromLocation);
        toLocationTextView = (TextView) findViewById(R.id.requestSelectionToLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);

        //Button initialization
        deleteButton = (Button) findViewById(R.id.requestSelectionButtonDelete);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);

        riderNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userController.viewUserProfile(ES.getUserByString(request.getRiderId()), ActivityRequestSelection.this);
            }
        });

        titleTextView.setText("Request Details");
        riderNameTextView.setText("Rider Name: " + rider.getFirstName() + " " + rider.getLastName());
        fromLocationTextView.setText("From Coordinates: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        toLocationTextView.setText("To Coordinates: Lat: " + toLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        requestSelectionDate.setText("Request Date: " + sdf.format(request.getDate().getTime()));

        statusTextView.setText("Status: " + request.statusCodeToString());
        checkUser();
    }

    public void checkUser() {
        activeUser = userController.getActiveUser();
        if (activeUser instanceof Rider) {
            userMode = "rider";
            cancelButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
            requestButtonRiderListener();
        }
        else if (activeUser instanceof Driver) {
            userMode = "driver";
            acceptButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
            acceptButton.setText("Accept Request");
            deleteButton.setText("View Rider");
            viewDriversButton.setVisibility(View.INVISIBLE);
            requestButtonDriverListener();
        }
        else {
            activeUser = null;
            userMode = null;
            Log.wtf("UHH", "excuse me?");
        }
    }

    public void requestButtonRiderListener() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean deleted = requestSingleton.removeRequest(request);

                while(deleted == null);
                if(deleted){
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setStatus(RequestStatus.CANCELLED);

                if (ES.updateRequest(request)) {
                    Log.e("ERROR", "Request not updated on server correctly");
                }
                statusTextView.setText("Status: " + request.statusCodeToString());
            }
        });

        viewDriversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRequestSelection.this, ActivityDriverList.class);
                startActivity(intent);
            }
        });
    }

    public void requestButtonDriverListener() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.addDriver(activeUser.getId());
                request.setStatus(RequestStatus.DRIVERS_FOUND);
                statusTextView.setText(request.statusCodeToString());
                ES.updateRequest(request);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userController.viewUserProfile(ES.getUserByString(request.getRiderId()), ActivityRequestSelection.this);
            }
        });
    }
}
