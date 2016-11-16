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

import io.searchbox.core.Update;

/**
 * The activity responsible for viewing a requests details more closely / inspecting a request.
 * Allows you to cancel requests, as well as view the drivers associated with a request.
 */

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
                Boolean deleted = requestSingleton.removeRequest(request);

                while(deleted == null);

                if(deleted){
                    finish();
                }
            }
        });

        requestSelectionButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = request.getStatus();
                status ^= 1;

                request.setStatus(status);

                ElasticSearchController.UpdateRequestTask updateRequestTask = new ElasticSearchController.UpdateRequestTask();
                updateRequestTask.execute(request);

                try{
                    if(!updateRequestTask.get())
                    {
                        Log.e("ERROR", "Request not updated on server correctly");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                requestSelectionStatus.setText("Status: " + request.statusCodeToString());
            }
        });
    }
}
