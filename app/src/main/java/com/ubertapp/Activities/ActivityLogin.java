package com.ubertapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.HelpMe;
import com.ubertapp.Models.User;
import com.ubertapp.R;


/**
 * User login screen.
 */
public class ActivityLogin extends Activity {

    private EditText usernameEditText;
    private Button loginButton;
    private ElasticSearchController ES = new ElasticSearchController();

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
                    User user = ES.getUserByID(usernameEditText.getText().toString());
                    if (user != null) {
                        // TODO: 2016-11-01 assign the user model the current user.

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
