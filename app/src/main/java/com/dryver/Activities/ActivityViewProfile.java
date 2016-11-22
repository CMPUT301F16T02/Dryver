package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dryver.Controllers.UserController;
import com.dryver.Models.Driver;
import com.dryver.Models.Rider;
import com.dryver.Models.User;
import com.dryver.R;

public class ActivityViewProfile extends Activity {

    private TextView title;
    private TextView phone;
    private TextView email;
    private TextView vehicleInfoTitle;
    private TextView vehicleInfo;
    private TextView ratingsTitle;
    //TODO: How to represent ratings?? Is there a 5 star widget?

    private UserController userController = UserController.getInstance();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        user = (Driver) userController.getViewedUser();

        title = (TextView)findViewById(R.id.profile_title);
        phone = (TextView)findViewById(R.id.driver_profile_phone);
        email = (TextView)findViewById(R.id.driver_profile_email);
        vehicleInfoTitle = (TextView)findViewById(R.id.description_title);
        vehicleInfo = (TextView)findViewById(R.id.driver_profile_vehicle_info);
        ratingsTitle = (TextView)findViewById(R.id.ratings_title);

        if(user instanceof Rider){
            vehicleInfoTitle.setVisibility(View.GONE);
            vehicleInfo.setVisibility(View.GONE);
            ratingsTitle.setVisibility(View.GONE);
        } else if(user instanceof Driver) {
            vehicleInfoTitle.setVisibility(View.VISIBLE);
            vehicleInfo.setVisibility(View.VISIBLE);
            ratingsTitle.setVisibility(View.VISIBLE);
            //this is bad, I'm sorry
            vehicleInfo.setText(((Driver) user).getVehicleDescription());
        } else{
            vehicleInfoTitle.setVisibility(View.VISIBLE);
            vehicleInfo.setVisibility(View.GONE);
            ratingsTitle.setVisibility(View.GONE);
        }

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
                Intent intent = new Intent(ActivityViewProfile.this, ActivityEmail.class);
                intent.putExtra("email", user.getEmail());
                startActivity(intent);
            }
        });
    }
}
