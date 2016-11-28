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
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Rider;
import com.dryver.Models.User;
import com.dryver.R;

/**
 * This activity is responsible for handling the viewing of a profile that is not the active user's
 * Email and phone is done from here
 */

public class ActivityViewProfile extends Activity {

    private TextView titleTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView vehicleInfoTitleTextView;
    private TextView vehicleInfoTextView;
    private TextView ratingsTitleTextView;
    private RatingBar ratingBar;
    //TODO: How to represent ratings?? Is there a 5 star widget?

    private UserController userController = UserController.getInstance();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        user = userController.getViewedUser();

        titleTextView = (TextView)findViewById(R.id.profile_title);
        phoneTextView = (TextView)findViewById(R.id.driver_profile_phone);
        emailTextView = (TextView)findViewById(R.id.driver_profile_email);
        vehicleInfoTitleTextView = (TextView)findViewById(R.id.description_title);
        vehicleInfoTextView = (TextView)findViewById(R.id.driver_profile_vehicle_info);
        ratingsTitleTextView = (TextView)findViewById(R.id.ratings_title);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        titleTextView.setText(user.getId() + "'s Profile");
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhoneNumber());

        checkUserType();
        setListeners();
    }

    /**
     * Checks whether the user is a rider or driver and display or hide appropriate UI elements
     */
    public void checkUserType(){
        if(user instanceof Rider){
            vehicleInfoTitleTextView.setVisibility(View.GONE);
            vehicleInfoTextView.setVisibility(View.GONE);
            ratingsTitleTextView.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        } else if(user instanceof Driver) {
            vehicleInfoTitleTextView.setVisibility(View.VISIBLE);
            vehicleInfoTextView.setVisibility(View.VISIBLE);
            ratingsTitleTextView.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            vehicleInfoTextView.setText(user.getVehicleDescription());
            ratingBar.setRating(user.getRating());
        } else{
            vehicleInfoTitleTextView.setVisibility(View.VISIBLE);
            vehicleInfoTextView.setVisibility(View.GONE);
            ratingsTitleTextView.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the listeners for the email TextView, the phone number TextView
     */
    public void setListeners(){
        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneTextView.getText().toString()));
                startActivity(intent);
            }
        });

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + user.getEmail()));
                Intent chooser = intent.createChooser(intent, "Send Email to " + user.getEmail());

                ComponentName emailApp = intent.resolveActivity(getPackageManager());
                ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                boolean hasEmailApp = emailApp != null && !emailApp.equals(unsupportedAction);

                if(hasEmailApp){
                    startActivity(chooser);
                } else{
                    emailTextView.setError("Please login in chosen email application");
                }
            }
        });
    }
}
