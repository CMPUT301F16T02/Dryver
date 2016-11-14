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

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Controllers.RequestListAdapter;
import com.ubertapp.Controllers.RequestSingleton;
import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;

import com.ubertapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityRiderRequest extends Activity {

    private Button mAddRequest;
    private ListView requestListView;
    private RequestListAdapter requestListAdapter;

    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Rider rider;
    private Location testFromLocation = new Location("from");
    private Location testToLocation = new Location("to");

    private static final String RETURN_VIEW_REQUEST = "com.ubertapp.return_view_request";
    private static final String RETURN_REQUEST_DELETE = "com.ubertapp.return_request_delete";
    private static final int REQUEST_VIEW_REQUEST = 0;
    private int requestPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);
        mAddRequest = (Button) findViewById(R.id.requestButtonNewRequest);
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);

        testFromLocation.setLatitude(53.523869);
        testFromLocation.setLongitude(-113.526146);
        testToLocation.setLatitude(53.548623);
        testToLocation.setLongitude(-113.506537);

        mAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.addRequest(rider, Calendar.getInstance(), testFromLocation, testToLocation, 0.5);
                requestListAdapter.notifyDataSetChanged();
            }
        });

        requestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                requestPosition = position;
                Intent intent = new Intent(ActivityRiderRequest.this, ActivityRequestSelection.class);
                intent.putExtra(RETURN_VIEW_REQUEST, requestPosition);

                startActivityForResult(intent, REQUEST_VIEW_REQUEST);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requestListAdapter = new RequestListAdapter(this, requestSingleton.getUpdatedRequests());
        requestListView.setAdapter(requestListAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                int position = (Integer) getIntent().getSerializableExtra(RETURN_VIEW_REQUEST);
                requestSingleton.getRequests().remove(position);
                requestListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {

        Log.i("Activtiy", "RiderRequest onResume()");
        super.onResume();
        requestListAdapter.notifyDataSetChanged();
    }


}
