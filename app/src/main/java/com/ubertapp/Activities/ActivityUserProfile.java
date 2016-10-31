package com.ubertapp.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.Spinner;

import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.User;
import com.ubertapp.R;
public class ActivityUserProfile extends Activity {

    //TODO: Should these be edit texts? What if it isnt their profile? How to dynamically change
    //type of text view to regular textview if it is another's profile?

    private User user = UserController.getInstance().getViewedUser();
    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private Spinner paymentMethodSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_user_profile);

        this.userNameEditText = (EditText)findViewById(R.id.userName_editText);
        this.emailEditText = (EditText)findViewById(R.id.email_editText);
        this.phoneNumberEditText = (EditText)findViewById(R.id.phone_editText);
        this.paymentMethodSpinner = (Spinner)findViewById(R.id.payment_spinner);

        userNameEditText.setText(user.getUserId());
        emailEditText.setText(user.getEmail());
        phoneNumberEditText.setText(user.getPhoneNumber());
    }



}
