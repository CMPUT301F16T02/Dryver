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


import android.util.Log;
import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.User;
import org.junit.Test;
import java.util.ArrayList;
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
