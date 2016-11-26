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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.dryver.Controllers.RequestMainAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.RequestStatus;
import com.dryver.Utility.ICallBack;
import com.dryver.Models.Request;
import com.dryver.Models.Rider;
import com.dryver.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * The activity that acts as the main rider activity. Lists requests, you can create requests here,
 * and select requests to inspect a request.
 */

public class ActivityRyderMain extends ActivityLoggedInActionBar {

    private Button addRequestButton;
    private ListView requestListView;
    private RequestMainAdapter requestMainAdapter;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private UserController userController = UserController.getInstance();

    private SwipeRefreshLayout swipeContainer;

    private Rider rider;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("info", "ActivityRyderMain.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ryder_main);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        assignElements();
        setListeners();
        checkStatuses();
        setTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("info", "ActivityRyderMain.onResume()");
        super.onResume();
        refreshRequestList();
    }

    /**
     * Sets the listeners for the add request button's click and the long click of the request list's
     * items
     */
    public void setListeners(){
        addRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.editRequest(ActivityRyderMain.this, new Request(rider.getId(), Calendar.getInstance()));
            }
        });

        requestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                requestSingleton.editRequest(ActivityRyderMain.this, (Request) requestListView.getItemAtPosition(position));
                return true;
            }
        });

        //https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    /**
     * Assigns the elements that are held in the UI that will be accessed or used later.
     */
    public void assignElements(){
        addRequestButton = (Button) findViewById(R.id.requestButtonNewRequest);
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);

        requestMainAdapter = new RequestMainAdapter(this, requestSingleton.getUpdatedRequests());
        requestListView.setAdapter(requestMainAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerRider);
    }

    /**
     * Sets correct background colors for listview items based on the status of the request.
     */
    public void checkStatuses(){
        if(requestSingleton.getRequests().size() != 0){
            for (Request request : requestSingleton.getRequests()){
                if(request.getStatus() == RequestStatus.DRIVERS_AVAILABLE){
                    requestListView.getChildAt(requestMainAdapter.getPosition(request));
                    notifyDriversAvailable(request);
                }
                else if(request.getStatus() == RequestStatus.PAYMENT_ACCEPTED){
                    requestListView.getChildAt(requestMainAdapter.getPosition(request));
                    notifyDriversAvailable(request);
                }
            }
        }
    }

    /**
     * Notifies the Rider if any drivers have accepted their request offer. Handshake request accepted
     * @param request
     */
    private void notifyDriversAvailable(final Request request){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.drivers_found_message)
                .setTitle(R.string.drivers_found_title);

        builder.setPositiveButton(R.string.drivers_found_view, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestSingleton.viewRequestDrivers(ActivityRyderMain.this, request);
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
    }

    /**
     * notifies the ryder if the request is complete
     */
    private void notifyComplete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.complete_message)
                .setTitle(R.string.complete_title);

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
    }

    /**
     * Begins the refresh of the request list
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
     * called when request list data changes
     */
    private void refreshRequestList(){
        Log.i("trace", "ActivityRyderMain.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        requestMainAdapter.notifyDataSetChanged();
        checkStatuses();
    }

    /**
     * Handles the asynchronous polling of ES for requests.
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
