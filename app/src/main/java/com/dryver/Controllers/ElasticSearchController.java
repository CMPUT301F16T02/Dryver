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

package com.dryver.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.dryver.Models.Request;
import com.dryver.Models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;

/**
 * Used to communicate with the Elasticsearch server follows the Songleton design pattern.
 *
 * @see <a href="https://github.com/searchbox-io/Jest/tree/master/jest">Jest</a>
 * @see <a href="http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/cmput301f16t02/_search?pretty=true&q=*:*">list of users</a>
 */
public class ElasticSearchController {
    /**
     * The instance of the singleton that is returns and used by everyone.
     */
    private static ElasticSearchController instance = new ElasticSearchController();

    protected ElasticSearchController() {
    }

    /**
     * Slightly hacky workaround for setting an alternate instance of the ESController. Intended for setting
     * a mock for testing purposes. Hacky workaround for mocking a singleton hehe...
     *
     * @param ES
     */
    public static void setMock(ElasticSearchController ES) {
        instance = ES;
    }

    /**
     * gets the Instance of the ElasticSearchController as it is a singleton
     *
     * @return ElasticSearchController
     */
    public static ElasticSearchController getInstance() {
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

    /**
     * User type for server
     */
    private static final String USER = "user";

    /**
     * Request type for the server
     */
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

    // ==============           PUBLIC USER               ===============

    public boolean addUser(User user) {
        if (getUserByString(user.getId()) == null) {
            AddUserTask addTask = new AddUserTask();
            addTask.execute(user);
            return true;
        }
        return false;
    }

    public boolean deleteUser(User user) {
        if (getUserByString(user.getId()) != null) {
            DeleteUserTask deleteTask = new DeleteUserTask();
            deleteTask.execute(user);
            return true;
        }
        return false;
    }

    public boolean updateUser(User user) {
        if (getUserByString(user.getId()) != null) {
            AddUserTask addTask = new AddUserTask();
            addTask.execute(user);
            return true;
        }
        return false;
    }

    public User getUserByString(String username) {
        GetUserTask getTask = new GetUserTask();
        User temp = null;
        try {
            temp = getTask.execute(username).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    // ==============           PRIVATE USER               ===============

    /**
     * Adds a user to the database.
     *
     * @return Boolean
     * @see User
     */
    private static class AddUserTask extends AsyncTask<User, Void, Boolean> {
        @Override
        protected Boolean doInBackground(User... search_parameters) {
            verifySettings();

            boolean addable = false;
            Index index = new Index.Builder(search_parameters[0]).index(INDEX).type(USER).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    addable = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addable;
        }
    }

    /**
     * Deletes a user in the database based on the user id.
     *
     * @see User
     */
    private static class DeleteUserTask extends AsyncTask<User, Void, Boolean> {

        @Override
        protected Boolean doInBackground(User... search_parameters) {
            verifySettings();

            Delete delete = new Delete.Builder(search_parameters[0].getId()).index(INDEX).type(USER).id(search_parameters[0].getId()).build();

            try {
                client.execute(delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * Gets a user based on the users' user id asynchronously
     *
     * @return User
     * @see User
     */
    private static class GetUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... search_parameters) {
            verifySettings();
            Get get = new Get.Builder(INDEX, search_parameters[0]).type(USER).id(search_parameters[0]).build();

            User user = null;
            try {
                JestResult result = client.execute(get);
                user = result.getSourceAsObject(User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return user;
        }
    }

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

    // ==============           PUBLIC REQUEST             ===============

    public boolean addRequest(Request request) {
        if (getRequestByString(request.getId()) == null) {
            AddRequestTask addTask = new AddRequestTask();
            addTask.execute(request);
            return true;
        }
        return false;
    }

    public boolean deleteRequest(Request request) {
        if (getRequestByString(request.getId()) != null) {
            DeleteRequestTask deleteTask = new DeleteRequestTask();
            deleteTask.execute(request);
            return true;
        }
        return false;
    }

    public boolean updateRequest(Request request) {
        Log.i("trace", "ElasticSearchController.updateRequest()");
        Request tempRequest = request;
        if (tempRequest.getId() == null) {
            if ((tempRequest = getRequestByMatch(tempRequest)) != null) {
                UpdateRequestTask updateTask = new UpdateRequestTask();
                updateTask.execute(tempRequest);
                return true;
            } else {
                return false;
            }
        } else {
            UpdateRequestTask updateTask = new UpdateRequestTask();
            updateTask.execute(tempRequest);
            return true;
        }
    }

    /**
     * gets a request from ES via ES ID
     *
     * @param requestID
     * @return Request
     * @see Request
     * @see GetRequestTask
     */
    public Request getRequestByString(String requestID) {
        GetRequestTask getTask = new GetRequestTask();
        Request temp = null;
        try {
            temp = getTask.execute(requestID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * returns a matching request from ES. Often used to check if the request is a duplicate as it returns null
     * if the inputted request is not a duplicate.
     *
     * @param request
     * @return Request
     * @see Request
     * @see GetRequestTask
     */
    public static Request getRequestByMatch(Request request) {
        Log.i("trace", "ElasticSearchController.getRequestByMatch()");
        GetRequestsTask getTask = new GetRequestsTask();

        ArrayList<Request> requestList = new ArrayList<Request>();

        try {
            requestList = getTask.execute(request.getRiderId()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!requestList.isEmpty()) {
            for (Request rq : requestList) {
                Log.i("Request rider ID: " + rq.getRiderId(), "displaying rider id.");
                if (rq.equals(request)) {
                    return rq;
                }
            }
        }
        return null;
    }

    /**
     * gets a request for a given Rider using the Rider's ID
     *
     * @param request
     * @return Request
     * @see Request
     * @see GetRequestsTask
     */
    public Request getRequestByRiderID(Request request) {
        Log.i("trace", "ElasticSearchController.getRequestByRiderID()");
        GetRequestsTask getTask = new GetRequestsTask();

        ArrayList<Request> requestList = new ArrayList<Request>();

        try {
            requestList = getTask.execute(request.getRiderId()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!requestList.isEmpty()) {
            for (Request rq : requestList) {
                if (rq.getRiderId().equals(request.getRiderId())) {
                    return rq;
                }
            }
        }
        return null;
    }

    /**
     * gets All requests for a given rider using rider ID
     *
     * @param riderID
     * @return ArrayList<Request>
     * @see GetRequestsTask
     * @see Request
     * @see com.dryver.Models.Rider
     */
    public ArrayList<Request> getRequests(String riderID) {
        Log.i("trace", "ElasticSearchController.getRequests()");
        GetRequestsTask getTask = new GetRequestsTask();
        ArrayList<Request> requestList = new ArrayList<Request>();
        try {
            requestList = getTask.execute(riderID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return requestList;
    }

    /**
     * Gets all requests on the entirety of ES
     *
     * @return ArrayList<Request>
     * @see GetAllRequestsTask
     * @see Request
     */
    public ArrayList<Request> getAllRequests() {
        Log.i("trace", "ElasticSearchController.getAllRequests()");
        GetAllRequestsTask getAllRequestsTask = new GetAllRequestsTask();
        getAllRequestsTask.execute();

        ArrayList<Request> requests = new ArrayList<Request>();

        try {
            requests = getAllRequestsTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            return requests;
        }
    }

    // ==============           PRIVATE REQUEST             ===============

    private static class AddRequestTask extends AsyncTask<Request, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Request... search_parameters) {
            verifySettings();

            boolean addable = false;
            Index index = new Index.Builder(search_parameters[0]).index(INDEX).type(REQUEST).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    addable = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addable;
        }
    }

    private static class DeleteRequestTask extends AsyncTask<Request, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Request... search_parameters) {
            verifySettings();

            Delete delete = new Delete.Builder(search_parameters[0].getId()).index(INDEX).type(USER).id(search_parameters[0].getId()).build();

            try {
                client.execute(delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * @return boolean
     * @throws InterruptedException
     * @see Request
     */
    private static class UpdateRequestTask extends AsyncTask<Request, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Request... search_parameters) {
            verifySettings();
            boolean updatable = false;
            Index index = new Index.Builder(search_parameters[0]).index(INDEX).type(REQUEST).id(search_parameters[0].getId()).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    search_parameters[0].setId(result.getId());
                } else {
                    Log.i("Error", "Elastic search was not able to add the user.");
                }
                updatable = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return updatable;
        }
    }

    /**
     * Adds a request to the ElasticSearch server
     *
     * @return boolean
     * @see Request
     */
    private static class GetRequestTask extends AsyncTask<String, Void, Request> {

        @Override
        protected Request doInBackground(String... search_parameters) {
            verifySettings();
            Get get = new Get.Builder(INDEX, search_parameters[0]).type(REQUEST).id(search_parameters[0]).build();

            Request request = null;
            try {
                JestResult result = client.execute(get);
                request = result.getSourceAsObject(Request.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return request;
        }
    }

    private static class GetRequestsTask extends AsyncTask<String, Void, ArrayList<Request>> {
        @Override
        protected ArrayList<Request> doInBackground(String... search_parameters) {
            Log.i("trace", "GetRequestsTask.doInBackground()");
            String search_string = "{\"from\": 0, \"size\": 10000, \"query\": {\"match\": {\"riderId\": \"" + search_parameters[0] + "\"}}}";

            verifySettings();
            Search search = new Search.Builder(search_string)
                    .addIndex(INDEX)
                    .addType(REQUEST)
                    .build();

            ArrayList<Request> requests = new ArrayList<Request>();
            try {
                JestResult result = client.execute(search);
                if (result.isSucceeded()) {
                    requests.addAll(result.getSourceAsObjectList(Request.class));
                    JsonObject resultObj = result.getJsonObject();
                    JsonArray hitsArray = resultObj
                            .get("hits")
                            .getAsJsonObject()
                            .get("hits")
                            .getAsJsonArray();

                    for (int i = 0; i < hitsArray.size(); i++) {
                        requests.get(i).setId(hitsArray.get(i).getAsJsonObject().get("_id").toString().replace("\"", ""));
                    }


                    return requests;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return requests;
        }
    }

    private static class GetAllRequestsTask extends AsyncTask<Void, Void, ArrayList<Request>> {
        @Override
        protected ArrayList<Request> doInBackground(Void... search_parameters) {
            verifySettings();

            String search_string = "{\"query\": { \"match_all\": {}}}";

            verifySettings();
            Search search = new Search.Builder(search_string)
                    .addIndex(INDEX)
                    .addType(REQUEST)
                    .build();

            ArrayList<Request> requests = new ArrayList<Request>();
            try {
                JestResult result = client.execute(search);
                requests.addAll(result.getSourceAsObjectList(Request.class));
                JsonObject resultObj = result.getJsonObject();
                JsonArray hitsArray = resultObj
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
    }

    //TODO: getDriverRequests

}