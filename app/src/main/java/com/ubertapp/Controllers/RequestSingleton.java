package com.ubertapp.Controllers;

import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import com.ubertapp.Models.User;

import java.util.ArrayList;

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

    public void addRequest(Double cost, Rider rider) {
        Request request = new Request(cost, rider);
        
    }

    // TODO: 2016-10-29 Check for duplicate requests from the same user.
}
