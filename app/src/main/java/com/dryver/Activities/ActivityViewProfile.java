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

    private TextView titleTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView vehicleInfoTitleTextView;
    private TextView vehicleInfoTextView;
    private TextView ratingsTitleTextView;
    //TODO: How to represent ratings?? Is there a 5 star widget?

    private UserController userController = UserController.getInstance();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        user = userController.getViewedUser();

        titleTextView = (TextView)findViewById(R.id.profile_title);
        phoneTextView = (TextView)findViewById(R.id.driver_profile_phone);
        emailTextView = (TextView)findViewById(R.id.driver_profile_email);
        vehicleInfoTitleTextView = (TextView)findViewById(R.id.description_title);
        vehicleInfoTextView = (TextView)findViewById(R.id.driver_profile_vehicle_info);
        ratingsTitleTextView = (TextView)findViewById(R.id.ratings_title);

        titleTextView.setText(user.getId() + "'s Profile");
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhoneNumber());

        if(user instanceof Rider){
            vehicleInfoTitleTextView.setVisibility(View.GONE);
            vehicleInfoTextView.setVisibility(View.GONE);
            ratingsTitleTextView.setVisibility(View.GONE);
        } else if(user instanceof Driver) {
            vehicleInfoTitleTextView.setVisibility(View.VISIBLE);
            vehicleInfoTextView.setVisibility(View.VISIBLE);
            ratingsTitleTextView.setVisibility(View.VISIBLE);
            //this is bad, I'm sorry
            vehicleInfoTextView.setText(((Driver) user).getVehicleDescription());
        } else{
            vehicleInfoTitleTextView.setVisibility(View.VISIBLE);
            vehicleInfoTextView.setVisibility(View.GONE);
            ratingsTitleTextView.setVisibility(View.GONE);
        }



        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneTextView.getText().toString()));
                startActivity(intent);
            }
        });

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(ActivityViewProfile.this, ActivityEmail.class);
                intent.putExtra("email", user.getEmail());
                startActivity(intent);
            }
        });
    }
}
