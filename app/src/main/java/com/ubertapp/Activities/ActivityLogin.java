package com.ubertapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.HelpMe;
import com.ubertapp.R;


/**
 * User login screen.
 */
public class ActivityLogin extends Activity {

    private EditText usernameEditText;
    private Button loginButton;
    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);

        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelpMe.isEmptyTextField(usernameEditText)) {
                    userController.login(usernameEditText.getText().toString());
                    Intent intent = new Intent(ActivityLogin.this, ActivitySelection.class);
                    ActivityLogin.this.startActivity(intent);
                }
            }
        });
    }


    // TODO: 2016-10-16 Generate error handling when database is integrated.

    // TODO: 2016-10-29 verify user exists
}
