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

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.dryver.Controllers.ElasticSearchController.GetUsersTask;

/**
 * Various Tests for the ElasticSearchController
 * @see ElasticSearchController
 */

public class ElasticSearchTests {
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private final static String username = "ESTestUser";
    private final static User testUser = new User(username, "testFirst", "testLast", "7805555555", "test@test.com");
    // TODO: 2016-11-13 remove sleep statements and replace with a wait on condition somehow.

    @BeforeClass
    public static void removeTestUsers() throws ExecutionException, InterruptedException {
        ElasticSearchController.GetUserByNameTask getUserByNameTask = new ElasticSearchController.GetUserByNameTask();
        getUserByNameTask.execute(username);

        ElasticSearchController.DeleteUserByIdTask deleteUserByIdTask = new ElasticSearchController.DeleteUserByIdTask();
        deleteUserByIdTask.execute(getUserByNameTask.get());

        deleteUserByIdTask = new ElasticSearchController.DeleteUserByIdTask();
        deleteUserByIdTask.execute(testUser);

    }


    /**
     * Tests Adding and then deleting a User from the Database
     * @throws InterruptedException
     */
    @Test
    public void testAddDeleteUser() throws InterruptedException, ExecutionException {
        User user = testUser;

        ElasticSearchController.DeleteUserByIdTask deleteUserByIdTask = new ElasticSearchController.DeleteUserByIdTask();
        deleteUserByIdTask.execute(user);
        assertFalse(deleteUserByIdTask.get());

        ElasticSearchController.AddUserTask addUserTask = new ElasticSearchController.AddUserTask();
        addUserTask.execute(user);
        assertTrue(addUserTask.get());


        deleteUserByIdTask.execute(user);
        assertTrue(deleteUserByIdTask.get());

        ElasticSearchController.GetUserByNameTask getUserByNameTask = new ElasticSearchController.GetUserByNameTask();
        getUserByNameTask.execute(user.getUserId());
        assertNull(getUserByNameTask.get());

    }

    /**
     * Tests updating an existing user's values in the database
     * @throws InterruptedException
     */
    @Test
    public void testUpdateUser() throws InterruptedException, ExecutionException {
        User user =  new User(username);

        ElasticSearchController.UpdateUserTask updateUserTask = new ElasticSearchController.UpdateUserTask();
        updateUserTask.execute(user);
        assertFalse(updateUserTask.get());

        ElasticSearchController.AddUserTask addUserTask = new ElasticSearchController.AddUserTask();
        addUserTask.execute(user);

        Thread.sleep(3000);

        ElasticSearchController.GetUserByNameTask getUserByNameTask = new ElasticSearchController.GetUserByNameTask();
        getUserByNameTask.execute(user.getUserId());
        User requestedUser = getUserByNameTask.get();

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

        updateUserTask = new ElasticSearchController.UpdateUserTask();
        updateUserTask.execute(user);
        assertTrue(updateUserTask.get());
        Thread.sleep(3000);

        getUserByNameTask = new ElasticSearchController.GetUserByNameTask();
        getUserByNameTask.execute(user.getUserId());
        User requestedUser2 = getUserByNameTask.get();
        assertEquals(user.getFirstName(), requestedUser2.getFirstName());
        assertEquals(user.getLastName(), requestedUser2.getLastName());
        assertEquals(user.getPhoneNumber(), requestedUser2.getPhoneNumber());
        assertEquals(user.getEmail(), requestedUser2.getEmail());

        ElasticSearchController.DeleteUserByIdTask deleteUserByIdTask = new ElasticSearchController.DeleteUserByIdTask();
        deleteUserByIdTask.execute(user);
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
