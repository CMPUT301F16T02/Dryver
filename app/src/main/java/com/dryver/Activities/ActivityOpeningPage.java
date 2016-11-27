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
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dryver.Controllers.UserController;
import com.dryver.R;
import com.dryver.Utility.HelpMe;

import java.util.concurrent.ExecutionException;


/**
 * This activity is the point for application and lets the user login or register.
 */
public class ActivityOpeningPage extends Activity {

    private EditText usernameEditText;
    private Button signinButton;
    private Button getStartedButton;

    private UserController userController = UserController.getInstance();

    //TODO: Logout method. I.E remove cached uses so it isn't found on next startup (userController probs)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_page);

        //Needed to make thread safe network calls
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* Check if their is an existing cached user. Otherwise prompt for login. */
        if (UserController.getInstance().isCached()) {
            UserController.getInstance().loadUser();
            Intent intent = new Intent(ActivityOpeningPage.this, ActivityRydeOrDryve.class);
            ActivityOpeningPage.this.startActivity(intent);
        }

        assignElements();
        setListeners();
    }

    private void assignElements(){
        signinButton = (Button) findViewById(R.id.signin_button);
        getStartedButton = (Button) findViewById(R.id.getstarted_button);
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
    }

    /**
     * Sets the listeners for the sign in button's click as well as the get started button's click
     */
    private void setListeners(){
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelpMe.isEmptyTextField(usernameEditText)) {
                    try {
                        if (userController.login(usernameEditText.getText().toString())) {
                            Intent intent = new Intent(ActivityOpeningPage.this, ActivityRydeOrDryve.class);
                            ActivityOpeningPage.this.startActivity(intent);
                        } else {
                            usernameEditText.setError("Username does not exist.");
                        }
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityOpeningPage.this, ActivityRegistration.class);
                ActivityOpeningPage.this.startActivity(intent);
            }
        });
    }
}
