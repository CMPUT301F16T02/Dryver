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
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.Models.User;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

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
    private Button cancelButton;
    private Button viewDriversButton;
    private Request request;
    private Location fromLocation;
    private Location toLocation;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private UserController userController = UserController.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_selection);

        request = requestSingleton.getViewedRequest();

        fromLocation = request.getFromLocation();
        toLocation = request.getToLocation();

        fromLocationTextView = (TextView) findViewById(R.id.requestSelectionFromLocation);
        toLocationTextView = (TextView) findViewById(R.id.requestSelectionToLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);

        cancelButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);

        fromLocationTextView.setText("From Coordinates: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        toLocationTextView.setText("To Coordinates: Lat: " + toLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        requestSelectionDate.setText("Request Date: " + HelpMe.getStringDate(request.getDate()));

        statusTextView.setText("Status: " + request.statusCodeToString());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setStatus(RequestStatus.CANCELLED);
                requestSingleton.pushRequest(request);
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
}
