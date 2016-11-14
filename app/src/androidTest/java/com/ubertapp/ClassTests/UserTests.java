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

package com.ubertapp.ClassTests;


import com.ubertapp.Models.User;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Test Suite for the User Class.
 * @see User
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
        String phone = "5555555555";
        String email = "jsmith@gmail.com";
        User user = new User(DEFAULT_USER_ID, first, last, phone, email);
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

