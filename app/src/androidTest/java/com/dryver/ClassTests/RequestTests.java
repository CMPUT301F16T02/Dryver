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
import android.support.test.runner.AndroidJUnit4;
import com.dryver.Models.Driver;
import com.dryver.Models.Request;
import com.dryver.Models.Rider;
import com.dryver.Models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * All Tests for the Request Class.
 */
@RunWith(AndroidJUnit4.class)
public class RequestTests {

    private double DEFAULT_COST = 1.00;

    private final String DEFAULT_DRIVER_ID = "a5d32d2s_21se2s2";
    private final String DEFAULT_DRIVER_ID2 = "a5d32d2s_21se2s3";
    private final String DEFAULT_RIDER_ID = "b8sjd9sl_28sjd2u";
    private final double rate = 0.5;

    private Location DEFAULT_TO_ADDRESS = new Location("from");
    private Location DEFAULT_FROM_ADDRESS = new Location("to");

    private final Calendar date = Calendar.getInstance();

    private User user1 = new User(DEFAULT_DRIVER_ID);
    private User user2 = new User(DEFAULT_DRIVER_ID2);
    private User user3 = new User(DEFAULT_RIDER_ID);

    private final Rider DEFAULT_RIDER = new Rider(user3);


    /**
     * Tests request initialization. Essentially checks that all fields have valid values after a
     * request object has been created.
     */
    @Test
    public void testRequestInit() {
        //Tests US 01.01.01, 01.02.01
        //A test for initializing a reuqest between rider and driver
        DEFAULT_FROM_ADDRESS.setLatitude(53.523869);
        DEFAULT_FROM_ADDRESS.setLongitude(-113.526146);
        DEFAULT_TO_ADDRESS.setLatitude(53.548623);
        DEFAULT_TO_ADDRESS.setLongitude(-113.506537);
        Request request = new Request(DEFAULT_RIDER.getId(), date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        // test cost
        assertEquals(DEFAULT_COST, request.getCost(), 0.001);
        // test ids
        assertEquals(DEFAULT_RIDER_ID, request.getRiderId());
        // test driver list.
        assertEquals(0, request.getDrivers().size());
        //test getRider

        assertEquals(DEFAULT_RIDER.getId(), request.getRiderId());
    }

    /**
     * Tests adding a driver to the request by creating a request, adding a driver, and then
     * checking that the request's driver list contains the driver.
     */
    @Test
    public void testAddDriver() {
        //This test is needed for everything in US 01.XX.XX
        //Makes sure handling our drivers works
        Request request = new Request(DEFAULT_RIDER.getId(), date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(user1);

        assertEquals(0, request.getDrivers().size());

        // check driver in list
        request.addDriver(driver.getId());
        assertTrue(request.getDrivers().contains(driver));

        // check list count
        request.addDriver(driver.getId());
        assertEquals(2, request.getDrivers().size());
    }

    /**
     * Test accept offer. US 01.08.01, 01.03.01, 02.01.01
     */
    @Test
    public void testAcceptOffer() {
        Request request = new Request(DEFAULT_RIDER.getId(), date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(user1);
        Driver driver2 = new Driver(user2);

        request.addDriver(driver.getId());
        request.addDriver(driver2.getId());

        request.acceptOffer(driver2.getId());

        assertEquals(driver2.getId(), request.getAcceptedDriverID());
    }

    /**
     * Test cancelling offer.
     */
    @Test
    public void testCancelOffer() {
        Request request = new Request(DEFAULT_RIDER.getId(), date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(user1);

        request.addDriver(driver.getId());
        request.acceptOffer(driver.getId());
        request.cancelOffer();

        assertEquals(null, request.getAcceptedDriverID());
    }

    /**
     * Test to/from-location's getters and setters.
     */
    @Test
    public void testToFromLocations() {
        Request request = new Request(DEFAULT_RIDER.getId(), date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);

        Location someLocation = new Location("test");
        someLocation.setLatitude(12.0);
        someLocation.setLongitude(12.0);

        request.setToLocation(someLocation);
        request.setFromLocation(someLocation);

        assertEquals(someLocation, request.getToLocation());
        assertEquals(someLocation, request.getFromLocation());
    }
}
