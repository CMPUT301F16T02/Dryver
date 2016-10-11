package com.ubertapp;


import com.ubertapp.Models.Driver;
import static org.junit.Assert.*;
import org.junit.Test;


public class DriverTests {

    private final String DEFAULT_DRIVER_ID = "a5d32d2s_21se2s2";


    @Test
    public void testGetId() {
        Driver driver = new Driver(DEFAULT_DRIVER_ID);
        assertEquals(DEFAULT_DRIVER_ID, driver.getUserId());
    }
}
