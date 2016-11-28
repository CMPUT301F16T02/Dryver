/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.Utility;


import android.app.Activity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.dryver.Models.Request;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Global helper methods for the ubertapp app. HelpMe stands for Helper Methods.
 */
public class HelpMe extends Activity {
    private static String DATABASE_URL = "http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/";
    private DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    /**
     * Helper method for providing a generic error to an EditText field if it's required and was left empty.
     *
     * @param editText the edit text
     * @return the boolean
     */
    static public boolean isEmptyTextField(EditText editText) {
        boolean empty = editText.getText().toString().equals("");
        if (empty) {
            editText.setError("This field is required.");
        }
        return empty;
    }

    /**
     * Checks the validity of an email within an editText
     *
     * @param editText
     * @return boolean
     */
    static public boolean isValidEmail(EditText editText) {
        boolean valid = android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
        if (!valid) {
            editText.setError("Invalid email. Must be of form name@domain.extension");
        }
        return valid;
    }

    /**
     * Checks the validity of a phone number within an edit text
     *
     * @param editText
     * @return boolean
     */
    static public boolean isValidPhone(EditText editText) {
        boolean valid = android.util.Patterns.PHONE.matcher(editText.getText().toString()).matches();
        if (!valid) {
            editText.setError("Invalid phone number.");
        }
        return valid;
    }

    /**
     * Cnvert a date to a consistent form that is TBC
     *
     * @param date
     */
    static public void dateToString(Date date) {
        // TODO: 2016-10-18 implement this
    }

    /**
     * Convert a string to a date
     *
     * @param stringDate
     */
    static public void stringToDate(String stringDate) {
        // TODO: 2016-10-18 implement this. 
    }

    /**
     * Checks whether the internet is connected
     *
     * @return boolean
     */
    static public boolean isInternetConnected() {
        try {
            InetAddress inetAddress = InetAddress.getByName(DATABASE_URL);
            return !inetAddress.equals("");
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Sets the date
     *
     * @param cal
     * @param datePicker
     */
    static public void setDatePicker(Calendar cal, DatePicker datePicker) {
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Set Time picker
     *
     * @param cal
     * @param timePicker
     */
    static public void setTimePicker(Calendar cal, TimePicker timePicker) {
        timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
    }

    /**
     * Sets the calender
     *
     * @param cal
     * @param datePicker
     * @param timePicker
     */
    static public void setCalendar(Calendar cal, DatePicker datePicker, TimePicker timePicker) {
        cal.set(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());
    }

    static public String getDateString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));
        return sdf.format(cal.getTime());
    }

    static public String formatLocation(Request request) {
        return formatPickupLocation(request) + "\n" + formatDestinationLocation(request);
    }

    static public String formatDestinationLocation(Request request) {
        String locationString = "Destination: ";
        if (request.getToAddress() != null) {
            locationString += request.getToAddress();
        } else {
            DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            locationString += decimalFormatter.format(request.getToLocation().getLatitude()) + ", " + decimalFormatter.format(request.getToLocation().getLongitude());
        }
        return locationString;
    }

    static public String formatPickupLocation(Request request) {
        String locationString = "Pickup At: ";
        if (request.getToAddress() != null) {
            locationString += request.getFromAddress();
        } else {
            DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            locationString += decimalFormatter.format(request.getToLocation().getLatitude()) + ", " + decimalFormatter.format(request.getToLocation().getLongitude());
        }
        return locationString;
    }

    static public String formatCurrencyToString(Double value) {
        return "$" + formatCurrency(value);
    }

    static public String formatCurrency(Double value) {
        DecimalFormat decimalFormatter = new DecimalFormat("0.00");
        return decimalFormatter.format(value);
    }


}
