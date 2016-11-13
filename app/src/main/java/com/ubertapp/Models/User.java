package com.ubertapp.Models;


import io.searchbox.annotations.JestId;

/**
 * User class. General class for any user of the UberTapp app. Note: a User can be both a Driver
 * and a Rider.
 */
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    // TODO: Implement phoneNumber correctly. Remember to change the test for it when you fix this.
    private String phoneNumber;
    private String userBio;
    @JestId
    private String id;
    // TODO: anything else for user info?

    /**
     * Instantiates a new User with unique id.
     *
     * @param userId the user id of the user.
     */
    public User(String userId) {
        this.userId = userId;
    }

    /**
     * Instantiates a new User with unique id, first name, and last name.
     *
     * @param userId    the user id
     * @param firstName the first name
     * @param lastName  the last name
     */
    public User(String userId, String firstName, String lastName,String phoneNumber, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Gets user id.
     *
     * @return the user id of the user.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        // Only for testing purposes?
        this.lastName = lastName;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets personal bio.
     *
     * @return the personal bio
     */
    public String getUserBio() {
        return userBio;
    }

    /**
     * Sets personal bio.
     *
     * @param personalBio the personal bio
     */
    public void setUserBio(String personalBio) {
        this.userBio = personalBio;
    }

    /**
     * Returns the ElasticSearch ID of the user
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the users id to the elastic search id created from adding user.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String returnRole() {
        return "unknown";
    }
}
