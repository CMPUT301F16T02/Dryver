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


import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Models.User;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

/**
 * Various Tests for the ElasticSearchController
 * @see ElasticSearchController
 */

public class ElasticSearchTests {
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private final static String username = "ESTestUser";
    private final static User testUser = new User(username, "testFirst", "testLast", "7805555555", "test@test.com");
    // TODO: 2016-11-13 remove sleep statements and replace with a wait on condition somehow.

    @AfterClass
    @BeforeClass
    public static void removeTestUsers() throws ExecutionException, InterruptedException {
        ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();
        elasticSearchController.deleteUser(testUser);
    }


    /**
     * Tests Adding and then deleting a User from the Database
     * @throws InterruptedException
     */
    @Test
    public void testAddDeleteUser() throws InterruptedException, ExecutionException {
        assertFalse(ES.deleteUser(testUser));
        assertTrue(ES.addUser(testUser));
        assertFalse(ES.addUser(testUser));
        assertTrue(ES.deleteUser(testUser));
    }

    /**
     * Tests updating an existing user's values in the database
     * @throws InterruptedException
     */
    @Test
    public void testUpdateUser() throws InterruptedException, ExecutionException {
        User user =  new User(username);

        assertFalse(ES.updateUser(user));
        assertTrue(ES.addUser(user));

        User requestedUser = ES.getUserByString(user.getId());

        assertEquals(user.getFirstName(), requestedUser.getFirstName());
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

        User requestedUser2 = ES.getUserByString(user.getId());
        assertEquals(user.getFirstName(), requestedUser2.getFirstName());
        assertEquals(user.getLastName(), requestedUser2.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser2.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser2.getEmail());
    }

//    /**
//     * Tests getting a user from the database
//     */
//    @Test
//    public void testGetUsers() {
//        ArrayList<User> userList = new ArrayList<User>();
//        // Tests to see if the connection to the ES server was established.
//        GetUsersTask getUsersTask = new GetUsersTask();
//        getUsersTask.execute("");
//        try {
//            userList = getUsersTask.get();
//        } catch (Exception e) {
//            Log.i("Error", "Failed to get the users.");
//        }
//    }

//    /**
//     * Tests getting a request from the database
//     */
//    @Test
//    public void testFetchRequest() {
//        // TODO: test fetching from server
//    }
//
//    /**
//     * Tests adding a request to the database
//     */
//    @Test
//    public void testPushRequest() {
//        // TODO: test pushing to server
//    }
}
