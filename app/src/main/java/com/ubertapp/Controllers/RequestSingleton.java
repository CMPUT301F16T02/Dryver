package com.ubertapp.Controllers;

import android.location.Address;

import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import com.ubertapp.Models.User;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Request Singleton. Deals from providing request information to the caller.
 */
public class RequestSingleton {
    private static RequestSingleton ourInstance = new RequestSingleton();
    private static ArrayList<Request> requests = new ArrayList<Request>();

    public static RequestSingleton getInstance() {
        return ourInstance;
    }

    private RequestSingleton() {
    }

    public static ArrayList<Request> getRequests() {
        return requests;
    }

    public void addRequest(Rider rider, Calendar date, Address fromLocation, Address toLocation, double rate) {
        Request request = new Request(rider, Calendar.getInstance(), fromLocation, toLocation, rate);
        requests.add(request);

}

        // TODO: 2016-10-29 Check for duplicate requests from the same user.
}
