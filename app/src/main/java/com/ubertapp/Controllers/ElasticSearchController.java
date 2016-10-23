/*
 * Copyright (C) 2016
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

package com.ubertapp.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;
import com.ubertapp.Models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;

/**
 * Used to communicate with the Elasticsearch server
 */

public class ElasticSearchController {
    /**
     * Static instance of the client
     */
    private static JestDroidClient client;
    /**
     * String URL pointing to the server
     */
    private static String DATABASE_URL = "http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080";
    /**
     * The primary index name
     */
    private static String INDEX = "cmput301f16t02";


    /**
     * Called to verify the connection to the server. Creates a connection if it doesn't exist.
     * Implemented as a Singleton
     */
    private static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(DATABASE_URL);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }


        try {
            client.execute(new CreateIndex.Builder("cmput301f16t02").build());
        } catch (IOException e) {
            Log.d("ERROR", "Could not create index.");
            e.printStackTrace();
        }

    }

    /**
     * Adds a user to the database.
     *
     * @param user
     * @see User
     */
    public void addUser(User user) {
        verifySettings();

        Index index = new Index.Builder(user).index(INDEX).type("tweet").build();

        try {
            DocumentResult result = client.execute(index);
            if (result.isSucceeded()) {
                user.setId(result.getId());
            } else {
                Log.i("Error", "Elastic search was not able to add the user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets a user based on the users Elasticsearch id
     *
     * @param id
     * @return User
     * @see User
     */
    public User getUser(String id) {
        verifySettings();

        Get get = new Get.Builder(INDEX, id).type("user").build();

        User user = null;
        try {
            JestResult result = client.execute(get);
            user = result.getSourceAsObject(User.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Used to get a list of users.
     */
    public static class GetUsersTask extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();
            ArrayList<User> users = new ArrayList<User>();

            Search search = new Search.Builder(search_parameters[0]).addIndex(INDEX).addType("user").build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<User> foundUsers = result.getSourceAsObjectList(User.class);
                    users.addAll(foundUsers);
                } else {
                    Log.i("Error", "The search query failed to find users");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when communicating with the server!");

            }
            return users;
        }
    }
}
