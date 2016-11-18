package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.R;

public class ActivityDriverProfile extends Activity {

    private TextView title;
    private TextView phone;
    private TextView email;
    private TextView vehicleInfo;
    //TODO: How to represent ratings?? Is there a 5 star widget?

    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        Driver driver = (Driver) userController.getViewedUser();

        title = (TextView)findViewById(R.id.profile_title);
        phone = (TextView)findViewById(R.id.driver_profile_phone);
        email = (TextView)findViewById(R.id.driver_profile_email);
        vehicleInfo = (TextView)findViewById(R.id.driver_profile_vehicle_info);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone.getText().toString()));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, email.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Message to: " +
                        userController.getViewedUser().getUsername() +
                        " From: " +
                        userController.getActiveUser().getUsername());
                intent.setType("message/rfc822");
            }
        });
    }
}
