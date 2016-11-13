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

package com.ubertapp.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;
import com.ubertapp.Models.Request;
import com.ubertapp.Models.Rider;
import com.ubertapp.Models.User;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;

/**
 * Used to communicate with the Elasticsearch server
 * @see <a href="https://github.com/searchbox-io/Jest/tree/master/jest">Jest</a>
 * @see <a href="http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/cmput301f16t02/_search?pretty=true&q=*:*">list of users</a>
 */

public class ElasticSearchController {
    private static ElasticSearchController instance = new ElasticSearchController();

    protected ElasticSearchController(){
    }

    //used for setting the mock controller for testing purposes
    public static void setMock(ElasticSearchController ES){
        instance = ES;
    }

    public static ElasticSearchController getInstance(){
        return instance;
    }

    /**
     * Static instance of the client
     */
    private static JestDroidClient client;
    /**
     * String URL pointing to the server
     */
    private static String DATABASE_URL = "http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/";
    /**
     * The primary index name
     */
    private static final String INDEX = "cmput301f16t02";
    private static final String USER = "user";
    private static final String REQUEST = "request";

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
            client.execute(new CreateIndex.Builder(INDEX).build());
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
    public boolean addUser(User user) {
        verifySettings();

        if (getUser(user.getUserId()) != null) {
            return false;
        }

        Index index = new Index.Builder(user).index(INDEX).type(USER).build();

        try {
            DocumentResult result = client.execute(index);
            if (result.isSucceeded()) {
                user.setId(result.getId());
            } else {
                Log.i("Error", "Elastic search was not able to add the user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    /**
     * Deletes a user in the database based on the userId.
     * @param user
     * @see User
     */
    public boolean deleteUser(User user) {
        User newUser = getUser(user.getUserId());
        return deleteUserByEsID(newUser);
    }

    /**
     * Deletes a user in the database based on the user id.
     * @param user
     * @see User
     */
    public boolean deleteUserByEsID(User user) {
        verifySettings();
        boolean deletable = false;

        if (user == null) {
            return deletable;
        }

        User internalUser = getUser(user.getUserId());
        if (internalUser == null || internalUser.getId() == null) {
            return deletable;
        }

        Delete delete = new Delete.Builder(user.getId()).index(INDEX).type(USER).build();

        try {
            client.execute(delete);
            deletable = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return deletable;
        }
    }

    /**
     * Gets a user based on the users' user id
     *
     * @param id
     * @return User
     * @see User
     */
    public User getUser(String id) {
        Log.i("Info", "logging in with user id: " + id);

        String search_string = "{\"from\": 0, \"size\": 10000, \"query\": {\"match\": {\"userId\": \"" + id + "\"}}}";

        verifySettings();
        Search search = new Search.Builder(search_string)
                .addIndex(INDEX)
                .addType(USER)
                .build();

        Log.i("info", "Searching using " + search_string.toString());

        User user = null;
        try {
            JestResult result = client.execute(search);
            user = result.getSourceAsObject(User.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Gets a user based on their ES ID set by jest-droid
     * @param id
     * @see User
     * @see JestDroidClient
     */
    public User getUserByEsID(String id) {
        Get get = new Get.Builder(INDEX, id).type(USER).build();

        JestResult result = null;
        try {
            result = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = result.getSourceAsObject(User.class);
        return user;
    }

    /**
     * Updates a existing user profile based on the ES id
     * @see User
     * */
    public boolean updateUser(User user) throws InterruptedException {
        verifySettings();

        if (getUser(user.getUserId()) == null) {
            return false;
        }

        Index index = new Index.Builder(user).index(INDEX).type(USER).id(user.getId()).build();

        try {
            DocumentResult result = client.execute(index);
            if (result.isSucceeded()) {
                user.setId(result.getId());
                return true;
            } else {
                Log.i("Error", "Elastic search was not able to add the user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    public boolean addRequest(Request request) {
        verifySettings();

        Index index = new Index.Builder(request).index(INDEX).type(REQUEST).build();

        try {
            DocumentResult result = client.execute(index);
            if (result.isSucceeded()) {
                request.setId(result.getId());
            } else {
                Log.i("Error", "Elastic search was not able to add the user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    public boolean deleteRequestByEsID(Request request) {
        verifySettings();
        boolean deletable = false;

        if (request == null) {
            return deletable;
        }

        Delete delete = new Delete.Builder(request.getId()).index(INDEX).type(REQUEST).build();

        try {
            DocumentResult result = client.execute(delete);
            deletable = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return deletable;
        }
    }

    public List<Request> getRiderRequests(Rider rider) {

        String search_string = "{\"from\": 0, \"size\": 10000, \"query\": {\"match\": {\"riderId\": \"" + rider.getUserId() + "\"}}}";

        verifySettings();
        Search search = new Search.Builder(search_string)
                .addIndex(INDEX)
                .addType(REQUEST)
                .build();

        List<Request> requests = new ArrayList<Request>();
        try {
            JestResult result = client.execute(search);
            requests = result.getSourceAsObjectList(Request.class);
            JsonObject resultObj = result.getJsonObject();
            JsonArray hitsArray =  resultObj
                    .get("hits")
                    .getAsJsonObject()
                    .get("hits")
                    .getAsJsonArray();

            for (int i = 0; i < hitsArray.size(); i++) {
                requests.get(i).setId(hitsArray.get(i).getAsJsonObject().get("_id").toString().replace("\"", ""));
            }


            return requests;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requests;
    }

//    public boolean updateRequest(Request request) throws InterruptedException {
//        verifySettings();
//
//        Index index = new Index.Builder(request).index(INDEX).type(REQUEST).id(request.getId()).build();
//
//        try {
//            DocumentResult result = client.execute(index);
//            if (result.isSucceeded()) {
//                request.setId(result.getId());
//                return true;
//            } else {
//                Log.i("Error", "Elastic search was not able to add the user.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            return true;
//        }
//    }

    //TODO: getDriverRequests

    /**
     * Used to get a list of users.
     */
    public static class GetUsersTask extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();
            ArrayList<User> users = new ArrayList<User>();

            Search search = new Search.Builder(search_parameters[0]).addIndex(INDEX).addType(USER).build();

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
