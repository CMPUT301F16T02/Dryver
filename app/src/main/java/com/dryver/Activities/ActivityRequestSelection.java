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

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.dryver.Utility.ICallBack;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The activity responsible for viewing a requests details more closely / inspecting a request.
 * Allows you to cancel requests, as well as view the drivers associated with a request.
 */

public class ActivityRequestSelection extends Activity {

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

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private UserController userController = UserController.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_selection);

        request = requestSingleton.getViewedRequest();

        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        String rider_name = (activeUser.getFirstName() + " " + activeUser.getLastName()); // Breaks here in offline mode

        fromLocation = request.getFromLocation();
        toLocation = request.getToLocation();

        fromLocationTextView = (TextView) findViewById(R.id.requestSelectionFromLocation);
        toLocationTextView = (TextView) findViewById(R.id.requestSelectionToLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);

        deleteButton = (Button) findViewById(R.id.requestSelectionButtonDelete);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);

        fromLocationTextView.setText("From Coordinates: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        toLocationTextView.setText("To Coordinates: Lat: " + toLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        requestSelectionDate.setText("Request Date: " + sdf.format(request.getDate().getTime()));

        statusTextView.setText("Status: " + request.statusCodeToString());
        checkUserType();
    }

    /**
     * Checks whether the user is a driver or a rider, hides or shows appropriate UI elements and
     * calls the proper listener initialization
     */
    public void checkUserType() {
        activeUser = userController.getActiveUser();
        if (activeUser instanceof Rider) {
            cancelButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
            setRiderListeners();
        } else if (activeUser instanceof Driver) {
            acceptButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
            acceptButton.setText("Accept Request");
            deleteButton.setText("View Rider");
            viewDriversButton.setVisibility(View.INVISIBLE);
            setDriverListeners();
        } else {
            activeUser = null;
            Log.wtf("UHH", "excuse me?");
        }
    }

    /**
     * Sets the appripriate listeners for the rider. This includes: the delete button's click,
     * the cancel button's click, and the view drivers button's click.
     */
    public void setRiderListeners() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.removeRequest(request, new ICallBack() {
                    @Override
                    public void execute() {
                        finish();
                    }
                });
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

    /**
     * sets the appropriate listeners for the driver. These include: the accept button's click
     */
    public void setDriverListeners() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.addDriver(activeUser.getId());
                request.setStatus(RequestStatus.DRIVERS_FOUND);
                statusTextView.setText(request.statusCodeToString());
                ES.updateRequest(request);
            }
        });
    }
}
