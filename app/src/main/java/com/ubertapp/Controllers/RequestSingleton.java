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
