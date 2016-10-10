package com.ubertapp.Models;


/**
 * User class. General class for any user of the UberTapp app. Note: a User can be both a Driver
 *  and a Rider.
 */
public class User {
    // We might need the following for each user:
    // - email string
    // - first/last name

    private String userId;

    /**
     * Instantiates a new User.
     *
     * @param userId the user id of the user.
     */
    public User(String userId) {
        this.userId = userId;
    }

    /**
     * Gets user id.
     *
     * @return the user id of the user.
     */
    public String getUserId() {
        return userId;
    }

}
