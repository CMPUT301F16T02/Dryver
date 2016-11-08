package com.ubertapp.Models;


import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import com.ubertapp.R;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Global helper methods for the ubertapp app. HelpMe stands for Helper Methods.
 */
public class HelpMe extends Activity {

    //http://stackoverflow.com/questions/8204680/java-regex-email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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

    static public boolean isValidEmail(EditText editText)
    {
        if(isEmptyTextField(editText))
        {
            return false;
        }

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(editText.getText().toString());
        return matcher.find();
    }

    static public void configureHeader(Activity activity, TextView header) {
//        back = (Button) activity.findViewById(R.id.back_button);
//        save = (Button) activity.findViewById(R.id.save_button);
        //header = (TextView) activity.findViewById(R.id.header_textview);
    }

    static public void dateToString(Date date) {
        // TODO: 2016-10-18 implement this
    }

    static public void stringToDate(String stringDate) {
        // TODO: 2016-10-18 implement this. 
    }
}
