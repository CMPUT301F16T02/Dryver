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

package com.ubertapp;


import android.location.Address;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;
import com.ubertapp.Models.Driver;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Locale;

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

    private final Rider DEFAULT_RIDER = new Rider(DEFAULT_RIDER_ID);


    /**
     * Tests request initialization. Essentially checks that all fields have valid values after a
     * request object has been created.
     */
    @Test
    public void testRequestInit() {
        DEFAULT_FROM_ADDRESS.setLatitude(53.523869);
        DEFAULT_FROM_ADDRESS.setLongitude(-113.526146);
        DEFAULT_TO_ADDRESS.setLatitude(53.548623);
        DEFAULT_TO_ADDRESS.setLongitude(-113.506537);
        Request request = new Request(DEFAULT_RIDER, date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        // test cost
        assertEquals(DEFAULT_COST, request.getCost(), 0.001);
        // test ids
        assertEquals(DEFAULT_RIDER_ID, request.getRiderId());
        // test driver list.
        assertEquals(0, request.getDrivers().size());
        //test getRider
        assertEquals(DEFAULT_RIDER, request.getRider());
    }

    /**
     * Tests adding a driver to the request by creating a request, adding a driver, and then
     * checking that the request's driver list contains the driver.
     */
    @Test
    public void testAddDriver() {
        Request request = new Request(DEFAULT_RIDER, date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(DEFAULT_DRIVER_ID);

        assertEquals(0, request.getDrivers().size());

        // check driver in list
        request.addDriver(driver);
        assertTrue(request.getDrivers().contains(driver));

        // check list count
        request.addDriver(driver);
        assertEquals(2, request.getDrivers().size());
    }

    /**
     * Test accept offer.
     */
    @Test
    public void testAcceptOffer() {
        Request request = new Request(DEFAULT_RIDER, date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(DEFAULT_DRIVER_ID);
        Driver driver2 = new Driver(DEFAULT_DRIVER_ID2);

        request.addDriver(driver);
        request.addDriver(driver2);

        request.acceptOffer(driver2);

        assertEquals(driver2.getUserId(), request.getAcceptedDriver().getUserId());
    }

    /**
     * Test cancelling offer.
     */
    @Test
    public void testCancelOffer() {
        Request request = new Request(DEFAULT_RIDER, date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);
        Driver driver = new Driver(DEFAULT_DRIVER_ID);

        request.addDriver(driver);
        request.acceptOffer(driver);
        request.cancelOffer();

        assertEquals(null, request.getAcceptedDriver());
    }

    /**
     * Test to/from-location's getters and setters.
     */
    @Test
    public void testToFromLocations() {
        Request request = new Request(DEFAULT_RIDER, date, DEFAULT_FROM_ADDRESS, DEFAULT_TO_ADDRESS, rate);

        Location someLocation = new Location("test");
        someLocation.setLatitude(12.0);
        someLocation.setLongitude(12.0);

        request.setToLocation(someLocation);
        request.setFromLocation(someLocation);

        assertEquals(someLocation, request.getToLocation());
        assertEquals(someLocation, request.getFromLocation());
    }
}
