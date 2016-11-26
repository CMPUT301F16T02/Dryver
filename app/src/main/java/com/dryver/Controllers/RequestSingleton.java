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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.dryver.Activities.ActivityDriverList;
import com.dryver.Activities.ActivityRequest;
import com.dryver.Activities.ActivityRequestSelection;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.RequestStatus;
import com.dryver.Models.Rider;
import com.dryver.Utility.ICallBack;
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
import java.util.Collections;
import java.util.Comparator;

/**
 * Request Singleton. Deals from providing request information to the caller.
 */
public class RequestSingleton {
    private static final String REQUESTS_SAV = "requests.json";
    private static RequestSingleton ourInstance = new RequestSingleton();
    private static ArrayList<Request> requests = new ArrayList<Request>();
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private UserController userController = UserController.getInstance();

    /**
     * The request passed on request selection or editing. Also used to make request. It is the request
     * of context at any given tim, given there is one.
     */
    private Request tempRequest;

    private RequestSingleton() {
    }

    public static RequestSingleton getInstance() {
        return ourInstance;
    }

    public void setRequestsAll() {
        requests = ES.getAllRequests();
//        loadRequests();
    }

    public ArrayList<Request> getRequests() {
//        loadRequests();
        return requests;
    }

    public Request getRequestById(String id) {
        for (Request req: requests) {
            if (req.getId().equals(id)) {
                return req;
            }
        }
        return null;
    }

    /**
     * Updates the requests and returns them. Gives an emoty callback
     *
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
     * A simple method for fetching an updated request list via Elastic Search. Executes callback after
     *
     * @param callBack
     * @sxee ICallBack
     * @see ElasticSearchController
     */
    public void updateRequests(ICallBack callBack) {
        Log.i("info", "RequestSingleton updateRequests()");

        //This is necessary as you can't remove from a list you are currently iterating through /facepalm
        ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();

        if (userController.getActiveUser() instanceof Rider) {
            ArrayList<Request> newRequests = ES.getRequests(userController.getActiveUser().getId());
            for (Request newRequest : newRequests) {
                if (!requests.contains(newRequest)) {
                    requests.add(newRequest);
                }
                for (Request oldRequest : requests) {
                    if (!newRequests.contains(oldRequest)) {
                        indicesToRemove.add(requests.indexOf(oldRequest));
                    }
                }
            }

            Collections.sort(indicesToRemove, Collections.<Integer>reverseOrder());
            for(int index : indicesToRemove){
                requests.remove(index);
            }


            saveRequests();
            callBack.execute();
        } else if (userController.getActiveUser() instanceof Driver) {
            requests = ES.getAllRequests();
            saveRequests();
            callBack.execute();
        }
        //TODO: Implement a way of searching for requests in a certain area or something for drivers
    }

    public void clearTempRequest() {
        tempRequest = null;
    }

    public Request getTempRequest() {
        return tempRequest;
    }

    public void pushTempRequest() {
        pushRequest(tempRequest);
        saveRequests();
    }

    /**
     * Opens the activity for viewing a request
     * @param context
     * @param request
     */
    public void viewRequest(Context context, Request request){
        tempRequest = request;
        Intent intent = new Intent(context, ActivityRequestSelection.class);
        context.startActivity(intent);
    }

    /**
     * opens the activity for editing or making a request
     * @param context
     * @param request
     */
    public void editRequest(Context context, Request request){
        tempRequest = request;
        Intent intent = new Intent(context, ActivityRequest.class);
        context.startActivity(intent);
    }

    /**
     * opens the activity for viewing a list of drivers
     * @param context
     * @param request
     */
    public void viewRequestDrivers(Context context, Request request){
        tempRequest = request;
        Intent intent = new Intent(context, ActivityDriverList.class);
        context.startActivity(intent);
    }

    /**
     * Updates a request if it's id matches, otherwise creates a brand new request.
     *
     * @param request the request
     */
    public void pushRequest(Request request) {
        if (ES.updateRequest(request)) {
            int position = requests.indexOf(request);
            requests.remove(position);
            requests.add(request);
        } else if (ES.addRequest(request)) {
            requests.add(request);
        }
        saveRequests();
    }

    /**
     * a method for removing a request from the current request list as well as
     * Elastic Search see deleteRequestById() in ESC
     *
     * @param request
     * @return Boolean
     * @see ElasticSearchController
     * @see ICallBack
     */
    public void removeRequest(Request request) {
        if (ES.deleteRequest(request)) {
            requests.remove(request);
        }
        saveRequests();
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
                return Double.compare(lhs.getCost(), rhs.getCost());
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
     *
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
     *
     * @param request
     * @param driverID
     */
    public void selectDriver(Request request, String driverID) {
        request.acceptOffer(driverID);
        request.setStatus(RequestStatus.DRIVER_SELECTED);
        ES.updateRequest(request);
    }

    // TODO: 2016-10-29 Check for duplicate requests from the same user.

    public double getEstimate() {
        Log.i("Calculating cost", "requestSingleton.getEstimate()");
        return tempRequest.getCost() + tempRequest.getDistance() * tempRequest.getRate();
    }

    /**
     * Saves the current ArrayList of requests to local storage
     */
    public void saveRequests() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), REQUESTS_SAV);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                Gson gson = new Gson();


                gson.toJson(requests, bufferedWriter);
                bufferedWriter.flush();

                fileOutputStream.close();
            } else {
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
     */
    public void loadRequests() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), REQUESTS_SAV);

                FileInputStream fileInputStream = new FileInputStream(file);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                Gson gson = new Gson();

                // Code from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
                Type listType = new TypeToken<ArrayList<Request>>() {
                }.getType();

                requests = gson.fromJson(bufferedReader, listType);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setDistance(double distance) {
        tempRequest.setDistance(distance);
    }

    //TODO Differentiate between Drivers/Accepted requests and Users/Requests made offline
}
