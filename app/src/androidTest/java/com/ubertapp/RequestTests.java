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
    private final String DEFAULT_DRIVER_ID2 = "a5d32d2s_21se2s3";
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

    @Test
    public void testAcceptOffer() {
        Request request = new Request(DEFAULT_COST, DEFAULT_RIDER);
        Driver driver = new Driver(DEFAULT_DRIVER_ID);
        Driver driver2 = new Driver(DEFAULT_DRIVER_ID2);

        request.addDriver(driver);
        request.addDriver(driver2);

        request.acceptOffer(driver2);

        assertEquals(driver2.getUserId(), request.getAcceptedDriver().getUserId());

    }

    @Test
    public void testCancelOffer() {

    }

    @Test
    public void testGetToLocation() {

    }
}
