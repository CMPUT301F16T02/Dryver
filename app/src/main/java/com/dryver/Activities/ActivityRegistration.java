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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.UserController;
import com.dryver.Utility.HelpMe;
import com.dryver.Models.User;
import com.dryver.R;


/**
 * User registration screen. User is able to register.
 */
public class ActivityRegistration extends Activity {
    private EditText usernameEditText;
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    // TODO: 2016-10-16 payment info

    private Button doneButton;
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Need this to run thread-safe networking calls.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        lastnameEditText = (EditText) findViewById(R.id.lastname_edittext);
        firstnameEditText = (EditText) findViewById(R.id.firstname_edittext);
        phoneEditText = (EditText) findViewById(R.id.phone_edittext);
        emailEditText = (EditText) findViewById(R.id.email_edittext);

        findViewById(R.id.username_edittext).requestFocus();

        doneButton = (Button) findViewById(R.id.done_button);
        setListeners();
    }

    /**
     * Sets the listeners for the done button's click
     */
    public void setListeners(){
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelpMe.isEmptyTextField(usernameEditText)
                        && !HelpMe.isEmptyTextField(firstnameEditText)
                        && !HelpMe.isEmptyTextField(lastnameEditText)
                        && HelpMe.isValidPhone(phoneEditText)
                        && HelpMe.isValidEmail(emailEditText)) {

                    User user = new User(usernameEditText.getText().toString(),
                            firstnameEditText.getText().toString(),
                            lastnameEditText.getText().toString(),
                            phoneEditText.getText().toString(),
                            emailEditText.getText().toString());

                    if (ES.addUser(user)) {
                        Log.i("Info", "User added succesfully via ElasticSearch Controller");
                        userController.setActiveUser(user);
                        Intent intent = new Intent(ActivityRegistration.this, ActivityRydeOrDryve.class);
                        ActivityRegistration.this.startActivity(intent);
                    } else {
                        usernameEditText.setError("Username is taken. Try something else.");
                    }
                } else if(!HelpMe.isValidEmail(emailEditText)) {
                    emailEditText.setError("Invalid email. Must be of form name@domain.extension");
                } else if(!HelpMe.isValidPhone(phoneEditText)) {
                    emailEditText.setError("Invalid phone number.");
                }
            }
        });
    }
}
