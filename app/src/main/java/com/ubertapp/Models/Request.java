package com.ubertapp.Models;

/**
 * The Request class represents a request that was sent out by the rider. A Driver that is in the
 * drivers list can then offer a ride to the Rider who made the request. It is essentially a class
 * who's sole purpose is to commpunicate b/w the Driver and Rider class whild holding important
 * information such as ride cost etc...
 */

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The type Request.
 */
public class Request {
    private Rider rider;
    private Collection<Driver> drivers;
    private Driver acceptedDriver;

    private Location fromLocation;
    private Location toLocation;

    private final double cost;
    private final String riderId;

    /**
     * Instantiates a new Request.
     *
     * @param cost  the cost
     * @param rider the rider
     */
    public Request(double cost, Rider rider) {
        this.cost = cost;
        this.rider = rider;
        this.riderId = rider.getUserId();
        this.drivers = new ArrayList<Driver>();
        this.acceptedDriver = null;
    }

    /**
     * Gets cost.
     *
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Gets drivers.
     *
     * @return the drivers
     */
    public Collection<Driver> getDrivers() {
        return this.drivers;
    }

    /**
     * Gets rider.
     *
     * @return the rider
     */
    public Rider getRider() {
        return this.rider;
    }

    /**
     * Gets rider id.
     *
     * @return the rider id
     */
    public String getRiderId() {
        return rider.getUserId();
    }

    /**
     * Gets accepted driver.
     *
     * @return the accepted driver
     */
    public Driver getAcceptedDriver() {
        return acceptedDriver;
    }

    /**
     * Adds a driver to the list of drivers.
     *
     * @param driver the driver
     */
    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    /**
     * Accept driver offer to give rider a ride.
     *
     * @param driver the driver
     */
    public void acceptOffer(Driver driver) {
        if (drivers.contains(driver)) {
            this.acceptedDriver = driver;
        } else { // if somehow the driver is not in the collection...
            this.acceptedDriver = null;
        }
    }

    /**
     * Cancel rider's offer to driver.
     */
    public void cancelOffer() {
        this.acceptedDriver = null;
    }

    // ================= LOCATION SERVICES. ====================

    /**
     * Gets user's to-location.
     *
     * @return the to-location
     */
    public Location getToLocation() {
        return toLocation;
    }

    /**
     * Gets user's from-location.
     *
     * @return the from-location
     */
    public Location getFromLocation() {
        return fromLocation;
    }

    /**
     * Sets user's from-location.
     *
     * @param fromLocation the from location
     */
    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    /**
     * Sets user's to-location.
     *
     * @param toLocation the to location
     */
    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }
}
