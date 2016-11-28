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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dryver.Controllers.RequestSingleton;
import com.dryver.Controllers.UserController;
import com.dryver.Models.Rider;
import com.dryver.R;
import com.dryver.Utility.HelpMe;
import com.dryver.Utility.ICallBack;

import java.util.Calendar;


/**
 * Activity used to get information from the user for a request they are about to make.
 * Or edit existing requests.
 */
public class ActivityRequest extends Activity {
    private Button setLocation;
    private Button submitRequest;
    private EditText requestDescription;
    private EditText tripPrice;
    private TextView locationText;

    private Calendar calendar = Calendar.getInstance();
    private UserController userController = UserController.getInstance();
    private RequestSingleton requestSingleton = RequestSingleton.getInstance();

    private Rider rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        rider = new Rider(userController.getActiveUser());
        userController.setActiveUser(rider);

        setLocation = (Button) findViewById(R.id.requestButtonLocation);
        submitRequest = (Button) findViewById(R.id.requestButtonSubmit);
        tripPrice = (EditText) findViewById(R.id.requestTripPrice);
        requestDescription = (EditText) findViewById(R.id.requestDescription);
        locationText = (TextView) findViewById(R.id.requestLocation);


        tripPrice.setText(HelpMe.formatCurrency(requestSingleton.getEstimate()));

        toggleSubmitButton();
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRequest.this, ActivityRequestMap.class);
                startActivity(intent);
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HelpMe.isEmptyTextField(tripPrice)) {
                    Double cost = Double.parseDouble(tripPrice.getText().toString());
                    requestSingleton.getTempRequest().setCost(cost);
                    requestSingleton.getTempRequest().setDate(calendar);
                    requestSingleton.getTempRequest().setDescription(requestDescription.getText().toString());
                    requestSingleton.pushTempRequest(new ICallBack(){
                        @Override
                        public void execute(){
                            Log.i("CALLBACK", "Make Request");
                            finish();
                        }
                    });
                }
            }
        });

        requestDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDescription.setText("");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        locationText.setText(HelpMe.formatLocation(requestSingleton.getTempRequest()));
        tripPrice.setText(HelpMe.formatCurrency(requestSingleton.getEstimate()));
        requestDescription.setText(requestSingleton.getTempRequest().getDescription());
        toggleSubmitButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestSingleton.clearTempRequest();
    }

    private void toggleSubmitButton() {
        if (requestSingleton.getTempRequest().hasRoute()) {
            submitRequest.setEnabled(true);
        } else {
            submitRequest.setEnabled(false);
        }
    }

}
