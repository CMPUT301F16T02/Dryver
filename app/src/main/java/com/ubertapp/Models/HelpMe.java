package com.ubertapp.Models;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.ubertapp.R;

/**
 * Global helper methods for the ubertapp app. HelpMe stands for Helper Methods.
 */
public class HelpMe extends Activity {

    /**
     * Helper method for providing a generic error to an EditText field if it's required and was left empty.
     *
     * @param editText the edit text
     * @return the boolean
     */
    static public boolean isEmptyTextField(EditText editText) {
        if (editText.getText().toString().equals("")) {
            editText.setError("This field is required.");
            return true;
        }
        return false;
    }

    static public void configureHeader(Activity activity, TextView header) {
//        back = (Button) activity.findViewById(R.id.back_button);
//        save = (Button) activity.findViewById(R.id.save_button);
        header = (TextView) activity.findViewById(R.id.header_textview);
    }
}
