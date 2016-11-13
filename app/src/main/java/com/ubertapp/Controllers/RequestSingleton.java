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

package com.ubertapp.Controllers;

import android.location.Location;
import android.util.Log;

import com.ubertapp.Models.Driver;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;

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

    private RequestSingleton() {
    }

    public ArrayList<Request> getRequests() {
        if(userController.getActiveUser() instanceof Rider){
            requests =  new ArrayList<Request>(ES.getRiderRequests((Rider)userController.getActiveUser()));
        } else if(userController.getActiveUser() instanceof Driver){
            //TODO: Implement a way of searching for requests in a certain area or something for drivers
        }
        return requests;
    }

    //TODO Correct Times... Why is date passed and not used?
    public void addRequest(Rider rider, Calendar date, Location fromLocation, Location toLocation, double rate) {
        Request request = new Request(rider, Calendar.getInstance(), fromLocation, toLocation, rate);

        //TODO: Handle offline here. If it isn't added to ES...
        if(ES.addRequest(request)) {
            Log.i("info", "Request Successfully added to server");
            requests.add(request);
        } else {
            Log.i("info", "Request no successfully added to server...");
        }
    }

    public void removeRequest(Request request)
    {
        if(!requests.contains(request)) {
            Log.i("info", "The request singleton doesn't have this request");
        } else if(ES.deleteRequestByEsID(request)){
            requests.remove(request);
        }
    }

    // TODO: 2016-10-29 Check for duplicate requests from the same user.
}
