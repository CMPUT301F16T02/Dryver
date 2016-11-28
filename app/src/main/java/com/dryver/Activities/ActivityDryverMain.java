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
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dryver.Adapters.DryverMainAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.ActivityDryverMainState;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.ICallBack;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Timer;
import java.util.TimerTask;

import static com.dryver.Models.ActivityDryverMainState.*;


/**
 * This activities deals with providing the driver with UI for requests.
 * Uses Google services for some of the geolocation functionalities
 *
 * Implements {@link ActivityLoggedInActionBar} for some of the UI features
 *
 * @see GoogleApiClient
 * @see RequestSingleton
 * @see UserController
 */
public class ActivityDryverMain extends ActivityLoggedInActionBar implements OnItemSelectedListener {
    private static final LatLngBounds EDMONTON_BOUNDS = new LatLngBounds(new LatLng(53.420980, -113.686921), new LatLng(53.657243, -113.330552));
    private static final int REQUEST_SELECT_PLACE = 0;
    private static final String API_KEY = "AIzaSyCqP3QKEmHTVQ7Tq1NFPNS5Ex28xZSuG2o";

    private ListView driverListView;
    private DryverMainAdapter dryverMainAdapter;
    private Button searchButton;
    private Spinner sortSpinner;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private EditText searchByEditText;
    private SwipeRefreshLayout swipeContainer;

    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Driver driver;
    private GoogleApiClient mClient;

    //11-27-2016 These 2 variables hold the search results for searching by keywords, please decide what to do with them
    //You can access many information regarding the location such as address, coordinates, etc
    private Location searchLocation;
    private String searchAddress;
    private String searchName;


