package com.ubertapp.Controllers;

/**
 * Created by drei on 2016-10-08.
 */
public class RequestSingleton {
    private static RequestSingleton ourInstance = new RequestSingleton();

    public static RequestSingleton getInstance() {
        return ourInstance;
    }

    private RequestSingleton() {
    }
}
