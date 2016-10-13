package com.ubertapp;


import com.ubertapp.Models.User;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Test Suite for the User Class.
 */
public class UserTests {

    private double DEFAULT_COST = 1.00;
    private final String DEFAULT_USER_ID = "b8sjd9sl_28sjd2u";

    /**
     * Tests the getId() function from User class.
     */
    @Test
    public void testGetId() {
        User user = new User(DEFAULT_USER_ID);
        assertEquals(DEFAULT_USER_ID, user.getUserId());
    }

    /**
     * Test to-location's getters and setters.
     */
    @Test
    public void testToLocation() {
        // TODO write to-location test
    }

    /**
     * Test from-location's getters and setters.
     */
    @Test
    public void testFromLocation() {
        // TODO write from-location test
    }


}

