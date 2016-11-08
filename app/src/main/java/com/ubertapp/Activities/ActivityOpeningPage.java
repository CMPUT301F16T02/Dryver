package com.ubertapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.ubertapp.R;


/**
 * The initial Opening Page of the ubertapp app.
 */
public class ActivityOpeningPage extends AppCompatActivity {

    private Button signinButton;
    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_page);

        signinButton = (Button) findViewById(R.id.signin_button);
        getStartedButton = (Button) findViewById(R.id.getstarted_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityOpeningPage.this, ActivityLogin.class);
                ActivityOpeningPage.this.startActivity(intent);
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityOpeningPage.this, ActivityRegistration.class);
                ActivityOpeningPage.this.startActivity(intent);
            }
        });
    }
}
