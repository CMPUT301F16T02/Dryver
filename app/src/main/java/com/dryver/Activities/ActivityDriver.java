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
import com.dryver.R;


/**
 * This activities deals with providing the driver with UI for requests.
 */
public class ActivityDriver extends ActivityLoggedInActionBar implements OnItemSelectedListener {

    private ListView requestListView;
    private Spinner sortSpinner;
    private RequestListAdapter requestListAdapter;
    private Location currentLocation;

    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        currentLocation = new Location("Current Location Test");
        currentLocation.setLatitude(53.456143);
        currentLocation.setLongitude(-113.514594);

        sortSpinner = (Spinner) findViewById(R.id.requestSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activity_driver_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);

        driver = new Driver(userController.getActiveUser());
        userController.setActiveUser(driver);
        requestListView = (ListView) findViewById(R.id.requestListViewRequest);

        requestListAdapter = new RequestListAdapter(this, requestSingleton.getRequests());
        requestListView.setAdapter(requestListAdapter);

        requestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(ActivityDriver.this, ActivityRequestSelection.class);
                startActivity(intent);
                return true;
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
}
