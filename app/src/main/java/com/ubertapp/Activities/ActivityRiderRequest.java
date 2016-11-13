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
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Controllers.RequestListAdapter;
import com.ubertapp.Controllers.RequestSingleton;
import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;

import com.ubertapp.R;

import java.util.Calendar;
import java.util.Locale;

public class ActivityRiderRequest extends Activity {

    private Button mAddRequest;
    private ListView requestListView;
    private RequestListAdapter requestListAdapter;
    private ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();
    private Rider rider;
    private Request request;

    private Address testFromAddress;
    private Address testToAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request);
        rider = new Rider(userController.getActiveUser().getUserId());

        testFromAddress = new Address(Locale.CANADA);
        testFromAddress.setLatitude(53.523869);
        testFromAddress.setLongitude(-113.526146);
        testToAddress = new Address(Locale.CANADA);
        testToAddress.setLatitude(53.548623);
        testToAddress.setLongitude(-113.506537);

        mAddRequest = (Button) findViewById(R.id.requestButtonNewRequest);
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);

        mAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.addRequest(rider, Calendar.getInstance(), testFromAddress, testToAddress, 0.5);
                requestListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requestListAdapter = new RequestListAdapter(this, requestSingleton.getRequests());
        requestListView.setAdapter(requestListAdapter);
    }
}
