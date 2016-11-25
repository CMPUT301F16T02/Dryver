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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Models.RequestStatus;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

/**
 * The activity responsible for viewing a requests details more closely / inspecting a request.
 * Allows you to cancel requests, as well as view the drivers associated with a request.
 */

public class ActivityRequestSelection extends Activity {

    private TextView locationTextView;
    private TextView requestSelectionDate;
    private TextView statusTextView;

    private Button cancelButton;
    private Button viewDriversButton;
    private Button deleteButton;

    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_selection);

        locationTextView = (TextView) findViewById(R.id.requestSelectionLocation);
        requestSelectionDate = (TextView) findViewById(R.id.requestSelectionDate);
        statusTextView = (TextView) findViewById(R.id.requestSelectionToStatus);
        viewDriversButton = (Button) findViewById(R.id.requestSelectionButtonViewList);
        cancelButton = (Button) findViewById(R.id.requestSelectionButtonCancel);
        deleteButton = (Button) findViewById(R.id.requestSelectionButtonDelete);

        checkCancelled();


        HelpMe.formatLocationTextView(requestSingleton.getTempRequest(), locationTextView);
        requestSelectionDate.setText("Request Date: " + HelpMe.getDateString(requestSingleton.getTempRequest().getDate()));
        statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());

        viewDriversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRequestSelection.this, ActivityDriverList.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.getTempRequest().setStatus(RequestStatus.CANCELLED);
                requestSingleton.pushTempRequest();
                checkCancelled();
                statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSingleton.removeRequest(requestSingleton.getTempRequest());
                statusTextView.setText("Status: " + requestSingleton.getTempRequest().statusCodeToString());
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.setTempRequest(null);
    }

    private void checkCancelled() {
        if (requestSingleton.getTempRequest().getStatus().equals(RequestStatus.CANCELLED)) {
            cancelButton.setEnabled(false);
        }
    }
}
