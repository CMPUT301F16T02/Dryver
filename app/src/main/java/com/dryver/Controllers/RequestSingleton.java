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
import com.dryver.Models.RequestStatus;
import com.dryver.Models.Rider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Request Singleton. Deals from providing request information to the caller.
 */
public class RequestSingleton {
    private static RequestSingleton ourInstance = new RequestSingleton();
    private Request viewedRequest;

    private static ArrayList<Request> requests = new ArrayList<Request>();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    public static RequestSingleton getInstance() {
        return ourInstance;
    }

    private RequestSingleton() {}

    public void setRequestsAll(){
        requests = ES.getAllRequests();
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public ArrayList<Request> getUpdatedRequests() {
        updateRequests();
        return requests;
    }

    public Request getViewedRequest() {
        return viewedRequest;
    }

    public void setViewedRequest(Request viewedRequest) {
        this.viewedRequest = viewedRequest;
    }

    /**
     * A simple method for fetching an updated request list via Elastic Search
     * @see ElasticSearchController
     */
    private void updateRequests() {
        Log.i("info", "RequestSingleton updateRequests()");
        if(userController.getActiveUser() instanceof Rider){
            requests = ES.getRequests(userController.getActiveUser().getId());
        } else if(userController.getActiveUser() instanceof Driver){
            requests = ES.getAllRequests();
            //TODO: Implement a way of searching for requests in a certain area or something for drivers
        }
    }

    /**
     * A method that adds a request to the current request list for the user as well as Elastic Search
     * @param riderID
     * @param date
     * @param fromLocation
     * @param toLocation
     * @param rate
     * @see ElasticSearchController
     */
    public void addRequest(String riderID, Calendar date, Location fromLocation, Location toLocation, double rate) {
        Request request = new Request(riderID, date, fromLocation, toLocation, rate);

        //TODO: Handle offline here. If it isn't added to ES...

        if (ES.addRequest(request)) {
            requests.add(request);
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

        Boolean deleted;
        if (deleted = ES.deleteRequest(request)) {
            requests.remove(request);
        }
        return deleted;
    }

    /**
     * Function that sorts the request arraylist by request date by overriding the compare method anonymously
     */
    public void sortRequestByDate() {
        Collections.sort(requests, new Comparator<Request>() {
            @Override
            public int compare(Request lhs, Request rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
    }

    /**
     * Function that sorts the request arraylist by request cost by overriding the compare method anonymously
     */
    public void sortRequestByCost() {
        Collections.sort(requests, new Comparator<Request>() {
            @Override
            public int compare(Request lhs, Request rhs) {
                return Double.compare(lhs.getRate(), rhs.getRate());
            }
        });
    }

    /**
     * Function that sorts the request arraylist by request distance by overriding the compare method anonymously
     */
    public void sortRequestByDistance() {
        Collections.sort(requests, new Comparator<Request>() {
            @Override
            public int compare(Request lhs, Request rhs) {
                return Double.compare(lhs.getFromLocation().distanceTo(lhs.getToLocation()), rhs.getFromLocation().distanceTo(rhs.getToLocation()));
            }
        });
    }

    /**
     * Function that sorts the request arraylist by request date by overriding the compare method anonymously
     * @param currentLocation
     */
    public void sortRequestsByProximity(final Location currentLocation) {
        Collections.sort(requests, new Comparator<Request>() {
            @Override
            public int compare(Request lhs, Request rhs) {
                return Double.compare(lhs.getFromLocation().distanceTo(currentLocation), rhs.getFromLocation().distanceTo(currentLocation));
            }
        });
    }

    public void selectDriver(Request request, String driverID){
        request.setAcceptedDriverID(driverID);
        request.setStatus(RequestStatus.FINALIZED);
        ES.updateRequest(request);
    }
    // TODO: 2016-10-29 Check for duplicate requests from the same user.
}
