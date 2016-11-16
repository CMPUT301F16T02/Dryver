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

package com.dryver.ClassTests;


import android.util.Log;

import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Models.User;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import static com.dryver.Controllers.ElasticSearchController.GetUsersTask;

/**
 * Various Tests for the ElasticSearchController
 * @see ElasticSearchController
 */

public class ElasticSearchTests {
    ElasticSearchController ES = ElasticSearchController.getInstance();
    // TODO: 2016-11-13 remove sleep statements and replace with a wait on condition somehow.


    /**
     * Tests Adding and then deleting a User from the Database
     * @throws InterruptedException
     */
    @Test
    public void testAddDeleteUser() throws InterruptedException {
        User user = new User("ESTestUser", "testFirst", "testLast", "7805555555", "test@test.com");

        assertFalse(ES.deleteUser(user));
        assertTrue(ES.addUser(user));

        Thread.sleep(3000);
        assertTrue(ES.deleteUser(user));
        Thread.sleep(3000);
        assertNull(ES.getUser(user.getUserId()));
        Thread.sleep(3000);
    }

    /**
     * Tests updating an existing user's values in the database
     * @throws InterruptedException
     */
    @Test
    public void testUpdateUser() throws InterruptedException {
        User user =  new User("ESTestUser");
        assertFalse(ES.updateUser(user));

        ES.addUser(user);
        Thread.sleep(3000);
        User requestedUser = ES.getUser(user.getUserId());

        assertEquals(null, requestedUser.getFirstName());
        assertEquals(user.getLastName(), requestedUser.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser.getEmail());

        user.setFirstName("testFirst");
        user.setLastName("testLast");
        user.setPhoneNumber("7805555555");
        user.setEmail("test@test.com");

        assertNotEquals(user.getFirstName(), requestedUser.getFirstName());
        assertNotEquals(user.getLastName(), requestedUser.getLastName());
        assertNotEquals(user.getPhoneNumber(), requestedUser.getPhoneNumber());
        assertNotEquals(user.getEmail(), requestedUser.getEmail());

        assertTrue(ES.updateUser(user));
        Thread.sleep(3000);
        User requestedUser2 = ES.getUser(user.getUserId());
        assertEquals(user.getFirstName(), requestedUser2.getFirstName());
        assertEquals(user.getLastName(), requestedUser2.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser2.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser2.getEmail());

        ES.deleteUser(user);
        Thread.sleep(3000);
    }

    /**
     * Tests getting a user from the database
     */
    @Test
    public void testGetUsers() {
        ArrayList<User> userList = new ArrayList<User>();
        // Tests to see if the connection to the ES server was established.
        GetUsersTask getUsersTask = new GetUsersTask();
        getUsersTask.execute("");
        try {
            userList = getUsersTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get the users.");
        }
    }

    /**
     * Tests getting a request from the database
     */
    @Test
    public void testFetchRequest() {
        // TODO: test fetching from server
    }

    /**
     * Tests adding a request to the database
     */
    @Test
    public void testPushRequest() {
        // TODO: test pushing to server
    }
}
