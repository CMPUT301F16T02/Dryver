/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.ClassTests;


import android.location.Location;
import android.util.Log;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Models.Request;
import com.dryver.Models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

/**
 * Various Tests for the ElasticSearchController
 *
 * @see ElasticSearchController
 */

public class ElasticSearchTests {
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    // USERS
    private final static String username = "ESTestUser";
    private final static User testUser = new User(username, "testFirst", "testLast", "7805555555", "test@test.com");

    // REQUESTS
    private final static Calendar calendar = Calendar.getInstance();
    private final static Location toLocation = new Location("to");
    private final static Location fromLocation = new Location("from");
    private final static Double cost = 12.00;
    private final static String requestId = "ESTestRequestUniqueID";
    private final static Request testRequest = new Request(username, calendar, toLocation, fromLocation, cost);

    /**
     * Tests Adding and then deleting a User from the Database
     *
     * @throws InterruptedException
     */
    @Test
    public void testAddDeleteUser() throws InterruptedException {
        //This is testing if we can successfully remove users from the elastic server
        assertFalse(ES.deleteUser(testUser));
        assertTrue(ES.addUser(testUser));
        Thread.sleep(2000);
        assertFalse(ES.addUser(testUser));
        assertTrue(ES.deleteUser(testUser));
        Thread.sleep(2000);
    }

    /**
     * Tests updating an existing user's values in the database
     *
     * @throws InterruptedException
     */
    @Test
    public void testUpdateUser() throws InterruptedException {
        //This is testing US 03.01.01, 03.02.01, 03.03.01
        //by ensuring the set and get methods are communicating with elastic search
        User user = new User(username);

        assertFalse(ES.updateUser(user));
        assertTrue(ES.addUser(user));

        User requestedUser = ES.getUserByString(user.getId());

        assertEquals(user.getFirstName(), requestedUser.getFirstName());
        assertEquals(user.getLastName(), requestedUser.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser.getEmail());

        user.setFirstName("testFirst");
        user.setLastName("testLast");
        user.setPhoneNumber("7805555555");
        user.setEmail("test@test.com");

        assertNotEquals(user.getFirstName(), requestedUser.getFirstName());
        assertNotEquals(user.getLastName(), requestedUser.getLastName());
        assertNotEquals(user.getPhoneNumber(), requestedUser.getPhoneNumber());
        assertNotEquals(user.getEmail(), requestedUser.getEmail());

        assertTrue(ES.updateUser(user));

        User requestedUser2 = ES.getUserByString(user.getId());
        assertEquals(user.getFirstName(), requestedUser2.getFirstName());
        assertEquals(user.getLastName(), requestedUser2.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser2.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser2.getEmail());

        ES.deleteUser(user);
        Thread.sleep(2000);

    }

    @Test
    public void testAddDeleteRequest() throws InterruptedException {
        //This tests US 01.04.01
        //Ensuring we can cancel and delete requests
        assertFalse(ES.deleteRequest(testRequest));
        assertTrue(ES.addRequest(testRequest));
        Thread.sleep(2000);
        assertFalse(ES.addRequest(testRequest));
        assertTrue(ES.deleteRequest(testRequest));
    }

    @Test
    public void testUpdateRequest() throws InterruptedException {
        //This test is used for many tests in the US 01.0X range
        //Ensuring we can change and update request information
        Request request = new Request(username, calendar, toLocation, fromLocation, cost);
        request.setId(requestId);
        ES.deleteRequest(request);
        assertTrue(ES.addRequest(request));

        Thread.sleep(2000);
        Request esRequest = ES.getRequestByString(request.getId());

        assertEquals(testRequest.getRiderId(), esRequest.getRiderId());
        assertEquals(testRequest.getToLocation().getLatitude(), esRequest.getToLocation().getLatitude());
        assertEquals(testRequest.getToLocation().getLongitude(), esRequest.getToLocation().getLongitude());
        assertEquals(testRequest.getFromLocation().getLatitude(), esRequest.getFromLocation().getLatitude());
        assertEquals(testRequest.getFromLocation().getLongitude(), esRequest.getFromLocation().getLongitude());
        assertEquals(testRequest.getCost(), esRequest.getCost());

        Location toLocation = new Location("To");
        toLocation.setLatitude(10);
        toLocation.setLongitude(10);
        Location fromLocation = new Location("From");
        fromLocation.setLatitude(10);
        fromLocation.setLongitude(10);

        esRequest.setToLocation(toLocation);
        esRequest.setFromLocation(fromLocation);
        esRequest.setCost(15.0);


        assertNotEquals(request.getToLocation().getLatitude(), esRequest.getToLocation().getLatitude());
        assertNotEquals(request.getToLocation().getLongitude(), esRequest.getToLocation().getLongitude());
        assertNotEquals(request.getFromLocation().getLatitude(), esRequest.getFromLocation().getLatitude());
        assertNotEquals(request.getFromLocation().getLongitude(), esRequest.getFromLocation().getLongitude());
        assertNotEquals(request.getCost(), esRequest.getCost());


        assertTrue(ES.updateRequest(esRequest));
        Thread.sleep(2000);
        Request esRequest2 = ES.getRequestByString(esRequest.getId());

        assertEquals(esRequest.getToLocation().getLatitude(), esRequest2.getToLocation().getLatitude());
        assertEquals(esRequest.getToLocation().getLongitude(), esRequest2.getToLocation().getLongitude());
        assertEquals(esRequest.getFromLocation().getLatitude(), esRequest2.getFromLocation().getLatitude());
        assertEquals(esRequest.getFromLocation().getLongitude(), esRequest2.getFromLocation().getLongitude());
        assertEquals(esRequest.getCost(), esRequest2.getCost());

        ES.deleteRequest(esRequest2);

        Thread.sleep(2000);
    }

    //This test doesnt do really anything yet, just wanted to make sure my method got a correct value (verified with postman)
    @Test
    public void testGetAllRequest() throws InterruptedException {
        ArrayList<Request> requests = ES.getAllRequests();

        Thread.sleep(3000);

        Log.i("infoAHHHHHHHHH", Integer.toString(requests.size()));
    }
}
