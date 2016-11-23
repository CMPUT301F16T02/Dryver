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

import com.dryver.Utility.IBooleanCallBack;
import com.dryver.Utility.ICallBack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;
import com.dryver.Models.Request;
import com.dryver.Models.User;

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
 * @see <a href="https://github.com/searchbox-io/Jest/tree/master/jest">Jest</a>
 * @see <a href="http://ec2-35-160-201-101.us-west-2.compute.amazonaws.com:8080/cmput301f16t02/_search?pretty=true&q=*:*">list of users</a>
 */
public class ElasticSearchController {
    /**
     * The instance of the singleton that is returns and used by everyone.
     */
    private static ElasticSearchController instance = new ElasticSearchController();

    protected ElasticSearchController(){}

    /**
     * Slightly hacky workaround for setting an alternate instance of the ESController. Intended for setting
     * a mock for testing purposes. Hacky workaround for mocking a singleton hehe...
     * @param ES
     */
    public static void setMock(ElasticSearchController ES){
        instance = ES;
    }

    /**
     * gets the Instance of the ElasticSearchController as it is a singleton
     * @return ElasticSearchController
     */
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

    // ==============           USER               ===============

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

    /**
     * Adds a user to the database.
     *
     * @see User
     * @return Boolean
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
    // ==============           REQUEST             ===============

    public boolean addRequest(Request request) {
        Log.i("trace", "ElasticSearchController.addRequest()");
        AddRequestTask addTask = new AddRequestTask(request);
        addTask.execute();

        boolean added = false;
        try {
            added =  addTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            return added;
        }
    }

    public boolean deleteRequest(Request request) {
        Log.i("trace", "ElasticSearchController.deleteRequest()");
        DeleteRequestTask deleteTask = new DeleteRequestTask(request);
        deleteTask.execute();

        //you cant return in try catch >.>
        boolean deleted = false;
        try {
            deleted =  deleteTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            return deleted;
        }

    }

    public boolean deleteRequestByID(Request request) {
        Log.i("trace", "ElasticSearchController.deleteRequestByID()");
        if (getRequestByID(request.getId()) != null) {
            DeleteRequestTask deleteTask = new DeleteRequestTask(request);
            deleteTask.execute();
            return true;
        }
        return false;
    }

    public boolean deleteRequestByRiderID(Request request) {
        Log.i("trace", "ElasticSearchController.deleteRequestByRiderID()");
        Request testRequest;
        if ((testRequest = getRequestByRiderID(request)) != null) {
            DeleteRequestTask deleteTask = new DeleteRequestTask(request);
            deleteTask.execute();
            deleteRequest(request); //recursively delete all similar requests to remove redundancy.
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

    public Request getRequestByID(String requestID) {
        Log.i("trace", "ElasticSearchController.getRequestByID()");
        GetRequestTask getTask = new GetRequestTask();
        Request request = null;
        try {
            request = getTask.execute(requestID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return request;
    }

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
            for (Request rq: requestList) {
                Log.i("Request rider ID: " + rq.getRiderId(), "displaying rider id.");
                if (rq.equals(request)) {
                    return rq;
                }
            }
        }
        return null;
    }

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
            for (Request rq: requestList) {
                if (rq.getRiderId().equals(request.getRiderId())) {
                    return rq;
                }
            }
        }
        return null;
    }

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

    public ArrayList<Request> getAllRequests(){
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
        } finally{
            return requests;
        }
    }

    /**
     * A class for getting the list of requests associated with a rider on the Elastic Search server
     * @see Request
     * @return boolean
     */
    private static class AddRequestTask extends AsyncTask<Void, Void, Boolean> {
        private Request request;
        private boolean canAdd = true;

        AddRequestTask(Request request){
            this.request = request;
        }

        @Override
        protected void onPreExecute(){
            canAdd = getRequestByMatch(request) == null;
        }

        @Override
        protected Boolean doInBackground(Void... search_parameters) {
            if(!canAdd) return false;

            verifySettings();
            boolean addable = false;
            Index index = new Index.Builder(request).index(INDEX).type(REQUEST).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    request.setId(result.getId());
                    addable = true;
                } else {
                    Log.i("Error", "Elastic search was not able to add the user.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addable;
        }
    }

    /**
     * A class for deleting a request by it's ElasticSearch ID
     * @see Request
     * @return boolean
     */
    private static class DeleteRequestTask extends AsyncTask<Void, Void, Boolean> {
        private Request request;
        private boolean canDelete = true;

        DeleteRequestTask(Request request){
            this.request = request;
        }

        @Override
        protected void onPreExecute(){
            canDelete = getRequestByMatch(request) != null;
        }


        @Override
        protected Boolean doInBackground(Void... search_parameters) {
            verifySettings();

            if(!canDelete){
                return false;
            }

            boolean deleted = false;

            Delete delete = new Delete.Builder(request.getId()).index(INDEX).type(REQUEST).id(request.getId()).build();

            try {
                //TODO: use the result?
                DocumentResult result = client.execute(delete);
                if (result.isSucceeded()){
                    deleted = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return deleted;
            }
        }
    }

    /**
     *
     * @see Request
     * @return boolean
     * @throws InterruptedException
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
                }
                else {
                    Log.i("Error", "Elastic search was not able to add the user.");
                }
                updatable = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return updatable;
        }
    }

    /**
     * Adds a request to the ElasticSearch server
     * @see Request
     * @return boolean
     * */
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
                if(result.isSucceeded()){
                    requests.addAll(result.getSourceAsObjectList(Request.class));
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
    }

    //TODO: getDriverRequests

}