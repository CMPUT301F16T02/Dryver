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
import com.dryver.Models.HelpMe;
import com.dryver.Models.User;
import com.dryver.R;

/**
 * This is the activity that is responsible for displaying both the active user's profile infomation
 * and the viewed user's information. This means that the active user (the one using the app) will
 * be able to edit there information here, and also that this activity will be displayed when they
 * select to view a driver or rider's profile (that is not their own). ****May be responsible for
 * at least calling the methods responsible for contacting the driver****
 */
public class ActivityUserProfile extends Activity {
    //Paypal? Bitcoin Wallets? Cash is an easy default cus then we can ignore everything lol

    private UserController userController = UserController.getInstance();
    private User user;
    private TextView titleTextView;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView paymentText;
    private Spinner paymentSpinner;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = userController.getViewedUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //This doesn't work for some reason;
        this.titleTextView = (TextView)findViewById(R.id.profile_name);
        titleTextView.setText(user.getId() + "'s Profile");

        this.emailEditText = (EditText)findViewById(R.id.profileEditTextEmail);
        this.phoneEditText = (EditText)findViewById(R.id.profileEditTextPhoneNumber);
        this.paymentText = (TextView)findViewById(R.id.profileTextViewPaymentMethod);
        this.paymentSpinner = (Spinner)findViewById(R.id.profileSpinnerPaymentMethod);
        this.saveChangesButton = (Button)findViewById(R.id.save_changes);

        //Allows for genericism and not creating another activity. Active user and view driver, for example handled by this activity
        //May get rid of this...
        if(user.equals(userController.getActiveUser())){
            setActiveUserFields();
        } else {
            setOtherUserFields();
        }

        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNumber());

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateUserProfile()){
                    finish();
                }
            }
        });
    }

    private void setActiveUserFields() {
        titleTextView.setEnabled(true);
        emailEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        paymentText.setVisibility(View.VISIBLE);
        paymentSpinner.setVisibility(View.VISIBLE);
    }

    private void setOtherUserFields(){
        titleTextView.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        paymentText.setVisibility(View.GONE);
        paymentSpinner.setVisibility(View.GONE);
    }

    //TODO: Default Payment Method
    private boolean updateUserProfile(){
        user = userController.getActiveUser();

        boolean updated = false;
        if(HelpMe.isValidPhone(phoneEditText) && HelpMe.isValidEmail(emailEditText))
        {
            user.setPhoneNumber(phoneEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            updated = userController.updateActiveUser();
        }
        return updated;
    }
}