    private ActivityDryverMainState state = ALL;
    private Timer timer;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dryver_main);

        requestSingleton.setRequestsAll();

        driver = new Driver(userController.getActiveUser());
        userController.setActiveUser(driver);

        assignElements();
        setListeners();
        setMapStuff();
        checkStatuses();
    }

    /**
     * Overriding {@link Activity} onStart and connects
     * {@link GoogleApiClient} client instance.
     */
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    /**
     * Overriding {@link Activity} onResume and refreshes the request list
     * to get an updated view
     *
     * Also sets timer for polling
     */
    @Override
    public void onResume () {
        super.onResume();
        refreshRequestList();
        setTimer();
    }

    /**
     * Overriding {@link Activity} onPause and cancels timer
     */
    @Override
    public void onPause(){
        Log.i("trace", "ActivityDryverMain.onPause()");
        super.onPause();
        timer.cancel();
    }

    /**
     * Overriding {@link Activity} onStop and disconnects
     * {@link GoogleApiClient} client instance.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    /**
     * Assigns all UI elements to the actual views and instantiates adapters
     *
     * @see DryverMainAdapter
     * @see ArrayAdapter
     */
    private void assignElements(){
        sortSpinner = (Spinner) findViewById(R.id.requestSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activity_driver_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
        searchByEditText = (EditText) findViewById(R.id.searchWith);
        searchButton = (Button) findViewById(R.id.searchButton);

        //TODO: Change this in future
        //sets the request singleton's requests lists to getAllRequests in ES Controller
        driverListView = (ListView) findViewById(R.id.dryverMainListView);
        //requestSingleton.setRequestsAll();
        dryverMainAdapter = new DryverMainAdapter(this, requestSingleton.getRequests());
        driverListView.setAdapter(dryverMainAdapter);
    }

    /**
     * Sets the action listeners for the long click on request list item, the click of current location
     * button, the refresh swipe.
     */
    private void setListeners(){
        driverListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                Intent intent = new Intent(ActivityDryverMain.this, ActivityDryverSelection.class);
                return true;
            }
        });

        searchButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view){
                requestSingleton.updateDriverRequests(state, new ICallBack() {
                    @Override
                    public void execute() {
                        refreshRequestList();
                    }
                }, searchByEditText);
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

    /**
     * Gets the result from {@link PlaceAutocomplete} intent to allow the driver to select
     * an address for searching
     *
     * @see PlaceAutocomplete
     * @see Place
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PLACE) {
            if(resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                searchLocation = new Location("Search Location");
                searchLocation.setLatitude(place.getLatLng().latitude);
                searchLocation.setLongitude(place.getLatLng().longitude);
                searchAddress = place.getAddress().toString();

                Toast.makeText(ActivityDryverMain.this,"Address: " + searchAddress + " Lat/Long: " + searchLocation.getLatitude() + " " + searchLocation.getLongitude(),
                        Toast.LENGTH_LONG).show();
                searchByEditText.setText(place.getLatLng().latitude + ", " + place.getLatLng().longitude);
            }
        }
    }

    /**
     * Initializes {@link GoogleApiClient} instance and attempts connection and initializes
     * {@link LocationRequest} to find the dryver's current location for searching purposes
     *
     * @see GoogleApiClient
     * @see LocationRequest
     */
    private void setMapStuff(){
        initializeLocationRequest(100, 100);
        mClient = new GoogleApiClient.Builder(ActivityDryverMain.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        findCurrentLocation();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();
    }

    /**
     * Checks the statuses of the requests the driver is viewing
     */
    public void checkStatuses(){
        if(alertDialog != null && alertDialog.isShowing()){
            return;
        } else if(requestSingleton.getRequests().size() != 0){
            for (Request request : requestSingleton.getRequests()){
                if(request.getStatus() == RequestStatus.PAYMENT_AUTHORIZED){
                    notifyPayment(request);
                    break;
                } else if(request.getStatus() == RequestStatus.DRIVER_CHOSEN &&
                        request.getAcceptedDriverID().equals(userController.getActiveUser().getId())){
                    notifySelected(request);
                    break;
                }
            }
        }
    }

    /**
     * Notifies if the state of a request that the driver is a part of has payment authorized
     */
    private void notifyPayment(final Request request){
        alertDialog = new AlertDialog.Builder(ActivityDryverMain.this)
                .setMessage(R.string.complete_message)
                .setTitle(R.string.complete_title)
                .setPositiveButton(R.string.dryver_selected_view, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestSingleton.viewRequest(ActivityDryverMain.this, request);
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        alertDialog.show();
    }

    /**
     * Notifies that the driver has been choses to fulfill the user's request
     * @param request
     */
    private void notifySelected(final Request request){
        alertDialog = new AlertDialog.Builder(ActivityDryverMain.this)
                .setMessage(R.string.dryver_selected_message)
                .setTitle(R.string.dryver_selected_title)
                .setPositiveButton(R.string.dryver_selected_view, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestSingleton.viewRequest(ActivityDryverMain.this, request);
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        alertDialog.show();
    }

    /**
     * Find the device's current location using location services
     *
     * @see LocationRequest
     * @see LocationServices
     * @see com.google.android.gms.location.FusedLocationProviderApi
     */
    public void findCurrentLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        Log.i("ActivityDryverMain: ", "CURRENT LOCATION: " + currentLocation);

        //CODE BELOW IS FOR CONTINUOUSLY UPDATING USER LOCATION
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("ActivityDryverMain: ", "NEW LOCATION: " + location);
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
        Log.i("trace", "ActivityDryverMain.onItemSelected()");
        String sortSelection = parent.getItemAtPosition(pos).toString();
        if (sortSelection.equals("All")) {
            state = ALL;
        }
        else if (sortSelection.equals("Pending")) {
            searchByEditText.setHint(R.string.empty);
            state = PENDING;
        } else if(sortSelection.equals("Active")){
            searchByEditText.setHint(R.string.active);
            state = ACTIVE;
        }else if (sortSelection.equals("Geolocation")) {
            searchByEditText.setHint(R.string.kilometers);
            state = GEOLOCATION;
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_OVERLAY)
                        .setBoundsBias(EDMONTON_BOUNDS)
                        .build(ActivityDryverMain.this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
        }
        else if (sortSelection.equals("Keyword")) {
            searchByEditText.setHint(R.string.keyword);
            state = KEYWORD;
        } else if(sortSelection.equals("Rate")){
            searchByEditText.setHint(R.string.rate);
            state = RATE;
        } else if(sortSelection.equals("Cost")){
            searchByEditText.setHint(R.string.cost);
            state = COST;
        }
    }

    /**
     * handles nothing being selected in the spinner
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto Generated method stub
    }

    /**
     * Initializes the location request
<<<<<<< HEAD
     * @param locationUpdates
     * @param locationInterval
=======
     *
     * @see LocationRequest
     * @param LOCATION_UPDATES
     * @param LOCATION_INTERVAL
>>>>>>> 11857a4feff0ab7de35f3ca582852cc3aad50539
     */
    public void initializeLocationRequest(int locationUpdates, int locationInterval) {
        Log.i("trace", "ActivityDryverMain.initializeLocationRequest()");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(locationUpdates);
        mLocationRequest.setInterval(locationInterval);
    }

    /**
     * Begins refreshing of the request list
     * @see ICallBack
     */
    public void beginRefresh() {
        Log.i("trace", "ActivityDryverMain.beginRefresh()");
        requestSingleton.updateDriverRequests(state, new ICallBack() {
            @Override
            public void execute() {
                refreshRequestList();
            }
        }, searchByEditText);
    }

    /**
     * The method called after data has changed in the request list
     */
    private void refreshRequestList(){
        Log.i("trace", "ActivityDryverMain.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        dryverMainAdapter.notifyDataSetChanged();
    }

    /**
     * Function used to set timer for polling
     */
    private void setTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beginRefresh();
                    }
                });
            }
        }, 0, 30000);//put here time 1000 milliseconds=1 second
    }
}