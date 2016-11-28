package com.dryver.Utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Simple class to check internet connectivity.
 * From http://stackoverflow.com/questions/12752598/check-online-status-android
 * AND http://stackoverflow.com/questions/6507535/webservice-availability-check-in-android
 * Accessed: 2016-11-27
 * Created by colemackenzie on 2016-11-27.
 */

public class ConnectionCheck {

    public boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
                //Network is available but check if we can get access from the network.
//                URL url = new URL("http://google.com");
                URL url = new URL("http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/cmput301f16t02");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200)  //Successful response.
                {
                    return true;
                } else {
                    Log.d("NO INTERNET", "NO INTERNET");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
