package com.ubertapp.Controllers;

import com.ubertapp.Models.Request;

import java.util.ArrayList;

/**
 * Created by drei on 2016-10-08.
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
}
