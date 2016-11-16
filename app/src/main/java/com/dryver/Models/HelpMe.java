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

package com.dryver.Models;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.widget.EditText;

import java.net.InetAddress;
import java.util.Date;


/**
 * Global helper methods for the ubertapp app. HelpMe stands for Helper Methods.
 */
public class HelpMe extends Activity {
    private static String DATABASE_URL = "http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/";

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

    /**
     * Checks the validity of an email within an editText
     * @param editText
     * @return boolean
     */
    static public boolean isValidEmail(EditText editText)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    /**
     * Checks the validity of a phone number within an edit text
     * @param editText
     * @return boolean
     */
    static public boolean isValidPhone(EditText editText)
    {
        return android.util.Patterns.PHONE.matcher(editText.getText().toString()).matches();
    }

    /**
     * Cnvert a date to a consistent form that is TBC
     * @param date
     */
    static public void dateToString(Date date) {
        // TODO: 2016-10-18 implement this
    }

    /**
     * Convert a string to a date
     * @param stringDate
     */
    static public void stringToDate(String stringDate) {
        // TODO: 2016-10-18 implement this. 
    }

    static public boolean isInternetConnected() {
        try {
            InetAddress inetAddress = InetAddress.getByName(DATABASE_URL);
            return !inetAddress.equals("");
        } catch (Exception e) {
            return false;
        }

    }
}
