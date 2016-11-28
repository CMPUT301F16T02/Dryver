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

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dryver.Adapters.DryverListAdapter;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.R;
import com.dryver.Utility.ICallBack;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ActivityRequestDriverList extends ActivityLoggedInActionBar {

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    private ListView driversListView;
    private ArrayList<String> drivers;
    private ArrayAdapter<String> adapter;
    private SwipeRefreshLayout swipeContainer;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver_list);

        drivers = requestSingleton.getTempRequest().getDrivers();
        driversListView = (ListView) findViewById(R.id.drivers_list);

        adapter = new DryverListAdapter(this, drivers);
        driversListView.setAdapter(adapter);

        assignElements();
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDriverList();
        setTimer();
    }

    @Override
    public void onPause() {
        Log.i("trace", "ActivityDryverMain.onPause()");
        super.onPause();
        timer.cancel();
    }

    private void assignElements() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerDriver);
    }

    private void setListeners() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginRefresh();
            }
        });
    }

    /**
     * Begins the refreshing of the driver list
     */
    public void beginRefresh() {
        requestSingleton.updateTempRequest(new ICallBack() {
            @Override
            public void execute() {
                refreshDriverList();
            }
        });
    }

    /**
     * Called after data in driver list has changed
     */
    private void refreshDriverList() {
        Log.i("trace", "ActivityRiderMain.refreshRequestList()");
        swipeContainer.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    private void setTimer() {
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
