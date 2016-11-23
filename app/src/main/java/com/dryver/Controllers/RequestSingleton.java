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
import android.os.Environment;
import android.util.Log;

import com.dryver.Models.Driver;
import com.dryver.Utility.IBooleanCallBack;
import com.dryver.Utility.ICallBack;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.Models.Rider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Request Singleton. Deals from providing request information to the caller.
 */
public class RequestSingleton {
    private static String REQUESTS_SAV = "requests.json";
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

    /**
     * Updates the requests and returns them. Gives an emoty callback
     * @return ArrayList<Request>
     * @see Request
     * @see ICallBack
     */
    public ArrayList<Request> getUpdatedRequests() {
        updateRequests(new ICallBack() {
            @Override
            public void execute() {
            }
        });
        return requests;
    }

    /**
     * Gets the currently viewed request (I.E open in RequestSelection)
     * @return Request
     * @see Request
     * @see com.dryver.Activities.ActivityRequestSelection
     */
    public Request getViewedRequest() {
        return viewedRequest;
    }

    /**
     * Sets the currently viewed request (I.E open in RequestSelection)
     * @param viewedRequest
     * @see Request
     * @see com.dryver.Activities.ActivityRequestSelection
     */
    public void setViewedRequest(Request viewedRequest) {
        this.viewedRequest = viewedRequest;
    }

    /**
     * A simple method for fetching an updated request list via Elastic Search. Executes callback after
     * @param callBack
     * @see ElasticSearchController
     * @see ICallBack
     */
    public void updateRequests(ICallBack callBack) {
        Log.i("info", "RequestSingleton updateRequests()");

        if(userController.getActiveUser() instanceof Rider){
            ArrayList<Request> newRequests = ES.getRequests(userController.getActiveUser().getId());
            for(Request newRequest : newRequests){
                if(!requests.contains(newRequest)){
                    requests.add(newRequest);
                }
                for(Request oldRequest: requests){
                    if(!newRequests.contains(oldRequest)){
                        requests.remove(oldRequest);
                    }
                }
            }
            callBack.execute();
        } else if(userController.getActiveUser() instanceof Driver) {
            requests = ES.getAllRequests();
            callBack.execute();
        }
            //TODO: Implement a way of searching for requests in a certain area or something for drivers
    }

    /**
     * A method that adds a request to the current request list for the user as well as Elastic Search
     * @param riderID
     * @param date
     * @param fromLocation
     * @param toLocation
     * @param rate
     * @param callBack
     * @see ElasticSearchController
     * @see ICallBack
     */
    public void addRequest(String riderID, Calendar date, Location fromLocation, Location toLocation, double rate, IBooleanCallBack callBack) {
        Log.i("trace", "RequestSingleton.addRequest()");
        Request request = new Request(riderID, date, fromLocation, toLocation, rate);

        //TODO: Handle offline here. If it isn't added to ES...

        if (ES.addRequest(request)) {
            requests.add(request);
            callBack.success();
        } else {
            callBack.failure();
        }
    }

    /**
     * a method for removing a request from the current request list as well as
     * Elastic Search see deleteRequestById() in ESC
     * @see ElasticSearchController
     * @param request
     * @param callBack
     * @return Boolean
     * @see ICallBack
     */
    public void removeRequest(Request request, ICallBack callBack){
        Log.i("trace", "RequestSingleton.removeRequest()");
        if (ES.deleteRequest(request)) {
            requests.remove(request);
            callBack.execute();
        }
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

    /**
     * A Function for a Rider selecting a Driver and updating the request in ES
     * @param request
     * @param driverID
     */
    public void selectDriver(Request request, String driverID){
        request.acceptOffer(driverID);
        request.setStatus(RequestStatus.FINALIZED);
        ES.updateRequest(request);
    }

    /**
     * Updates the current viewed request. Called by ActivityDriverList to update the driver list
     * @param request
     * @param callBack
     * @see ICallBack
     */
    public void updateViewedRequest(Request request, ICallBack callBack){
        Log.i("trace", "RequestSingleton.updateViewedRequest()");
        Request updatedRequest = ES.getRequestByID(request.getId());
        if(updatedRequest != null){
            viewedRequest = updatedRequest;
            callBack.execute();
        }
    }
    // TODO: 2016-10-29 Check for duplicate requests from the same user.

    /**
     * Saves the current ArrayList of requests to local storage
     * */
    public void saveRequests() {
        try {
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), REQUESTS_SAV);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                Gson gson = new Gson();


                gson.toJson(requests, bufferedWriter);
                bufferedWriter.flush();

                fileOutputStream.close();
            }
            else {
                throw new IOException("External storage was not available!");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Loads all the requests from local storage
     * */
    public void loadRequests() {
        try {
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), REQUESTS_SAV);

                FileInputStream fileInputStream = new FileInputStream(file);

                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(fileInputStream));

                Gson gson = new Gson();

                // Code from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
                Type listType = new TypeToken<ArrayList<Request>>(){}.getType();

                requests = gson.fromJson(bufferedReader, listType);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true or false if there are cached requests.
     * */
    public boolean hasCacheRequests() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return new File(Environment.getExternalStorageDirectory(), REQUESTS_SAV).isFile();
        } else return false;
    }

    /**
     * Syncs all locally stored requests with the server.
     * */
    public void syncRequests() {
        //TODO Sync requests with ES and local storage. Should use timestamps for versioning.
    }

    //TODO Differentiate between Drivers/Accepted requests and Users/Requests made offline
}
