package com.ubertapp;


import android.support.test.runner.AndroidJUnit4;
import com.ubertapp.Models.Driver;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * All Tests for the Request Class.
 */

@RunWith(AndroidJUnit4.class)
public class RequestTests {

    private double DEFAULT_COST = 1.00;

    private final String DEFAULT_DRIVER_ID = "a5d32d2s_21se2s2";
    private final String DEFAULT_RIDER_ID = "b8sjd9sl_28sjd2u";

    private final Rider DEFAULT_RIDER = new Rider(DEFAULT_RIDER_ID);


    /**
     * Tests request initialization. Essentially checks that all fields have valid values after a
     * request object has been created.
     */
    @Test
    public void testRequestInit() {
        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
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
        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
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
     * Tests offering a ride to the user. Essentially verifies that the driver's id is added
     * properly to the request
     * TODO: other verification to ensure offer happened.
     */
    @Test
    public void testOfferRide()
    {
        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
        Driver driver = new Driver(DEFAULT_DRIVER_ID);

        assertEquals(0, request.getDrivers().size());

        // check driver in list
        request.addDriver(driver);
        assertTrue(request.getDrivers().contains(driver));

        // check list count
        request.addDriver(driver);
        assertEquals(2, request.getDrivers().size());

        request.offerRide(driver);
        assertEquals(driver.getUserId(), request.getOfferingDriverID());
    }

    @Test
    public void testAcceptOffer()
    {
        //not quite sure how to test this yet
    }

//    @Test
//    public void testGetCost() {
//        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
////        Request r = new Request(10005.40, default_did, default_rid);
//        assertEquals(10005.40, request.getCost(), 0.001);
//    }

//    @Test
//    public void testGetaDriver() {
//        String did = "fg2swd";
//
//        Request r = new Request(10005.50, did, default_rid);
//
//        assertEquals("fg2swd", r.getDriverId());
//    }

//    @Test
//    public void testGetRider() {
//        String rid = "fg2swd";
//
//        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
//
//        assertEquals("fg2swd", r.getRiderId());
//    }
}
