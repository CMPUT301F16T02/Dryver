package com.ubertapp.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ubertapp.Controllers.UserController;
import com.ubertapp.Models.User;
import com.ubertapp.R;

/**
 * This is the activity that is responsible for displaying both the active user's profile infomation
 * and the viewed user's information. This means that the active user (the one using the app) will
 * be able to edit there information here, and also that this activity will be displayed when they
 * select to view a driver or rider's profile (that is not their own). ****May be responsible for
 * at least calling the methods responsible for contacting the driver****
 */
public class ActivityUserProfile extends Activity {
    //TODO: Contacting a driver via email or phone. (should that be here?)

    private UserController userController = UserController.getInstance();

    private User user;
    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView paymentText;
    private Spinner paymentSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = userController.getViewedUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_user_profile);

        this.userNameEditText = (EditText)findViewById(R.id.profileEditTextUserName);
        this.emailEditText = (EditText)findViewById(R.id.profileEditTextEmail);
        this.phoneEditText = (EditText)findViewById(R.id.profileEditTextPhoneNumber);
        this.paymentText = (TextView)findViewById(R.id.profileTextViewPaymentMethod);
        this.paymentSpinner = (Spinner)findViewById(R.id.profileSpinnerPaymentMethod);

        //Allows for genericism and not creating another activity. Active user and view driver, for example handled by this activity
        if(user.equals(userController.getActiveUser())){
            setActiveUserFields();
        } else {
            setOtherUserFields();
        }

        userNameEditText.setText(user.getUserId());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNumber());
    }

    private void setActiveUserFields() {
        userNameEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        paymentText.setVisibility(View.VISIBLE);
        paymentSpinner.setVisibility(View.VISIBLE);
    }

    private void setOtherUserFields(){
        userNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        paymentText.setVisibility(View.GONE);
        paymentSpinner.setVisibility(View.GONE);
    }
}
