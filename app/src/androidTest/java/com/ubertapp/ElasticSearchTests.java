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

package com.ubertapp;


import android.text.format.Time;
import android.util.Log;
import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.User;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.ubertapp.Controllers.ElasticSearchController.GetUsersTask;


public class ElasticSearchTests {

    // Not sure how we are going to be implementing these ones yet
    // TODO: Implement other tests for dealing with the server when the class evolves.

    @Test
    public void testAddUser() {
        // Add user to database, then gets the user from the new elastic search id
        // and comapares the user in the db to the original
        User user = new User("123", "Cole", "Mackenzie", "7805555555", "123@gmail.com");
        ElasticSearchController elasticSearch = ElasticSearchController.getInstance();

        User result = elasticSearch.getUserByID(user.getId());
        assert(result.equals(user));

        elasticSearch.addUser(user);

        result = elasticSearch.getUserByID(user.getId());
        assert(result.equals(user));
    }

    @Test
    public void testDeleteUser() {
        User user = new User("321", "NotCole", "SomeLastName", "780555555","notarealemail@noreply.com");
        ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();
        User result = elasticSearchController.getUserByID(user.getId());
        assert(result.equals(user));
        elasticSearchController.addUser(user);

        result = elasticSearchController.getUserByID(user.getId());
        assert(result.equals(user));

        // User has been added, now delete them.

        elasticSearchController.deleteUser(user); // Send delete request
        result = elasticSearchController.getUserByID(user.getId()); //
        assertNull("User was not null", result);
    }

    @Test
    public void testUpdateUser() {
        ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();

        // Create user
        User user =  new User("587");
        elasticSearchController.addUser(user);
        while(user.getId() == null) {
            System.out.println("Still null");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotNull("Id was null", user.getId());

        // Get the user from the DB
        User result = elasticSearchController.getUserByEsID(user.getId());
        while(result.getId() == null) {
            System.out.println("Still null");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals("ID did not match", user.getId(), result.getId()); // Ensure they match

        // Update the local user
        user.setEmail("test@test.com");
        user.setFirstName("Updated name");
        user.setLastName("Updated lastname");
        user.setPhoneNumber("0000000000");

        // Post update
        elasticSearchController.updateUser(user);

        // Get result
        User updatedUser = elasticSearchController.getUserByEsID(user.getId());
        while(result.getId() == null) {
            System.out.println("Still null");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Check that they match
        assertEquals("Email not equal", user.getEmail(), updatedUser.getEmail());
        assertEquals("Name not equal", user.getFirstName(), updatedUser.getFirstName());
        assertEquals("Lastname not equal", user.getLastName(), updatedUser.getLastName());
        assertEquals("Phone not equal", user.getPhoneNumber(), updatedUser.getPhoneNumber());
    }

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

    @Test
    public void testFetchRequest() {
        // TODO: test fetching from server
    }

    @Test
    public void testPushRequest() {
        // TODO: test pushing to server
    }
}
