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

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import com.dryver.Activities.ActivityEditProfile;
import com.dryver.Activities.ActivityViewProfile;
import com.dryver.Models.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

/**
 * Controller for the Current user, as well as the user currently being viewed.
 * Follows the Singleton design pattern, allows user to view ActivityEditProfile.
 */
public class UserController {
    private static String ACTIVE_USER_SAV = "active_user.json";
    private static UserController instance = new UserController();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    private User activeUser;
    private User viewedUser;
    private boolean cached = false;

    /**
     * Gets instance of the User.
     *
     * @return the instance
     */
    public static UserController getInstance() {
        return instance;
    }

    private UserController() {}

    /**
     * Gets active user.
     *
     * @return the active user
     */
    public User getActiveUser() {
        return activeUser;
    }

    /**
     * Gets viewed user.
     *
     * @return the viewed user
     */
    public User getViewedUser() {
        return viewedUser;
    }

    /**
     * Sets active user.
     *
     * @param activeUser the active user
     */
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        saveUser();
    }

    /**
     * A method for viewing and editing your own profile
     *
     * @param currentActivity the current activity
     */
    public void editUserProfile(Activity currentActivity) {
        viewedUser = activeUser;
        Intent intent = new Intent(currentActivity, ActivityEditProfile.class);
        currentActivity.startActivity(intent);
    }

    /**
     * a method for viewing others' profiles
     * @param user
     * @param currentActivity
     */
    public void viewUserProfile(User user, Activity currentActivity){
        viewedUser = user;
        Intent intent = new Intent(currentActivity, ActivityViewProfile.class);
        currentActivity.startActivity(intent);
    }

    /**
     * Login the user by userid they provide in the text field.
     *
     * @param username the userid
     * @return the true or false based on login success
     */
    //TODO: Exceptions handled in the activity
    public boolean login(String username) throws ExecutionException, InterruptedException {
        if ((activeUser = ES.getUserByString(username)) != null) {
            saveUser();
        };
        return (activeUser) != null;
    }

    /**
     * Logout the active user.
     */
    public void logout() {
        deleteFile();
        activeUser = null;
        viewedUser = null;
    }

    /**
     * updates the user, is called by the saveButton onclick listener in UserProfile
     * @return
     */
    public boolean updateActiveUser(){
        return ES.updateUser(activeUser);
    }

    /**
     * Save the current active user's profile to the local storage
     * */
    public void saveUser() {
        try {
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), ACTIVE_USER_SAV);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                Gson gson = new Gson();
                gson.toJson(activeUser, bufferedWriter);
                bufferedWriter.flush();

                fileOutputStream.close();
            }
            else {
                throw new IOException("External storage was not available!");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Load the saved user from file
     * http://stackoverflow.com/questions/7887078/android-saving-file-to-external-storage
     * @see User
     */
    public void loadUser() {
        try {
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                File file = new File(Environment.getExternalStorageDirectory(), ACTIVE_USER_SAV);

                FileInputStream fileInputStream = new FileInputStream(file);

                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(fileInputStream));

                Gson gson = new Gson();
                activeUser = gson.fromJson(bufferedReader, User.class);
                setCached(true);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes a local files (cached uses)
     */
    public void deleteFile() {
        try {
            boolean file = new File(ACTIVE_USER_SAV).delete();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        setCached(false);
    }


    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
