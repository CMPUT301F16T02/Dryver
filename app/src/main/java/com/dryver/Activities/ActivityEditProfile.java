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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dryver.Controllers.UserController;
import com.dryver.Models.Rider;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

/**
 * This activity is for displaying the active user's information as well as allowing them to edit it
 * and save it.
 */
public class ActivityEditProfile extends Activity {
    private UserController userController = UserController.getInstance();
    private TextView titleTextView;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView paymentText;
    private Spinner paymentSpinner;
    private TextView vehicleDesriptionTextView;
    private TextView ratingTextView;
    private EditText vehicleDescriptionEditText;
    private Button saveChangesButton;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        titleTextView = (TextView) findViewById(R.id.profile_name);
        titleTextView.setText(userController.getActiveUser().getId() + "'s Profile");

        emailEditText = (EditText) findViewById(R.id.profileEditTextEmail);
        phoneEditText = (EditText) findViewById(R.id.profileEditTextPhoneNumber);
        paymentText = (TextView) findViewById(R.id.profileTextViewPaymentMethod);
        paymentSpinner = (Spinner) findViewById(R.id.profileSpinnerPaymentMethod);
        vehicleDesriptionTextView = (TextView) findViewById(R.id.activity_edit_profile_vehicle_textview);
        vehicleDescriptionEditText = (EditText) findViewById(R.id.edit_profile_vehicle_description);
        saveChangesButton = (Button) findViewById(R.id.save_changes);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar3);
        ratingTextView = (TextView) findViewById(R.id.edit_profile_rating_title);

        emailEditText.setText(userController.getActiveUser().getEmail());
        phoneEditText.setText(userController.getActiveUser().getPhoneNumber());

        checkUserType();
        setListeners();
    }

    /**
     * checks whether the user is a rider or driver, and displays and hides the appropriate UI elements
     */
    public void checkUserType() {
        if (userController.getActiveUser() instanceof Rider) {
            vehicleDesriptionTextView.setVisibility(View.GONE);
            vehicleDescriptionEditText.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            ratingTextView.setVisibility(View.GONE);
        } else {
            vehicleDesriptionTextView.setVisibility(View.VISIBLE);
            vehicleDescriptionEditText.setVisibility(View.VISIBLE);
            vehicleDescriptionEditText.setText(userController.getActiveUser().getVehicleDescription());
            ratingBar.setVisibility(View.VISIBLE);
            ratingTextView.setVisibility(View.VISIBLE);
            ratingBar.setRating(userController.getActiveUser().getRating());
        }
    }

    /**
     * Sets the listeners for the click of save changes button
     */
    private void setListeners() {
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateUserProfile()) {
                    finish();
                }
            }
        });
    }

    //TODO: Default Payment Method

    /**
     * updates the user's profile. This happens when they click save changes
     *
     * @return boolean to check whether or not the profile has been updated
     */
    private boolean updateUserProfile() {
        boolean updated = false;
        if (HelpMe.isValidPhone(phoneEditText) && HelpMe.isValidEmail(emailEditText)) {
            userController.getActiveUser().setPhoneNumber(phoneEditText.getText().toString());
            userController.getActiveUser().setEmail(emailEditText.getText().toString());
            userController.getActiveUser().setVehicleDescription(vehicleDescriptionEditText.getText().toString());

            updated = userController.updateActiveUser();
        }
        return updated;
    }
}
