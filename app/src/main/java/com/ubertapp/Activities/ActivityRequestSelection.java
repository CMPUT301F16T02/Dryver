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

package com.ubertapp.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Controllers.RequestSingleton;
import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import com.ubertapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityRequestSelection extends Activity {

    private TextView requestSelectionTitle;
    private TextView requestSelectionRiderName;
    private TextView requestSelectionFromLocation;
    private TextView requestSelectionToLocation;
    private TextView requestSelectionDate;
    private TextView requestSelectionStatus;
    private Button requestSelectionButtonDelete;
    private Button requestSelectionButtonCancel;
    private Button requestSelectionButtonViewDriver;
    private SimpleDateFormat sdf;
    private Request request;
    private Location fromLocation;
    private Location toLocation;
    private Rider rider;;
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
        setContentView(R.layout.activity_activity_request_selection);


        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        position = (Integer) getIntent().getSerializableExtra(RETURN_VIEW_REQUEST);
        request = requestSingleton.getRequests().get(position);
        status = request.getStatus();

        rider = new Rider(userController.getActiveUser());
        fromLocation = request.getFromLocation();
        toLocation = request.getToLocation();

        requestSelectionTitle = (TextView) findViewById(R.id.requestSelectionTitle);
        requestSelectionRiderName = (TextView) findViewById(R.id.requestSelectionRiderName);
        requestSelectionFromLocation = (TextView) findViewById(R.id.requestSelectionFromLocation);
        requestSelectionToLocation = (TextView) findViewById(R.id.requestSelectionToLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        requestSelectionStatus = (TextView) findViewById(R.id.requestSelectionToStatus);
        requestSelectionButtonCancel = (Button) findViewById(R.id.requestSelectionButtonCancel);
        requestSelectionButtonDelete = (Button) findViewById(R.id.requestSelectionButtonDelete);
        requestSelectionButtonViewDriver = (Button) findViewById(R.id.requestSelectionButtonViewList);

        requestSelectionTitle.setText("Request Details");
        requestSelectionRiderName.setText("Rider Name: " + rider.getFirstName() + " " + rider.getLastName());
        requestSelectionFromLocation.setText("From Coordinates: Lat: " + fromLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        requestSelectionToLocation.setText("To Coordinates: Lat: " + toLocation.getLatitude() + " Long: " + fromLocation.getLongitude());
        requestSelectionDate.setText("Request Date: " + sdf.format(request.getDate().getTime()));


        requestSelectionStatus.setText("Status: " + request.statusCodeToString());

        requestSelectionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.removeRequest(requestSingleton.getRequests().get(position));
                setResult(RETURN_DELETE_CODE);
                finish();
            }
        });

        requestSelectionButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    status = request.getStatus();
                    status ^= 1;

                    request.setStatus(status);
                    ES.updateRequest(request);

                    requestSelectionStatus.setText("Status: " + request.statusCodeToString());
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
