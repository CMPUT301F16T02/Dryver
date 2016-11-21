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
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        driver = (Driver) userController.getViewedUser();

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
                Intent intent = new Intent(ActivityDriverProfile.this, ActivityEmail.class);
                intent.putExtra("email", driver.getEmail());
                startActivity(intent);
            }
        });
    }
}
