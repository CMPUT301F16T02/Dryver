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
    private ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();

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
        activeUser =  elasticSearchController.getUserByID(userid);
        return activeUser;
    }

    public void logout() {
        activeUser = null;
        viewedUser = null;
    }
}
