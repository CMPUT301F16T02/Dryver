package com.ubertapp.Models;

import android.widget.EditText;

/**
 * Global helper methods for the ubertapp app. HelpMe stands for Helper Methods.
 */
public class HelpMe {

    /**
     * Helper method for providing a generic error to an EditText field if it's required and was left empty.
     *
     * @param editText the edit text
     * @return the boolean
     */
    static public boolean isEmptyTextField(EditText editText) {
        if (editText.getText().toString().equals("")) {
            editText.setError("This field is required.");
            return false;
        }
        return true;
    }
}
