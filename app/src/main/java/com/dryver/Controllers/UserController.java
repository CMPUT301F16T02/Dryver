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

import com.dryver.Activities.ActivityEditProfile;
import com.dryver.Activities.ActivityViewProfile;
import com.dryver.Models.Driver;
import com.dryver.Models.User;

import java.util.concurrent.ExecutionException;

/**
 * Controller for the Current user, as well as the user currently being viewed.
 * Follows the Singleton design pattern, allows user to view ActivityEditProfile.
 */
public class UserController {
    private static UserController instance = new UserController();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    private User activeUser;
    private User viewedUser;

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
    }

    /**
     * View active user profile.
     *
     * @param currentActivity the current activity
     */
    public void viewActiveUserProfile(Activity currentActivity) {
        viewedUser = activeUser;
        viewUserProfile(currentActivity, activeUser);
    }

    /**
     * View user profile.
     *
     * @param currentActivity the current activity
     * @param user            the user
     */
    public void viewUserProfile(Activity currentActivity, User user) {
        viewedUser = user;
        if (user.equals(activeUser)) {
            Intent intent = new Intent(currentActivity, ActivityEditProfile.class);
            currentActivity.startActivity(intent);
        } else if (user instanceof Driver){
            Intent intent = new Intent(currentActivity, ActivityViewProfile.class);
            currentActivity.startActivity(intent);
        } else{
            //do something
        }


    }

    /**
     * Login the user by userid they provide in the text field.
     *
     * @param username the userid
     * @return the true or false based on login success
     */
    //TODO: Exceptions handled in the activity
    public boolean login(String username) throws ExecutionException, InterruptedException {
        return (activeUser = ES.getUserByString(username)) != null;
    }

    /**
     * Logout the active user.
     */
    public void logout() {
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
}
