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
     * Test first last name.
     */
    @Test
    public void testFirstLastName() {
        String first = "John";
        String last = "Smith";
        User user = new User(DEFAULT_USER_ID, first, last);
        assertEquals(first, user.getFirstName());
        assertEquals(last, user.getLastName());
    }

    /**
     * Test email.
     */
    @Test
    public void testEmail() {
        User user = new User(DEFAULT_USER_ID);
        String email = "testing@gmail.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    /**
     * Test phone number.
     */
    @Test
    public void testPhoneNumber() {
        User user = new User(DEFAULT_USER_ID);
        String phone = "5879895565";
        user.setPhoneNumber(phone);
        assertEquals(phone, user.getPhoneNumber());
    }

    /**
     * Test user biography.
     */
    @Test
    public void testUserBio() {
        User user = new User(DEFAULT_USER_ID);
        String bio = "User sample bio text.";
        user.setUserBio(bio);
        assertEquals(bio, user.getUserBio());
    }

}

