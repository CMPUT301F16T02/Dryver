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

package com.dryver.Models;

/**
 * The Request class represents a request that was sent out by the rider. A Driver that is in the
 * drivers list can then offer a ride to the Rider who made the request. It is essentially a class
 * who's sole purpose is to commpunicate b/w the Driver and Rider class whild holding important
 * information such as ride cost etc...
 */

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import io.searchbox.annotations.JestId;

/**
 * The type Request.
 */
public class Request implements Serializable {
    @JestId
    private String id;
    private final String riderId;
    private ArrayList<String> drivers;
    private String acceptedDriverID;
    private String description;
    private Calendar date;
    //Status: 0 for cancelled, 1 for Pending, 2 for Accepted
    private RequestStatus status;

    private SimpleCoordinates fromCoordinates;
    private SimpleCoordinates toCoordinates;

    private double cost;
    private double rate = 0.70;
    private double distance = 1.00;

    public Request(String riderId, Calendar date) {
        this.riderId = riderId;
        this.date = date;
        this.fromCoordinates = new SimpleCoordinates(0.0, 0.0, "from");
        this.toCoordinates = new SimpleCoordinates(0.0, 0.0, "to");
        this.drivers = new ArrayList<String>();
        this.acceptedDriverID = null;
        this.cost = 0;
        this.status = RequestStatus.NO_DRIVERS;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Instantiates a new Request.
     *
     * @param date         date the request was created
     * @param fromLocation location of the rider
     * @param toLocation   destination of the rider
     * @param cost         the rate
     */
    public Request(String riderId, Calendar date, Location fromLocation, Location toLocation, double cost) {
        this.riderId = riderId;
        this.date = date;
        this.fromCoordinates = new SimpleCoordinates(fromLocation.getLatitude(), fromLocation.getLongitude(), fromLocation.getProvider());
        this.toCoordinates = new SimpleCoordinates(toLocation.getLatitude(), toLocation.getLongitude(), toLocation.getProvider());
        this.drivers = new ArrayList<String>();
        this.acceptedDriverID = null;
        this.cost = cost;
        this.status = RequestStatus.NO_DRIVERS;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Gets elastic search user id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets description for a ride.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description for a ride.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets drivers who accepted the ride.
     *
     * @return the drivers
     */
    public ArrayList<String> getDrivers() {
        return this.drivers;
    }

    /**
     * Gets rider userId.
     *
     * @return the rider id
     */
    public String getRiderId() {
        return this.riderId;
    }

    /**
     * Adds a driver to the list of drivers.
     *
     * @param driver the driver
     */
    public void addDriver(String driver) {
        drivers.add(driver);
    }

    /**
     * Returns the accepted driver's ID
     *
     * @return String
     */
    public String getAcceptedDriverID() {
        return this.acceptedDriverID;
    }

    public boolean isAcceptedDriver(String driverID) {
        if (acceptedDriverID == null) {
            return false;
        }
        if (acceptedDriverID.equals(driverID)) {
            return true;
        }
        return false;
    }

    /**
     * Accapts an offer, and sets the accepted Driver
     *
     * @param driverID
     */
    public void acceptOffer(String driverID) {
        if (drivers.contains(driverID)) {
            this.acceptedDriverID = driverID;
        } else {
            this.acceptedDriverID = null;
        }
    }

    /**
     * Cancel rider's offer to driver.
     */
    public void cancelOffer() {
        this.acceptedDriverID = null;
    }

    /**
     * Gets user's to-location.
     *
     * @return the to-location
     */
    public Location getToLocation() {
        return toCoordinates.getLocation();
    }

    /**
     * Gets user's from-location.
     *
     * @return the from-location
     */
    public Location getFromLocation() {
        return fromCoordinates.getLocation();
    }

    /**
     * Sets user's from-location.
     *
     * @param fromLocation the from location
     */
    public void setFromLocation(Location fromLocation) {
        this.fromCoordinates.setLocation(fromLocation.getLatitude(), fromLocation.getLongitude());
        this.fromCoordinates.setLocationName(fromLocation.getProvider());
    }

    public void setFromCoordinatesName(String address) {
        this.fromCoordinates.setLocationName(address);
    }

    public void setToCoordinatesName(String address) {
        this.toCoordinates.setLocationName(address);
    }

    /**
     * Sets user's to-location.
     *
     * @param toLocation the to location
     */
    public void setToLocation(Location toLocation) {
        this.toCoordinates.setLocation(toLocation.getLatitude(), toLocation.getLongitude());
        this.toCoordinates.setLocationName(toLocation.getProvider());
    }

    /**
     * Get the status of the request
     *
     * @return int status
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Set the request status
     *
     * @param status the status
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * Get the request's date of creation
     *
     * @return Calendar date
     */
    public Calendar getDate() {
        return this.date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.rate = cost/(distance/1000);
        this.cost = cost;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Converts the status of the request to a string
     * 0 is Cancelled
     * 1 is Pending
     * 2 is Accepted
     *
     * @return String string
     */
    public String statusCodeToString() {
        if (status == RequestStatus.CANCELLED) {
            return "Cancelled";
        } else if (status == RequestStatus.NO_DRIVERS) {
            return "No Drivers Found";
        } else if (status == RequestStatus.DRIVERS_AVAILABLE) {
            return "Drivers Available";
        } else if (status == RequestStatus.DRIVER_CHOSEN) {
            return "Driver Chosen";
        } else if (status == RequestStatus.PAYMENT_AUTHORIZED) {
            return "Payment Authorized";
        } else if (status == RequestStatus.PAYMENT_ACCEPTED) {
            return "Request Complete!";
        } else {
            return "Unknown Status String";
        }
    }

    public boolean hasDriver(String driverID) {
        if (drivers.contains(driverID)) {
            return true;
        }
        return false;
    }

    public void removeDriver(String driverID) {
        drivers.remove(driverID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        return id != null ? id.equals(request.id) : request.id == null;

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

// REFERENCES
// http://stackoverflow.com/questions/1389736/how-do-i-create-a-unique-id-in-java
