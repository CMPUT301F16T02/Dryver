package com.ubertapp;


import com.ubertapp.Models.Rider;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * All Tests for the Rider Class.
 */

public class RiderTests {

    private final String DEFAULT_RIDER_ID = "b8sjd9sl_28sjd2u";

    /**
     * Tests the getId() function from Rider class.
     */
    @Test
    public void testGetId() {
        Rider rider = new Rider(DEFAULT_RIDER_ID);
        assertEquals(DEFAULT_RIDER_ID, rider.getUserId());
    }
}
