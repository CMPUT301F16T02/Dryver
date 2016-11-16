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

import com.dryver.Activities.ActivityUserProfile;
import com.dryver.Models.User;

/**
 * Controller for the Current user, as well as the user currently being viewed.
 * Follows the Singleton design pattern, allows user to view ActivityUserProfile.
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
        Intent intent = new Intent(currentActivity, ActivityUserProfile.class);
        currentActivity.startActivity(intent);
    }

    /**
     * Login the user by userid they provide in the text field.
     *
     * @param userid the userid
     * @return the true or false based on login success
     */
    public boolean login(String userid) {
        return (activeUser = ES.getUser(userid)) != null;
    }

    /**
     * Logout the active user.
     */
    public void logout() {
        activeUser = null;
        viewedUser = null;
    }
}
