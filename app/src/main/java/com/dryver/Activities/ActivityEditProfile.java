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

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Utility.HelpMe;
import com.dryver.Models.Rider;
import com.dryver.Models.User;
import com.dryver.R;

/**
 * This activity is for displaying the active user's information as well as allowing them to edit it
 * and save it.
 */
public class ActivityEditProfile extends Activity {
    private UserController userController = UserController.getInstance();
    private User user;
    private Driver driver;
    private TextView titleTextView;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView paymentText;
    private Spinner paymentSpinner;
    private TextView vehicleDesriptionTextView;
    private EditText vehicleDescriptionEditText;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = userController.getViewedUser();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.titleTextView = (TextView)findViewById(R.id.profile_name);
        titleTextView.setText(user.getId() + "'s Profile");

        this.emailEditText = (EditText)findViewById(R.id.profileEditTextEmail);
        this.phoneEditText = (EditText)findViewById(R.id.profileEditTextPhoneNumber);
        this.paymentText = (TextView)findViewById(R.id.profileTextViewPaymentMethod);
        this.paymentSpinner = (Spinner)findViewById(R.id.profileSpinnerPaymentMethod);
        this.vehicleDesriptionTextView = (TextView)findViewById(R.id.activity_edit_profile_vehicle_textview);
        this.vehicleDescriptionEditText = (EditText)findViewById(R.id.edit_profile_vehicle_description);
        this.saveChangesButton = (Button)findViewById(R.id.save_changes);

        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNumber());

        checkUserType();
        setListeners();
    }

    /**
     * checks whether the user is a rider or driver, and displays and hides the appropriate UI elements
     */
    public void checkUserType(){
        if(user instanceof Rider){
            vehicleDesriptionTextView.setVisibility(View.GONE);
            vehicleDescriptionEditText.setVisibility(View.GONE);
        } else if(user instanceof Driver) {
            driver = (Driver) userController.getViewedUser();
            vehicleDesriptionTextView.setVisibility(View.VISIBLE);
            vehicleDescriptionEditText.setVisibility(View.VISIBLE);
            //this is bad, I'm sorry
            vehicleDescriptionEditText.setText(((Driver) user).getVehicleDescription());
        } else{
            vehicleDesriptionTextView.setVisibility(View.VISIBLE);
            vehicleDescriptionEditText.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the listeners for the click of save changes button
     */
    private void setListeners(){
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateUserProfile()){
                    finish();
                }
            }
        });
    }

    //TODO: Default Payment Method

    /**
     * updates the user's profile. This happens when they click save changes
     * @return
     */
    private boolean updateUserProfile(){
        user = userController.getActiveUser();

        boolean updated = false;
        if(HelpMe.isValidPhone(phoneEditText) && HelpMe.isValidEmail(emailEditText))
        {
            user.setPhoneNumber(phoneEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            driver.setVehicleDescription(vehicleDescriptionEditText.getText().toString());

            updated = userController.updateActiveUser();
        }
        return updated;
    }
}
