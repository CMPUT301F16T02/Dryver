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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.dryver.Controllers.RequestListAdapter;
import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Request;
import com.dryver.Models.Rider;
import com.dryver.R;


/**
 * The activity that acts as the main rider activity. Lists requests, you can create requests here,
 * and select requests to inspect a request.
 */

public class ActivityRequestList extends ActivityLoggedInActionBar {

    private Button mAddRequest;
    private ListView requestListView;
    private RequestListAdapter requestListAdapter;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private UserController userController = UserController.getInstance();

    private SwipeRefreshLayout swipeContainer;

    private Rider rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        mAddRequest = (Button) findViewById(R.id.requestButtonNewRequest);
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);

        requestListAdapter = new RequestListAdapter(this, requestSingleton.getUpdatedRequests());
        requestListView.setAdapter(requestListAdapter);

        mAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRequestList.this, ActivityRequest.class);
                startActivity(intent);
            }
        });

        requestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                requestSingleton.setViewedRequest((Request)requestListView.getItemAtPosition(position));
                Intent intent = new Intent(ActivityRequestList.this, ActivityRequest.class);
                startActivity(intent);
                return true;
            }
        });


        //https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRequestList();
            }
        });

    }

    public void refreshRequestList() {
        if(requestSingleton.updateRequests()){
            swipeContainer.setRefreshing(false);
            requestListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRequestList();
    }
}
