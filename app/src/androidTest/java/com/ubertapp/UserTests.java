package com.ubertapp;


import com.ubertapp.Models.User;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * All User class tests.
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
}
