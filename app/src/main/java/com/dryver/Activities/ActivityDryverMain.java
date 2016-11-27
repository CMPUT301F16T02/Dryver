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


import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.dryver.Adapters.DryverMainAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.ActivityDryverMainState;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.ICallBack;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

import static com.dryver.Models.ActivityDryverMainState.*;


/**
 * This activities deals with providing the driver with UI for requests.
 */
public class ActivityDryverMain extends ActivityLoggedInActionBar implements OnItemSelectedListener {

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

    private Timer timer;
    private ActivityDryverMainState state = ALL;

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

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onResume () {
        super.onResume();
        refreshRequestList();
        setTimer();
    }

    @Override
    public void onPause(){
        Log.i("trace", "ActivityDryverMain.onPause()");
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    /**
     * Assigns all UI elements to the actual views
     */
    private void assignElements(){
        sortSpinner = (Spinner) findViewById(R.id.requestSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activity_driver_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
        searchByEditText = (EditText) findViewById(R.id.searchWith);
        searchByEditText.setInputType(0);
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
     * button, the refresh swipe, and also does some google maps stuff **Maybe maps should be moved**
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
     * does some mappy type stuff
     */
    private void setMapStuff(){
        //========== EXPERIMENTAL CODE ==============
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
        //==============================================
    }

    /**
     * Checks the statuses of the requests the driver is viewing
     */
    public void checkStatuses(){
        if(requestSingleton.getRequests().size() != 0){
            for (Request request : requestSingleton.getRequests()){
                if(request.getStatus() == RequestStatus.PAYMENT_AUTHORIZED){
                    notifyPayment();
                } else if(request.getStatus() == RequestStatus.DRIVER_CHOSEN &&
                        request.getAcceptedDriverID() == userController.getActiveUser().getId()){
                    notifySelected(request);
                }
            }
        }
    }

    /**
     * Notifies if the state of a request that the driver is a part of has payment authorized
     */
    private void notifyPayment(){
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(R.string.complete_message)
                .setTitle(R.string.complete_title)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();
    }

    /**
     * Notifies that the driver has been choses to fulfill the user's request
     * @param request
     */
    private void notifySelected(final Request request){
        new AlertDialog.Builder(ActivityDryverMain.this)
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
                .create()
                .show();
    }

    /**
     * Find the device's current location using location services
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
        }
        else if (sortSelection.equals("Geolocation")) {
            searchByEditText.setHint(R.string.kilometers);
            state = GEOLOCATION;
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
    }

    /**
     * Initializes the location request
     * @param LOCATION_UPDATES
     * @param LOCATION_INTERVAL
     */
    public void initializeLocationRequest(int LOCATION_UPDATES, int LOCATION_INTERVAL) {
        Log.i("trace", "ActivityDryverMain.initializeLocationRequest()");
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