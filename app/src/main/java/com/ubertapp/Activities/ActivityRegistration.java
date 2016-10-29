package com.ubertapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.HelpMe;
import com.ubertapp.Models.User;
import com.ubertapp.R;


/**
 * User registration screen. User is able to register
 */
public class ActivityRegistration extends Activity {
    private EditText usernameEditText;
    private EditText firstlastnameEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    // TODO: 2016-10-16 payment info

    private Button doneButton;
    private ElasticSearchController ES = new ElasticSearchController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        firstlastnameEditText = (EditText) findViewById(R.id.firstlastname_edittext);
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        addressEditText = (EditText) findViewById(R.id.address_edittext);

        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelpMe.isEmptyTextField(usernameEditText)
                        && !HelpMe.isEmptyTextField(firstlastnameEditText)
                        && !HelpMe.isEmptyTextField(emailEditText)
                        && !HelpMe.isEmptyTextField(addressEditText)) {

                    User user = new User(usernameEditText.getText().toString());
                    if (ES.getUserByID(user.getId()) == null) {
                        ES.addUser(user);
                        Intent intent = new Intent(ActivityRegistration.this, ActivitySelection.class);
                        ActivityRegistration.this.startActivity(intent);
                    } else {
                        usernameEditText.setError("Username is taken. Try something else.");
                    }

                    // TODO: 2016-10-29 add the rest of the user info to the database.
                }
            }
        });
    }

    // TODO: 2016-10-16 check if user name is unique
}
