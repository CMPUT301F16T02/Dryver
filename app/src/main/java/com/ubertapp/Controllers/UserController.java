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

import android.app.Activity;
import android.content.Intent;

import com.ubertapp.Activities.ActivityUserProfile;
import com.ubertapp.Models.User;

/**
 * Controller for the Current user, as well as the user currently being viewed.
 * Follows the Singleton design pattern, allows user to view ActivityUserProfile.
 */
public class UserController {
    private static UserController instance = new UserController();
    private ElasticSearchController ES = ElasticSearchController.getInstance();

    private User activeUser;
    private User viewedUser;

    public static UserController getInstance() {
        return instance;
    }

    private UserController() {
    }

    public User getActiveUser() {
        return activeUser;
    }

    public User getViewedUser() {
        return viewedUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void viewActiveUserProfile(Activity currentActivity) {
        viewUserProfile(currentActivity, activeUser);
    }

    public void viewUserProfile(Activity currentActivity, User user) {
        viewedUser = user;
        Intent intent = new Intent(currentActivity, ActivityUserProfile.class);
        currentActivity.startActivity(intent);
    }

    public User login(String userid) {
        activeUser =  ES.getUserByID(userid);
        return activeUser;
    }

    public void logout() {
        activeUser = null;
        viewedUser = null;
    }
}
