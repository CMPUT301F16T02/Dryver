/*
 * Copyright (C) 2016
 * Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
        return android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    static public boolean isValidPhone(EditText editText)
    {
        return android.util.Patterns.PHONE.matcher(editText.getText().toString()).matches();
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
