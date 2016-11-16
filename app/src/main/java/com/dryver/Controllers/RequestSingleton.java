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

package com.dryver.Controllers;

import android.location.Location;
import android.util.Log;

import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.Rider;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Request Singleton. Deals from providing request information to the caller.
 */
public class RequestSingleton {
    private static RequestSingleton ourInstance = new RequestSingleton();
    private static ArrayList<Request> requests = new ArrayList<Request>();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    public static RequestSingleton getInstance() {
        return ourInstance;
    }

    private RequestSingleton() {}

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public ArrayList<Request> getUpdatedRequests() {
        updateRequests();
        return requests;
    }

    /**
     * A simple method for fetching an updated request list via Elastic Search
     * @see ElasticSearchController
     */
    private void updateRequests() {
        Log.i("info", "RequestSingleton updateRequests()");
        if(userController.getActiveUser() instanceof Rider){
            ElasticSearchController.GetRequestsTask getRequestsTask = new ElasticSearchController.GetRequestsTask();
            getRequestsTask.execute(userController.getActiveUser().getUserId());
            try {
                requests = getRequestsTask.get();
            } catch (Exception e) {
                Log.i("Error", "Failed to get " + userController.getActiveUser().getUserId() + "'s ID.");
            }
        } else if(userController.getActiveUser() instanceof Driver){
            //TODO: Implement a way of searching for requests in a certain area or something for drivers
        }
    }

    /**
     * A method that adds a request to the current request list for the user as well as Elastic Search
     * @param rider
     * @param date
     * @param fromLocation
     * @param toLocation
     * @param rate
     * @see ElasticSearchController
     */
    //TODO Correct Times... Why is date passed and not used?
    public void addRequest(Rider rider, Calendar date, Location fromLocation, Location toLocation, double rate) {
        Log.i("info", "RequestSingleton addRequest()");

        Request request = new Request(rider, date, fromLocation, toLocation, rate);

        //TODO: Handle offline here. If it isn't added to ES...

        ElasticSearchController.AddRequestTask addRequestTask = new ElasticSearchController.AddRequestTask();
        addRequestTask.execute(request);
        try{
            if(addRequestTask.get()) {
                Log.i("info", "Request Successfully added to server");
                requests.add(request);
            } else {
                Log.i("info", "Request no successfully added to server...");
            }
        }catch (Exception e){
            e.getStackTrace();
        }

    }

    /**
     * a synchronized method for removing a request from the current request list as well as
     * Elastic Search see deleteRequestById() in ESC
     * @see ElasticSearchController
     * @param request
     * @return Boolean
     */
    public synchronized Boolean removeRequest(Request request){
        Log.i("info", "RequestSingleton removeRequest()");
        if(!requests.contains(request)) {
            Log.i("info", "The request singleton doesn't have this request");
            return false;
        }

        ElasticSearchController.DeleteRequestTask deleteRequestTask = new ElasticSearchController.DeleteRequestTask();
        deleteRequestTask.execute(request);

        Boolean deleted = false;
        try {
            deleted = deleteRequestTask.get();
            if(deleted) {
                Log.i("info", "Request successfully removed from the server");
                requests.remove(request);
            }
        } catch (Exception e) {
            Log.i("Error", "Failed to get " + userController.getActiveUser().getUserId() + "'s ID.");
        } finally {
            return deleted;
        }
    }
    // TODO: 2016-10-29 Check for duplicate requests from the same user.
}
