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
    private static UserController ourInstance = new UserController();

    private User activeUser;
    private User viewedUser;

    public static UserController getInstance() {
        return ourInstance;
    }

    private UserController() {
    }

    public User getViewedUser() {
        return viewedUser;
    }

    public void viewActiveUserProfile(Activity currentActivity)
    {
        viewUserProfile(currentActivity, activeUser);
    }

    public void viewUserProfile(Activity currentActivity, User user)
    {
        viewedUser = user;
        Intent intent = new Intent(currentActivity, ActivityUserProfile.class);
        currentActivity.startActivity(intent);
    }

    public void logout()
    {
        activeUser = null;
        viewedUser = null;
    }
}
