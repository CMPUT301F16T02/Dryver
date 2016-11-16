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
import android.widget.Button;
import android.widget.EditText;

import com.dryver.Controllers.UserController;
import com.dryver.Models.HelpMe;
import com.dryver.R;


/**
 * Activity for user login.
 */
public class ActivityLogin extends Activity {
    private EditText usernameEditText;
    private Button loginButton;
    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Need this to run thread-safe networking calls.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);

        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelpMe.isEmptyTextField(usernameEditText)) {
                    if (userController.login(usernameEditText.getText().toString())) {
                        Intent intent = new Intent(ActivityLogin.this, ActivitySelection.class);
                        ActivityLogin.this.startActivity(intent);
                    } else {
                        usernameEditText.setError("Username does not exist.");
                    }
                }
            }
        });
    }


    // TODO: 2016-10-16 Generate error handling when database is integrated.

    // TODO: 2016-10-29 verify user exists
}
