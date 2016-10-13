package com.ubertapp.Models;

/**
 * The Rider class represents a rider. That meaning it can request rides, and then accept them from
 * an instance of the Driver class after specifying some parameters about the ride.
 */

public class Rider extends User {

    public Rider(String userId) {
        super(userId);
    }
}
