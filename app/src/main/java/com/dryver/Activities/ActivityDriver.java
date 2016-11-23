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
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dryver.Controllers.RequestListAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.R;
import com.dryver.Utility.ICallBack;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * This activities deals with providing the driver with UI for requests.
 */
public class ActivityDriver extends ActivityLoggedInActionBar implements OnItemSelectedListener {

    private ListView requestListView;
    private Button currentLocationButton;
    private Spinner sortSpinner;
    private RequestListAdapter requestListAdapter;
    private Location currentLocation;
    private LocationRequest mLocationRequest;

    private SwipeRefreshLayout swipeContainer;

    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Driver driver;
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        currentLocationButton = (Button) findViewById(R.id.requestButtonCurrentLocation);
        currentLocationButton.setVisibility(View.INVISIBLE);

        sortSpinner = (Spinner) findViewById(R.id.requestSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activity_driver_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);

        driver = new Driver(userController.getActiveUser());
        userController.setActiveUser(driver);

        //TODO: Change this in future
        //sets the request singleton's requests lists to getAllRequests in ES Controller
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);
        requestSingleton.setRequestsAll();
        requestListAdapter = new RequestListAdapter(this, requestSingleton.getRequests());
        requestListView.setAdapter(requestListAdapter);

        setListeners();

    }

    private void setListeners(){
        requestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(ActivityDriver.this, ActivityRequestSelection.class);
                requestSingleton.setViewedRequest((Request)requestListView.getItemAtPosition(position));
                startActivity(intent);
                return true;
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentLocation();
            }
        });

        //========== EXPERIMENTAL CODE ==============
        initializeLocationRequest(100, 100);
        mClient = new GoogleApiClient.Builder(ActivityDriver.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        currentLocationButton.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();
        //==============================================

        //https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerDriver);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    public void findCurrentLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        Log.i("ActivityDriver: ", "CURRENT LOCATION: " + currentLocation);

        //CODE BELOW IS FOR CONTINUOUSLY UPDATING USER LOCATION
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("ActivityDriver: ", "NEW LOCATION: " + location);
            }
        });
    }

    //Depending on the spinner select, requests are sorted according to the selection.
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String sortSelection = parent.getItemAtPosition(pos).toString();
        if (sortSelection.equals("Date")) {
            requestSingleton.sortRequestByDate();
        }
        else if (sortSelection.equals("Distance")) {
            requestSingleton.sortRequestByDistance();
        }
        else if (sortSelection.equals("Cost")) {
            requestSingleton.sortRequestByCost();
        }
        else if (sortSelection.equals("Proximity")) {
            requestSingleton.sortRequestsByProximity(currentLocation);
        }
        requestListAdapter.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void initializeLocationRequest(int LOCATION_UPDATES, int LOCATION_INTERVAL) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(LOCATION_UPDATES);
        mLocationRequest.setInterval(LOCATION_INTERVAL);
    }

    public void beginRefresh() {
        requestSingleton.updateRequests(new ICallBack() {
            @Override
            public void execute() {
                refreshRequestList();
            }
        });
    }

    private void refreshRequestList(){
        Log.i("trace", "ActivityRequestList.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        requestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume () {
        super.onResume();
        refreshRequestList();
    }
}