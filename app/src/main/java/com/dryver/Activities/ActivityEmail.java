package com.dryver.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dryver.Models.HelpMe;
import com.dryver.R;

public class ActivityEmail extends Activity {

    TextView toTextView;
    EditText subjectEditText;
    EditText bodyEditText;
    Button sendButton;

    private String email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
        }

        this.toTextView = (TextView)findViewById(R.id.email_to);
        this.toTextView.setText("To: " + email);

        this.subjectEditText = (EditText)findViewById(R.id.email_subject);
        this.bodyEditText = (EditText)findViewById(R.id.email_body);
        this.sendButton = (Button)findViewById(R.id.email_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if(!HelpMe.isEmptyTextField(subjectEditText) &&
                        !HelpMe.isEmptyTextField(bodyEditText)){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subjectEditText.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, bodyEditText.getText().toString());
                    intent.setType("message/rfc822");
                }
            }
        });
    }
}
