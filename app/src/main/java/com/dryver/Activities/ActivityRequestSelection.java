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
import com.dryver.Models.Request;
import com.dryver.Models.Rider;
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
    private TextView dateTextView;
    private TextView statusTextView;
    private Button deleteRequestButton;
    private Button cancelRequestButton;
    private Button viewDriversButton;
    private SimpleDateFormat sdf;
    private Request request;
    private Location fromLocation;
    private Location toLocation;
    private Rider rider;
    private int status;
    private int position;

    private static final String RETURN_VIEW_REQUEST = "com.ubertapp.return_view_request";
    private static final String RETURN_REQUEST_DELETE = "com.ubertapp.return_request_delete";
    private static final int RETURN_DELETE_CODE = 2;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private UserController userController = UserController.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_selection);

        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        request = requestSingleton.getRequests().get(position);

        rider = new Rider(userController.getActiveUser());
        fromLocation = request.getFromLocation();
        toLocation = request.getToLocation();

        titleTextView = (TextView) findViewById(R.id.requestSelectionTitle);
        riderNameTextView = (TextView) findViewById(R.id.requestSelectionRiderName);
        fromLocationTextView = (TextView) findViewById(R.id.requestSelectionFromLocation);
        toLocationTextView = (TextView) findViewById(R.id.requestSelectionToLocation);
        dateTextView = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);
        cancelRequestButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
        deleteRequestButton = (Button) findViewById(R.id.requestSelectionButtonDelete);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);

        titleTextView.setText("Request Details");
        riderNameTextView.setText("Rider Name: " + rider.getFirstName() + " " + rider.getLastName());
        fromLocationTextView.setText("From Coordinates: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        toLocationTextView.setText("To Coordinates: Lat: " + toLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        dateTextView.setText("Request Date: " + sdf.format(request.getDate().getTime()));


        statusTextView.setText("Status: " + request.statusCodeToString());

        deleteRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean deleted = requestSingleton.removeRequest(request);

                while(deleted == null);

                if(deleted){
                    finish();
                }
            }
        });

        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setStatus(request.getStatus() ^ 1);

                if (ES.updateRequest(request)) {
                    Log.e("ERROR", "Request not updated on server correctly");
                }

                statusTextView.setText("Status: " + request.statusCodeToString());
            }
        });

        viewDriversButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO: popup list of drivers?
            }
        });
    }
}
