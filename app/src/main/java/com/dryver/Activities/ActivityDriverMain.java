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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.dryver.Controllers.DriverListAdapter;
import com.dryver.Controllers.RequestListAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.ICallBack;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * This activities deals with providing the driver with UI for requests.
 */
public class ActivityDriverMain extends ActivityLoggedInActionBar implements OnItemSelectedListener {

    private ListView driverListView;
    private DriverListAdapter driverListAdapter;

    private Button currentLocationButton;
    private Spinner sortSpinner;
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

        requestSingleton.setRequestsAll();

        driver = new Driver(userController.getActiveUser());
        userController.setActiveUser(driver);

        assignElements();
        setListeners();
        setMapStuff();
        checkStatuses();
    }

    private void assignElements(){
        sortSpinner = (Spinner) findViewById(R.id.requestSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activity_driver_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);

        currentLocationButton = (Button) findViewById(R.id.requestButtonCurrentLocation);
        currentLocationButton.setVisibility(View.INVISIBLE);
        //TODO: Change this in future
        //sets the request singleton's requests lists to getAllRequests in ES Controller
        driverListView = (ListView) findViewById(R.id.requestListViewRequest);

        driverListAdapter = new DriverListAdapter(this, requestSingleton.getRequests());
        driverListView.setAdapter(driverListAdapter);
    }

    /**
     * Sets the action listeners for the long click on request list item, the click of current location
     * button, the refresh swipe, and also does some google maps stuff **Maybe maps should be moved**
     */
    private void setListeners(){
        driverListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                Intent intent = new Intent(ActivityDriverMain.this, ActivityDriverSelection.class);
                return true;
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentLocation();
            }
        });

        //https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerDriver);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    private void setMapStuff(){
        //========== EXPERIMENTAL CODE ==============
        initializeLocationRequest(100, 100);
        mClient = new GoogleApiClient.Builder(ActivityDriverMain.this)
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
    }

    public void checkStatuses(){
        if(requestSingleton.getRequests().size() != 0){
            for (Request request : requestSingleton.getRequests()){
                if(request.getStatus() == RequestStatus.COMPLETE){
                    notifyComplete();
                } else if(request.getStatus() == RequestStatus.DRIVER_SELECTED &&
                        request.getAcceptedDriverID() == userController.getActiveUser().getId()){
                    notifySelected();
                }

            }
        }
    }

    private void notifyComplete(){
        //popup
    }

    private void notifySelected(){
        //popup
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

    /**
     * Find the device's current location using location services
     */
    public void findCurrentLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        Log.i("ActivityDriverMain: ", "CURRENT LOCATION: " + currentLocation);

        //CODE BELOW IS FOR CONTINUOUSLY UPDATING USER LOCATION
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("ActivityDriverMain: ", "NEW LOCATION: " + location);
            }
        });
    }

    //Depending on the spinner select, requests are sorted according to the selection.

    /**
     * Handles selection of various selections in the sort by spinner
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
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
        driverListAdapter.notifyDataSetChanged();
    }

    /**
     * handles nothing being selected in the spinner
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Initializes the location request
     * @param LOCATION_UPDATES
     * @param LOCATION_INTERVAL
     */
    public void initializeLocationRequest(int LOCATION_UPDATES, int LOCATION_INTERVAL) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(LOCATION_UPDATES);
        mLocationRequest.setInterval(LOCATION_INTERVAL);
    }

    /**
     * Begins refreshing of the request list
     * @see ICallBack
     */
    public void beginRefresh() {
        requestSingleton.updateRequests(new ICallBack() {
            @Override
            public void execute() {
                refreshRequestList();
            }
        });
    }

    /**
     * The method called after data has changed in the request list
     */
    private void refreshRequestList(){
        Log.i("trace", "ActivityRiderMain.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        driverListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume () {
        super.onResume();
        refreshRequestList();
    }
}