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

package com.dryver.ClassTests;


import android.location.Location;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Models.Request;
import com.dryver.Models.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

/**
 * Various Tests for the ElasticSearchController
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
    private final static Request testRequest = new Request(username, calendar, toLocation, fromLocation, cost);

    @AfterClass
    @BeforeClass
    public static void removeTestUsers() throws ExecutionException, InterruptedException {
        ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();
        elasticSearchController.deleteUser(testUser);
        elasticSearchController.deleteRequest(testRequest);
        Thread.sleep(2000);
    }


    /**
     * Tests Adding and then deleting a User from the Database
     * @throws InterruptedException
     */
    @Test
    public void testAddDeleteUser() {
        assertFalse(ES.deleteUser(testUser));
        assertTrue(ES.addUser(testUser));
        assertFalse(ES.addUser(testUser));
        assertTrue(ES.deleteUser(testUser));
    }

    /**
     * Tests updating an existing user's values in the database
     * @throws InterruptedException
     */
    @Test
    public void testUpdateUser() {
        User user =  new User(username);

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
    }

    @Test
    public void testAddDeleteRequest() throws InterruptedException {
        assertFalse(ES.deleteRequest(testRequest));
        assertTrue(ES.addRequest(testRequest));
        Thread.sleep(2000);
        assertFalse(ES.addRequest(testRequest));
        assertTrue(ES.deleteRequest(testRequest));
    }

    @Test
    public void testUpdateRequest() throws InterruptedException {
        assertTrue(ES.addRequest(testRequest));
        Thread.sleep(2000);
        Request esRequest = ES.getRequestByMatch(testRequest);

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
        esRequest.setCost(15);


        assertNotEquals(testRequest.getToLocation().getLatitude(), esRequest.getToLocation().getLatitude());
        assertNotEquals(testRequest.getToLocation().getLongitude(), esRequest.getToLocation().getLongitude());
        assertNotEquals(testRequest.getFromLocation().getLatitude(), esRequest.getFromLocation().getLatitude());
        assertNotEquals(testRequest.getFromLocation().getLongitude(), esRequest.getFromLocation().getLongitude());
        assertNotEquals(testRequest.getCost(), esRequest.getCost());


        assertTrue(ES.updateRequest(esRequest));
        Thread.sleep(2000);
        Request esRequest2 = ES.getRequestByMatch(esRequest);

        assertEquals(esRequest.getToLocation().getLatitude(), esRequest2.getToLocation().getLatitude());
        assertEquals(esRequest.getToLocation().getLongitude(), esRequest2.getToLocation().getLongitude());
        assertEquals(esRequest.getFromLocation().getLatitude(), esRequest2.getFromLocation().getLatitude());
        assertEquals(esRequest.getFromLocation().getLongitude(), esRequest2.getFromLocation().getLongitude());
        assertEquals(esRequest.getCost(), esRequest2.getCost());
    }
}
